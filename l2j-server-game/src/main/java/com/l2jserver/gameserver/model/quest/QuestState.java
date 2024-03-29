package com.l2jserver.gameserver.model.quest;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.enums.QuestType;
import com.l2jserver.gameserver.enums.audio.IAudio;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.instancemanager.TerritoryWarManager;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.AbstractScript;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.network.serverpackets.ExShowQuestMark;
import com.l2jserver.gameserver.network.serverpackets.QuestList;
import com.l2jserver.gameserver.network.serverpackets.TutorialCloseHtml;
import com.l2jserver.gameserver.network.serverpackets.TutorialEnableClientEvent;
import com.l2jserver.gameserver.network.serverpackets.TutorialShowQuestionMark;
import com.l2jserver.gameserver.util.Util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuestState {

  private static final Logger _log = Logger.getLogger(QuestState.class.getName());

  private final String questName;
  private final Quest quest;

  private final L2PcInstance player;

  private int state;

  /** A map of key->value pairs containing the quest state variables and their values */
  private Map<String, String> vars;

  /** boolean flag letting QuestStateManager know to exit quest when cleaning up */
  private boolean exitQuestOnCleanUp = false;

  /**
   * Constructor of the QuestState. Creates the QuestState object and sets the player's progress of
   * the quest to this QuestState.
   */
  public QuestState(Quest quest, L2PcInstance player, int state) {
    this.quest = quest;
    questName = quest.getName();
    this.player = player;
    this.state = state;

    player.setQuestState(this);
  }

  public L2PcInstance getPlayer() {
    return player;
  }

  /**
   * @return the current State of this QuestState
   * @see com.l2jserver.gameserver.model.quest.State
   */
  public int getState() {
    return state;
  }

  /**
   * @return {@code true} if the State of this QuestState is COMPLETED, {@code false} otherwise
   * @see com.l2jserver.gameserver.model.quest.State
   */
  public boolean isCompleted() {
    return (state == State.COMPLETED);
  }

  /**
   * Add parameter used in quests.
   *
   * @param var String pointing out the name of the variable for quest
   * @param val String pointing out the value of the variable for quest
   * @return String (equal to parameter "val")
   */
  public void setInternal(String var, String val) {
    if (vars == null) {
      vars = new HashMap<>();
    }

    if (val == null) {
      val = "";
    }

    vars.put(var, val);
  }

  public String set(String var, int val) {
    return set(var, Integer.toString(val));
  }

  /**
   * Return value of parameter "val" after adding the couple (var,val) in class variable "vars".<br>
   * Actions:<br>
   *
   * <ul>
   *   <li>Initialize class variable "vars" if is null.
   *   <li>Initialize parameter "val" if is null
   *   <li>Add/Update couple (var,val) in class variable Map "vars"
   *   <li>If the key represented by "var" exists in Map "vars", the couple (var,val) is updated in
   *       the database.<br>
   *       The key is known as existing if the preceding value of the key (given as result of
   *       function put()) is not null.<br>
   *       If the key doesn't exist, the couple is added/created in the database
   *       <ul>
   *
   * @param var String indicating the name of the variable for quest
   * @param val String indicating the value of the variable for quest
   * @return String (equal to parameter "val")
   */
  public String set(String var, String val) {
    if (vars == null) {
      vars = new HashMap<>();
    }

    if (val == null) {
      val = "";
    }

    String old = vars.put(var, val);
    if (old != null) {
      Quest.updateQuestVarInDb(this, var, val);
    } else {
      Quest.createQuestVarInDb(this, var, val);
    }

    if ("cond".equals(var)) {
      try {
        int previousVal;
        try {
          previousVal = Integer.parseInt(old);
        } catch (Exception ex) {
          previousVal = 0;
        }
        setCond(Integer.parseInt(val), previousVal);
      } catch (Exception e) {
        _log.log(
            Level.WARNING,
            player.getName()
                + ", "
                + getQuestName()
                + " cond ["
                + val
                + "] is not an integer.  Value stored, but no packet was sent: "
                + e.getMessage(),
            e);
      }
    }

    return val;
  }

  /**
   * Internally handles the progression of the quest so that it is ready for sending appropriate
   * packets to the client.<br>
   * <u><i>Actions :</i></u><br>
   *
   * <ul>
   *   <li>Check if the new progress number resets the quest to a previous (smaller) step.
   *   <li>If not, check if quest progress steps have been skipped.
   *   <li>If skipped, prepare the variable completedStateFlags appropriately to be ready for
   *       sending to clients.
   *   <li>If no steps were skipped, flags do not need to be prepared...
   *   <li>If the passed step resets the quest to a previous step, reset such that steps after the
   *       parameter are not considered, while skipped steps before the parameter, if any, maintain
   *       their info.
   * </ul>
   *
   * @param cond the current quest progress condition (0 - 31 including)
   * @param old the previous quest progress condition to check against
   */
  private void setCond(int cond, int old) {
    if (cond == old) {
      return;
    }

    int completedStateFlags = 0;
    // cond 0 and 1 do not need completedStateFlags. Also, if cond > 1, the 1st step must
    // always exist (i.e. it can never be skipped). So if cond is 2, we can still safely
    // assume no steps have been skipped.
    // Finally, more than 31 steps CANNOT be supported in any way with skipping.
    if ((cond < 3) || (cond > 31)) {
      unset("__compltdStateFlags");
    } else {
      completedStateFlags = getInt("__compltdStateFlags");
    }

    // case 1: No steps have been skipped so far...
    if (completedStateFlags == 0) {
      // check if this step also doesn't skip anything. If so, no further work is needed
      // also, in this case, no work is needed if the state is being reset to a smaller value
      // in those cases, skip forward to informing the client about the change...

      // ELSE, if we just now skipped for the first time...prepare the flags!!!
      if (cond > (old + 1)) {
        // set the most significant bit to 1 (indicates that there exist skipped states)
        // also, ensure that the least significant bit is an 1 (the first step is never skipped, no
        // matter
        // what the cond says)
        completedStateFlags = 0x80000001;

        // since no flag had been skipped until now, the least significant bits must all
        // be set to 1, up until "old" number of bits.
        completedStateFlags |= ((1 << old) - 1);

        // now, just set the bit corresponding to the passed cond to 1 (current step)
        completedStateFlags |= (1 << (cond - 1));
        set("__compltdStateFlags", String.valueOf(completedStateFlags));
      }
    }
    // case 2: There were exist previously skipped steps
    // if this is a push back to a previous step, clear all completion flags ahead
    else if (cond < old) {
      // note, this also unsets the flag indicating that there exist skips
      completedStateFlags &= ((1 << cond) - 1);

      // now, check if this resulted in no steps being skipped any more
      if (completedStateFlags == ((1 << cond) - 1)) {
        unset("__compltdStateFlags");
      } else {
        // set the most significant bit back to 1 again, to correctly indicate that this skips
        // states.
        // also, ensure that the least significant bit is an 1 (the first step is never skipped, no
        // matter
        // what the cond says)
        completedStateFlags |= 0x80000001;
        set("__compltdStateFlags", String.valueOf(completedStateFlags));
      }
    }
    // If this moves forward, it changes nothing on previously skipped steps.
    // Just mark this state and we are done.
    else {
      completedStateFlags |= (1 << (cond - 1));
      set("__compltdStateFlags", String.valueOf(completedStateFlags));
    }

    // send a packet to the client to inform it of the quest progress (step change)
    player.sendPacket(new QuestList());

    final Quest q = getQuest();
    if (!q.isCustomQuest() && (cond > 0)) {
      player.sendPacket(new ExShowQuestMark(q.getId()));
    }
  }

  /**
   * Insert (or update) in the database variables that need to stay persistent for this player after
   * a reboot. This function is for storage of values that are not related to a specific quest but
   * are global instead, i.e. can be used by any script.
   *
   * @param var the name of the variable to save
   * @param value the value of the variable
   */
  // TODO: these methods should not be here, they could be used by other classes to save some
  // variables, but they can't because they require to create a QuestState first.
  public void saveGlobalQuestVar(String var, String value) {
    try (var con = ConnectionFactory.getInstance().getConnection();
        var ps =
            con.prepareStatement(
                "REPLACE INTO character_quest_global_data (charId, var, value) VALUES (?, ?, ?)")) {
      ps.setInt(1, player.getObjectId());
      ps.setString(2, var);
      ps.setString(3, value);
      ps.executeUpdate();
    } catch (Exception e) {
      _log.log(
          Level.WARNING, "Could not insert player's global quest variable: " + e.getMessage(), e);
    }
  }

  /**
   * Read from the database a previously saved variable for this quest.<br>
   * Due to performance considerations, this function should best be used only when the quest is
   * first loaded.<br>
   * Subclasses of this class can define structures into which these loaded values can be saved.<br>
   * However, on-demand usage of this function throughout the script is not prohibited, only not
   * recommended.<br>
   * Values read from this function were entered by calls to "saveGlobalQuestVar".
   *
   * @param var the name of the variable whose value to get
   * @return the value of the variable or an empty string if the variable does not exist in the
   *     database
   */
  // TODO: these methods should not be here, they could be used by other classes to save some
  // variables, but they can't because they require to create a QuestState first.
  public String getGlobalQuestVar(String var) {
    String result = "";
    try (var con = ConnectionFactory.getInstance().getConnection();
        var ps =
            con.prepareStatement(
                "SELECT value FROM character_quest_global_data WHERE charId = ? AND var = ?")) {
      ps.setInt(1, player.getObjectId());
      ps.setString(2, var);
      try (var rs = ps.executeQuery()) {
        if (rs.next()) {
          result = rs.getString(1);
        }
      }
    } catch (Exception e) {
      _log.log(
          Level.WARNING, "Could not load player's global quest variable: " + e.getMessage(), e);
    }
    return result;
  }

  /**
   * Checks if the quest state progress ({@code cond}) is at the specified step.
   *
   * @param condition the condition to check against
   * @return {@code true} if the quest condition is equal to {@code condition}, {@code false}
   *     otherwise
   * @see #getInt(String var)
   */
  public boolean isCond(int condition) {
    return (getInt("cond") == condition);
  }

  /**
   * @param var the name of the variable to get
   * @return the integer value of the variable or 0 if the variable does not exist or its value is
   *     not an integer
   */
  public int getInt(String var) {
    if (vars == null) {
      return -1;
    }

    final String variable = vars.get(var);
    if ((variable == null) || variable.isEmpty()) {
      return -1;
    }

    int varint = -1;
    try {
      varint = Integer.parseInt(variable);
    } catch (NumberFormatException nfe) {
      _log.log(
          Level.INFO,
          "Quest "
              + getQuestName()
              + ", method getInt("
              + var
              + "), tried to parse a non-integer value ("
              + variable
              + "). Char Id: "
              + player.getObjectId(),
          nfe);
    }

    return varint;
  }

  public String getQuestName() {
    return questName;
  }

  public int getCond() {
    if (isStarted()) {
      return getInt("cond");
    }
    return 0;
  }

  /** @return {@code true} if the State of this QuestState is STARTED, {@code false} otherwise */
  public boolean isStarted() {
    return (state == State.STARTED);
  }

  /**
   * Sets the quest state progress ({@code cond}) to the specified step.
   *
   * @param value the new value of the quest state progress
   * @return this {@link QuestState} object
   * @see #set(String var, String val)
   * @see #setCond(int, boolean)
   */
  public QuestState setCond(int value) {
    if (isStarted()) {
      set("cond", Integer.toString(value));
    }
    return this;
  }

  /**
   * Check if a given variable is set for this quest.
   *
   * @param variable the variable to check
   * @return {@code true} if the variable is set, {@code false} otherwise
   * @see #get(String)
   * @see #getInt(String)
   * @see #getCond()
   */
  public boolean isSet(String variable) {
    return (get(variable) != null);
  }

  /**
   * @param var the name of the variable to get
   * @return the value of the variable from the list of quest variables
   */
  public String get(String var) {
    if (vars == null) {
      return null;
    }
    return vars.get(var);
  }

  /**
   * Sets the quest state progress ({@code cond}) to the specified step.
   *
   * @param value the new value of the quest state progress
   * @param playQuestMiddle if {@code true}, plays "ItemSound.quest_middle"
   * @return this {@link QuestState} object
   * @see #setCond(int value)
   * @see #set(String var, String val)
   */
  public QuestState setCond(int value, boolean playQuestMiddle) {
    if (!isStarted()) {
      return this;
    }
    set("cond", String.valueOf(value));

    if (playQuestMiddle) {
      AbstractScript.playSound(player, Sound.ITEMSOUND_QUEST_MIDDLE);
    }
    return this;
  }

  public boolean hasMemoState() {
    return getMemoState() > 0;
  }

  /** @return the current Memo State */
  public int getMemoState() {
    if (isStarted()) {
      return getInt("memoState");
    }
    return -1;
  }

  public QuestState setMemoState(int value) {
    set("memoState", String.valueOf(value));
    return this;
  }

  public boolean isMemoState(int memoState) {
    return (getInt("memoState") == memoState);
  }

  public String removeMemo() {
    return unset("memoState");
  }

  /**
   * Removes a quest variable from the list of existing quest variables.
   *
   * @param var the name of the variable to remove
   * @return the previous value of the variable or {@code null} if none were found
   */
  public String unset(String var) {
    if (vars == null) {
      return null;
    }

    String old = vars.remove(var);
    if (old != null) {
      Quest.deleteQuestVarInDb(this, var);
    }
    return old;
  }

  /**
   * Sets the memo state ex.
   *
   * @param slot the slot where the value will be saved
   * @param value the value
   * @return this QuestState
   */
  public QuestState setMemoStateEx(int slot, long value) {
    set("memoStateEx" + slot, String.valueOf(value));
    return this;
  }

  /**
   * Verifies if the given value is equal to the current memos state ex.
   *
   * @param slot the slot where the value was saved
   * @param memoStateEx the value to verify
   * @return {@code true} if the values are equal, {@code false} otherwise
   */
  public boolean isMemoStateEx(int slot, int memoStateEx) {
    return (getMemoStateEx(slot) == memoStateEx);
  }

  /**
   * Gets the memo state ex.
   *
   * @param slot the slot where the value was saved
   * @return the memo state ex
   */
  public int getMemoStateEx(int slot) {
    if (isStarted()) {
      return getInt("memoStateEx" + slot);
    }
    return 0;
  }

  /**
   * Add player to get notification of characters death
   *
   * @param character the {@link L2Character} object of the character to get notification of death
   */
  public void addNotifyOfDeath(L2Character character) {
    if (!(character instanceof L2PcInstance)) {
      return;
    }

    ((L2PcInstance) character).addNotifyQuestOfDeath(this);
  }

  /**
   * Return the quantity of one sort of item hold by the player
   *
   * @param itemId the Id of the item wanted to be count
   * @return long
   */
  public long getQuestItemsCount(int itemId) {
    return quest.getQuestItemsCount(player, itemId);
  }

  /**
   * @param itemId the Id of the item required
   * @return true if item exists in player's inventory, false - if not
   */
  public boolean hasQuestItems(int itemId) {
    return quest.hasQuestItems(player, itemId);
  }

  /**
   * @param itemIds list of items that are required
   * @return true if all items exists in player's inventory, false - if not
   */
  public boolean hasQuestItems(int... itemIds) {
    return quest.hasQuestItems(player, itemIds);
  }

  /**
   * Return the level of enchantment on the weapon of the player(Done specifically for weapon SA's)
   *
   * @param itemId Id of the item to check enchantment
   * @return int
   */
  public int getEnchantLevel(int itemId) {
    return quest.getEnchantLevel(player, itemId);
  }

  // TODO: This all remains because of backward compatibility, should be cleared when all scripts
  // are rewritten in java

  /** Give adena to the player */
  public void giveAdena(long count, boolean applyRates) {
    giveItems(Inventory.ADENA_ID, count, applyRates ? 0 : 1);
  }

  public void giveItems(int itemId, long count, int enchantlevel) {
    quest.giveItems(player, itemId, count, enchantlevel);
  }

  /** Give reward to player using multiplier's */
  public void rewardItems(ItemHolder item) {
    quest.rewardItems(player, item);
  }

  /** Give reward to player using multiplier's */
  public void rewardItems(int itemId, long count) {
    quest.rewardItems(player, itemId, count);
  }

  /** Give item/reward to the player */
  public void giveItems(int itemId, long count) {
    quest.giveItems(player, itemId, count, 0);
  }

  public void giveItems(ItemHolder holder) {
    quest.giveItems(player, holder.getId(), holder.getCount(), 0);
  }

  public void giveItems(int itemId, long count, byte attributeId, int attributeLevel) {
    quest.giveItems(player, itemId, count, attributeId, attributeLevel);
  }

  public boolean giveItemRandomly(
      int itemId, long amount, long limit, double dropChance, boolean playSound) {
    return quest.giveItemRandomly(
        player, null, itemId, amount, amount, limit, dropChance, playSound);
  }

  public boolean giveItemRandomly(
      L2Npc npc, int itemId, long amount, long limit, double dropChance, boolean playSound) {
    return quest.giveItemRandomly(
        player, npc, itemId, amount, amount, limit, dropChance, playSound);
  }

  public boolean giveItemRandomly(
      L2Npc npc,
      int itemId,
      long minAmount,
      long maxAmount,
      long limit,
      double dropChance,
      boolean playSound) {
    return quest.giveItemRandomly(
        player, npc, itemId, minAmount, maxAmount, limit, dropChance, playSound);
  }

  // TODO: More radar functions need to be added when the radar class is complete.
  // BEGIN STUFF THAT WILL PROBABLY BE CHANGED
  public void addRadar(int x, int y, int z) {
    player.getRadar().addMarker(x, y, z);
  }

  public void removeRadar(int x, int y, int z) {
    player.getRadar().removeMarker(x, y, z);
  }

  public void clearRadar() {
    player.getRadar().removeAllMarkers();
  }

  /**
   * Remove items from player's inventory when talking to NPC in order to have rewards.<br>
   * Actions:<br>
   *
   * <ul>
   *   <li>Destroy quantity of items wanted
   *   <li>Send new inventory list to player
   * </ul>
   *
   * @param itemId Identifier of the item
   * @param count Quantity of items to destroy
   */
  public void takeItems(int itemId, long count) {
    AbstractScript.takeItems(player, itemId, count);
  }

  /**
   * Add XP and SP as quest reward
   *
   * @param exp
   * @param sp
   */
  public void addExpAndSp(int exp, int sp) {
    AbstractScript.addExpAndSp(player, exp, sp);
  }

  /**
   * @param loc
   * @return number of ticks from GameTimeController
   */
  public int getItemEquipped(int loc) {
    return AbstractScript.getItemEquipped(player, loc);
  }

  /**
   * @return {@code true} if quest is to be exited on clean up by QuestStateManager, {@code false}
   *     otherwise
   */
  public boolean isExitQuestOnCleanUp() {
    return exitQuestOnCleanUp;
  }

  // END STUFF THAT WILL PROBABLY BE CHANGED

  /**
   * @param isExitQuestOnCleanUp {@code true} if quest is to be exited on clean up by
   *     QuestStateManager, {@code false} otherwise
   */
  public void setIsExitQuestOnCleanUp(boolean isExitQuestOnCleanUp) {
    exitQuestOnCleanUp = isExitQuestOnCleanUp;
  }

  /**
   * Start a timed event for a quest.<br>
   * Will call an event in onEvent/onAdvEvent.
   *
   * @param name the name of the timer/event
   * @param time time in milliseconds till the event is executed
   */
  public void startQuestTimer(String name, long time) {
    getQuest().startQuestTimer(name, time, null, player, false);
  }

  /** @return the {@link Quest} object of this QuestState */
  public Quest getQuest() {
    return QuestManager.getInstance().getQuest(questName);
  }

  /**
   * Start a timed event for a quest.<br>
   * Will call an event in onEvent/onAdvEvent.
   *
   * @param name the name of the timer/event
   * @param time time in milliseconds till the event is executed
   * @param npc the L2Npc associated with this event
   */
  public void startQuestTimer(String name, long time, L2Npc npc) {
    getQuest().startQuestTimer(name, time, npc, player, false);
  }

  /**
   * Start a repeating timed event for a quest.<br>
   * Will call an event in onEvent/onAdvEvent.
   *
   * @param name the name of the timer/event
   * @param time time in milliseconds till the event is executed/repeated
   */
  public void startRepeatingQuestTimer(String name, long time) {
    getQuest().startQuestTimer(name, time, null, player, true);
  }

  /**
   * Start a repeating timed event for a quest.<br>
   * Will call an event in onEvent/onAdvEvent.
   *
   * @param name the name of the timer/event
   * @param time time in milliseconds till the event is executed/repeated
   * @param npc the L2Npc associated with this event
   */
  public void startRepeatingQuestTimer(String name, long time, L2Npc npc) {
    getQuest().startQuestTimer(name, time, npc, player, true);
  }

  /**
   * @param name the name of the QuestTimer required
   * @return the {@link QuestTimer} object with the specified name or {@code null} if it doesn't
   *     exist
   */
  public QuestTimer getQuestTimer(String name) {
    return getQuest().getQuestTimer(name, null, player);
  }

  /**
   * Add a temporary spawn of the specified npc.<br>
   * Player's coordinates will be used for the spawn.
   *
   * @param npcId the Id of the npc to spawn
   * @return the {@link L2Npc} object of the newly spawned npc or {@code null} if the npc doesn't
   *     exist
   * @see #addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, int
   *     despawnDelay, boolean isSummonSpawn)
   */
  public L2Npc addSpawn(int npcId) {
    return addSpawn(npcId, player.getX(), player.getY(), player.getZ(), 0, false, 0, false);
  }

  /** Add a temporary spawn of the specified npc. */
  public L2Npc addSpawn(
      int npcId,
      int x,
      int y,
      int z,
      int heading,
      boolean randomOffset,
      int despawnDelay,
      boolean isSummonSpawn) {
    return quest.addSpawn(npcId, x, y, z, heading, randomOffset, despawnDelay, isSummonSpawn);
  }

  /**
   * Add a temporary spawn of the specified npc.<br>
   * Player's coordinates will be used for the spawn.
   *
   * @param npcId the Id of the npc to spawn
   * @param despawnDelay time in milliseconds till the npc is despawned (default: 0)
   * @return the {@link L2Npc} object of the newly spawned npc or {@code null} if the npc doesn't
   *     exist
   * @see #addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, int
   *     despawnDelay, boolean isSummonSpawn)
   */
  public L2Npc addSpawn(int npcId, int despawnDelay) {
    return addSpawn(
        npcId, player.getX(), player.getY(), player.getZ(), 0, false, despawnDelay, false);
  }

  /**
   * Add a temporary spawn of the specified npc.
   *
   * @param npcId the Id of the npc to spawn
   * @param x the X coordinate of the npc spawn location
   * @param y the Y coordinate of the npc spawn location
   * @param z the Z coordinate (height) of the npc spawn location
   * @return the {@link L2Npc} object of the newly spawned npc or {@code null} if the npc doesn't
   *     exist
   * @see #addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, int
   *     despawnDelay, boolean isSummonSpawn)
   */
  public L2Npc addSpawn(int npcId, int x, int y, int z) {
    return addSpawn(npcId, x, y, z, 0, false, 0, false);
  }

  // --- Spawn methods ---

  /**
   * Add a temporary spawn of the specified npc.
   *
   * @param npcId the Id of the npc to spawn
   * @param x the X coordinate of the npc spawn location
   * @param y the Y coordinate of the npc spawn location
   * @param z the Z coordinate (height) of the npc spawn location
   * @param despawnDelay time in milliseconds till the npc is despawned (default: 0)
   * @return the {@link L2Npc} object of the newly spawned npc or {@code null} if the npc doesn't
   *     exist
   * @see #addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, int
   *     despawnDelay, boolean isSummonSpawn)
   */
  public L2Npc addSpawn(int npcId, int x, int y, int z, int despawnDelay) {
    return addSpawn(npcId, x, y, z, 0, false, despawnDelay, false);
  }

  /**
   * Add a temporary spawn of the specified npc.
   *
   * @param npcId the Id of the npc to spawn
   * @param cha the character whose coordinates will be used for the npc spawn
   * @return the {@link L2Npc} object of the newly spawned npc or {@code null} if the npc doesn't
   *     exist
   * @see #addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, int
   *     despawnDelay, boolean isSummonSpawn)
   */
  public L2Npc addSpawn(int npcId, L2Character cha) {
    return addSpawn(npcId, cha.getX(), cha.getY(), cha.getZ(), cha.getHeading(), true, 0, false);
  }

  /**
   * Add a temporary spawn of the specified npc.
   *
   * @param npcId the Id of the npc to spawn
   * @param cha the character whose coordinates will be used for the npc spawn
   * @param despawnDelay time in milliseconds till the npc is despawned (default: 0)
   * @return the {@link L2Npc} object of the newly spawned npc or {@code null} if the npc doesn't
   *     exist
   * @see #addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, int
   *     despawnDelay, boolean isSummonSpawn)
   */
  public L2Npc addSpawn(int npcId, L2Character cha, int despawnDelay) {
    return addSpawn(
        npcId, cha.getX(), cha.getY(), cha.getZ(), cha.getHeading(), true, despawnDelay, false);
  }

  /**
   * Add a temporary spawn of the specified npc.
   *
   * @param npcId the Id of the npc to spawn
   * @param cha the character whose coordinates will be used for the npc spawn
   * @param randomOffset if {@code true}, adds +/- 50~100 to X/Y coordinates of the spawn location
   * @param despawnDelay time in milliseconds till the npc is despawned (default: 0)
   * @return the {@link L2Npc} object of the newly spawned npc or {@code null} if the npc doesn't
   *     exist
   * @see #addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, int
   *     despawnDelay, boolean isSummonSpawn)
   */
  public L2Npc addSpawn(int npcId, L2Character cha, boolean randomOffset, int despawnDelay) {
    return addSpawn(
        npcId,
        cha.getX(),
        cha.getY(),
        cha.getZ(),
        cha.getHeading(),
        randomOffset,
        despawnDelay,
        false);
  }

  /**
   * Add a temporary spawn of the specified npc.
   *
   * @param npcId the Id of the npc to spawn
   * @param x the X coordinate of the npc spawn location
   * @param y the Y coordinate of the npc spawn location
   * @param z the Z coordinate (height) of the npc spawn location
   * @param heading the heading of the npc
   * @param randomOffset if {@code true}, adds +/- 50~100 to X/Y coordinates of the spawn location
   * @param despawnDelay time in milliseconds till the npc is despawned (default: 0)
   * @return the {@link L2Npc} object of the newly spawned npc or {@code null} if the npc doesn't
   *     exist
   * @see #addSpawn(int, int, int, int, int, boolean, int, boolean)
   */
  public L2Npc addSpawn(
      int npcId, int x, int y, int z, int heading, boolean randomOffset, int despawnDelay) {
    return addSpawn(npcId, x, y, z, heading, randomOffset, despawnDelay, false);
  }

  /**
   * Send an HTML file to the specified player.
   *
   * @param filename the name of the HTML file to show
   * @return the contents of the HTML file that was sent to the player
   * @see #showHtmlFile(String, L2Npc)
   * @see Quest#showHtmlFile(L2PcInstance, String)
   * @see Quest#showHtmlFile(L2PcInstance, String, L2Npc)
   */
  public String showHtmlFile(String filename) {
    return showHtmlFile(filename, null);
  }

  /**
   * Send an HTML file to the specified player.
   *
   * @param filename the name of the HTML file to show
   * @param npc the NPC that is showing the HTML file
   * @return the contents of the HTML file that was sent to the player
   * @see Quest#showHtmlFile(L2PcInstance, String)
   * @see Quest#showHtmlFile(L2PcInstance, String, L2Npc)
   */
  public String showHtmlFile(String filename, L2Npc npc) {
    return getQuest().showHtmlFile(player, filename, npc);
  }

  /**
   * Set condition to 1, state to STARTED and play the "ItemSound.quest_accept".<br>
   * Works only if state is CREATED and the quest is not a custom quest.
   *
   * @return the newly created {@code QuestState} object
   */
  public QuestState startQuest() {
    return startQuest(true);
  }

  /**
   * Starts the quest.
   *
   * @param playSound if {@code true} plays the accept sound
   * @return the quest state
   */
  public QuestState startQuest(boolean playSound) {
    return startQuest(playSound, 1);
  }

  /**
   * Starts the quest.
   *
   * @param playSound if {@code true} plays the accept sound
   * @param cond the cond
   * @return the quest state
   */
  public QuestState startQuest(boolean playSound, int cond) {
    if (isCreated() && !getQuest().isCustomQuest()) {
      set("cond", cond);
      setState(State.STARTED);
      if (playSound) {
        playSound(Sound.ITEMSOUND_QUEST_ACCEPT);
      }
    }
    return this;
  }

  /**
   * Finishes the quest and removes all quest items associated with this quest from the player's
   * inventory.<br>
   * If {@code type} is {@code QuestType.ONE_TIME}, also removes all other quest data associated
   * with this quest.
   *
   * @param type the {@link QuestType} of the quest
   * @return this {@link QuestState} object
   * @see #exitQuest(QuestType type, boolean playExitQuest)
   * @see #exitQuest(boolean repeatable)
   * @see #exitQuest(boolean repeatable, boolean playExitQuest)
   */
  public QuestState exitQuest(QuestType type) {
    if (type == QuestType.DAILY) {
      exitQuest(false);
      setRestartTime();
    } else {
      exitQuest(type == QuestType.REPEATABLE);
    }
    return this;
  }

  /**
   * Finishes the quest and removes all quest items associated with this quest from the player's
   * inventory.<br>
   * If {@code type} is {@code QuestType.ONE_TIME}, also removes all other quest data associated
   * with this quest.
   *
   * @param type the {@link QuestType} of the quest
   * @param playExitQuest if {@code true}, plays "ItemSound.quest_finish"
   * @return this {@link QuestState} object
   * @see #exitQuest(QuestType type)
   * @see #exitQuest(boolean repeatable)
   * @see #exitQuest(boolean repeatable, boolean playExitQuest)
   */
  public QuestState exitQuest(QuestType type, boolean playExitQuest) {
    exitQuest(type);
    if (playExitQuest) {
      playSound(Sound.ITEMSOUND_QUEST_FINISH);
    }
    return this;
  }

  /**
   * Finishes the quest and removes all quest items associated with this quest from the player's
   * inventory.<br>
   * If {@code repeatable} is set to {@code false}, also removes all other quest data associated
   * with this quest.
   *
   * @param repeatable if {@code true}, deletes all data and variables of this quest, otherwise
   *     keeps them
   * @param playExitQuest if {@code true}, plays "ItemSound.quest_finish"
   * @return this {@link QuestState} object
   * @see #exitQuest(QuestType type)
   * @see #exitQuest(QuestType type, boolean playExitQuest)
   * @see #exitQuest(boolean repeatable)
   */
  public QuestState exitQuest(boolean repeatable, boolean playExitQuest) {
    exitQuest(repeatable);
    if (playExitQuest) {
      playSound(Sound.ITEMSOUND_QUEST_FINISH);
    }
    return this;
  }

  /**
   * Finishes the quest and removes all quest items associated with this quest from the player's
   * inventory.<br>
   * If {@code repeatable} is set to {@code false}, also removes all other quest data associated
   * with this quest.
   *
   * @param repeatable if {@code true}, deletes all data and variables of this quest, otherwise
   *     keeps them
   * @return this {@link QuestState} object
   * @see #exitQuest(QuestType type)
   * @see #exitQuest(QuestType type, boolean playExitQuest)
   * @see #exitQuest(boolean repeatable, boolean playExitQuest)
   */
  public QuestState exitQuest(boolean repeatable) {
    player.removeNotifyQuestOfDeath(this);

    if (!isStarted()) {
      return this;
    }

    // Clean registered quest items
    getQuest().removeRegisteredQuestItems(player);

    Quest.deleteQuestInDb(this, repeatable);
    if (repeatable) {
      player.delQuestState(getQuestName());
      player.sendPacket(new QuestList());
    } else {
      setState(State.COMPLETED);
    }
    vars = null;
    return this;
  }

  /**
   * @param state the new state of the quest to set
   * @return {@code true} if state was changed, {@code false} otherwise
   * @see #setState(int state, boolean saveInDb)
   * @see com.l2jserver.gameserver.model.quest.State
   */
  public boolean setState(int state) {
    return setState(state, true);
  }

  /**
   * Change the state of this quest to the specified value.
   *
   * @param state the new state of the quest to set
   * @param saveInDb if {@code true}, will save the state change in the database
   * @return {@code true} if state was changed, {@code false} otherwise
   * @see com.l2jserver.gameserver.model.quest.State
   */
  public boolean setState(int state, boolean saveInDb) {
    if (this.state == state) {
      return false;
    }
    final boolean newQuest = isCreated();
    this.state = state;
    if (saveInDb) {
      if (newQuest) {
        Quest.createQuestInDb(this);
      } else {
        Quest.updateQuestInDb(this);
      }
    }

    player.sendPacket(new QuestList());
    return true;
  }

  /**
   * @return {@code true} if the State of this QuestState is CREATED, {@code false} otherwise
   * @see com.l2jserver.gameserver.model.quest.State
   */
  public boolean isCreated() {
    return (state == State.CREATED);
  }

  /**
   * Send a packet in order to play a sound to the player.
   *
   * @param audio the {@link IAudio} object of the sound to play
   */
  public void playSound(IAudio audio) {
    AbstractScript.playSound(player, audio);
  }

  /**
   * Set the restart time for the daily quests.<br>
   * The time is hardcoded at {@link Quest#getResetHour()} hours, {@link Quest#getResetMinutes()}
   * minutes of the following day.<br>
   * It can be overridden in scripts (quests).
   */
  public void setRestartTime() {
    final Calendar reDo = Calendar.getInstance();
    if (reDo.get(Calendar.HOUR_OF_DAY) >= getQuest().getResetHour()) {
      reDo.add(Calendar.DATE, 1);
    }
    reDo.set(Calendar.HOUR_OF_DAY, getQuest().getResetHour());
    reDo.set(Calendar.MINUTE, getQuest().getResetMinutes());
    set("restartTime", String.valueOf(reDo.getTimeInMillis()));
  }

  /**
   * Check if a daily quest is available to be started over.
   *
   * @return {@code true} if the quest is available, {@code false} otherwise.
   */
  public boolean isNowAvailable() {
    final String val = get("restartTime");
    return !Util.isDigit(val) || (Long.parseLong(val) <= System.currentTimeMillis());
  }

  public void setNRMemo(L2PcInstance talker, int value) {
    set("NRmemo", String.valueOf(value));
  }

  // TODO Check if remover only for basic NRmemo or from all!
  // TODO Maybe value determine what to be removed?
  public void removeNRMemo(L2PcInstance talker, int value) {
    unset("NRmemo");
  }

  public void setNRMemoState(L2PcInstance talker, int slot, int value) {
    set("NRmemoState" + slot, String.valueOf(value));
  }

  public int getNRMemoState(L2PcInstance talker, int slot) {
    return getInt("NRmemoState" + slot);
  }

  // TODO Find what unknown do (always 1)
  public void setNRMemoStateEx(L2PcInstance talker, int slot, int unknown, int value) {
    set("NRmemoStateEx" + slot, String.valueOf(value));
  }

  // TODO Find what unknown do (always 1)
  public int getNRMemoStateEx(L2PcInstance talker, int slot, int unknown) {
    return getInt("NRmemoStateEx" + slot);
  }

  public boolean haveNRMemo(L2PcInstance talker, int slot) {
    return getInt("NRmemo") == slot;
  }

  public void setNRFlagJournal(L2PcInstance talker, int questId, int flagId) {
    set("NRFlagJournal", String.valueOf(flagId));
  }

  public void setFlagJournal(int flagId) {
    set("FlagJournal", String.valueOf(flagId));
  }

  public void resetFlagJournal(int flagId) {
    unset("FlagJournal");
  }

  public void enableTutorialEvent(L2PcInstance talker, int state) {
    talker.sendPacket(new TutorialEnableClientEvent(state));
  }

  public int getDominionSiegeID(L2PcInstance talker) {
    return TerritoryWarManager.getInstance().getRegisteredTerritoryId(talker);
  }

  public void showQuestionMark(L2PcInstance talker, int number) {
    talker.sendPacket(new TutorialShowQuestionMark(number));
  }

  public void showQuestionMark(int number) {
    player.sendPacket(new TutorialShowQuestionMark(number));
  }

  public void closeTutorialHtml(L2PcInstance player) {
    player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
  }
}
