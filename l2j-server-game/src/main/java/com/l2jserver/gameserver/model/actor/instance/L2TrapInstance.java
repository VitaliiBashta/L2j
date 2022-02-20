package com.l2jserver.gameserver.model.actor.instance;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.enums.TrapAction;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.knownlist.TrapKnownList;
import com.l2jserver.gameserver.model.actor.tasks.npc.trap.TrapTask;
import com.l2jserver.gameserver.model.actor.tasks.npc.trap.TrapTriggerTask;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.trap.OnTrapAction;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.items.L2Weapon;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.olympiad.OlympiadGameManager;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.AbstractNpcInfo.TrapInfo;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.taskmanager.DecayTaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public final class L2TrapInstance extends L2Npc {
  private static final int TICK = 1000; // 1s
  private final int _lifeTime;
  private final List<Integer> _playersWhoDetectedMe = new ArrayList<>();
  private final SkillHolder _skill;
  private boolean _hasLifeTime;
  private boolean _isInArena = false;
  private boolean _isTriggered;
  private L2PcInstance owner;
  private int _remainingTime;
  // Tasks
  private ScheduledFuture<?> _trapTask = null;

  /** Creates a trap. */
  public L2TrapInstance(L2NpcTemplate template, L2PcInstance owner, int lifeTime) {
    this(template, owner.getInstanceId(), lifeTime);
    this.owner = owner;
  }

  /** Creates a trap. */
  public L2TrapInstance(L2NpcTemplate template, int instanceId, int lifeTime) {
    super(template);
    setInstanceType(InstanceType.L2TrapInstance);
    setInstanceId(instanceId);
    setName(template.getName());
    setIsInvul(false);

    owner = null;
    _isTriggered = false;
    _skill = getTemplate().getParameters().getObject("trap_skill", SkillHolder.class);
    _hasLifeTime = lifeTime >= 0;
    _lifeTime = lifeTime != 0 ? lifeTime : 30000;
    _remainingTime = _lifeTime;
    if (_skill != null) {
      _trapTask =
          ThreadPoolManager.getInstance()
              .scheduleGeneralAtFixedRate(new TrapTask(this), TICK, TICK);
    }
  }

  @Override
  public void broadcastPacket(L2GameServerPacket mov, int radiusInKnownlist) {
    for (L2PcInstance player : getKnownList().getKnownPlayers().values()) {
      if ((player != null)
          && isInsideRadius(player, radiusInKnownlist, false, false)
          && (_isTriggered || canBeSeen(player))) {
        player.sendPacket(mov);
      }
    }
  }

  @Override
  public void broadcastPacket(L2GameServerPacket mov) {
    for (L2PcInstance player : getKnownList().getKnownPlayers().values()) {
      if ((player != null) && (_isTriggered || canBeSeen(player))) {
        player.sendPacket(mov);
      }
    }
  }

  @Override
  public void sendDamageMessage(
      L2Character target, int damage, boolean mcrit, boolean pcrit, boolean miss) {
    if (miss || (owner == null)) {
      return;
    }

    if (owner.isInOlympiadMode()
        && (target instanceof L2PcInstance)
        && ((L2PcInstance) target).isInOlympiadMode()
        && (((L2PcInstance) target).getOlympiadGameId() == owner.getOlympiadGameId())) {
      OlympiadGameManager.getInstance().notifyCompetitorDamage(getOwner(), damage);
    }

    if ((target.isInvul() || target.isHpBlocked()) && !target.isNpc()) {
      owner.sendPacket(SystemMessageId.ATTACK_WAS_BLOCKED);
    } else {
      final SystemMessage sm =
          SystemMessage.getSystemMessage(SystemMessageId.C1_DONE_S3_DAMAGE_TO_C2);
      sm.addCharName(this);
      sm.addCharName(target);
      sm.addInt(damage);
      owner.sendPacket(sm);
    }
  }

  /** Get the owner of this trap. */
  public L2PcInstance getOwner() {
    return owner;
  }

  /**
   * Verify if the character can see the trap.
   *
   * @param cha the character to verify
   * @return {@code true} if the character can see the trap, {@code false} otherwise
   */
  public boolean canBeSeen(L2Character cha) {
    if ((cha != null) && _playersWhoDetectedMe.contains(cha.getObjectId())) {
      return true;
    }

    if ((owner == null) || (cha == null)) {
      return false;
    }
    if (cha == owner) {
      return true;
    }

    if (cha instanceof L2PcInstance) {
      // observers can't see trap
      if (((L2PcInstance) cha).inObserverMode()) {
        return false;
      }

      // olympiad competitors can't see trap
      if (owner.isInOlympiadMode()
          && ((L2PcInstance) cha).isInOlympiadMode()
          && (((L2PcInstance) cha).getOlympiadSide() != owner.getOlympiadSide())) {
        return false;
      }
    }

    if (_isInArena) {
      return true;
    }

    return owner.isInParty()
        && cha.isInParty()
        && (owner.getParty().getLeaderObjectId() == cha.getParty().getLeaderObjectId());
  }

  @Override
  public TrapKnownList getKnownList() {
    return (TrapKnownList) super.getKnownList();
  }

  @Override
  public void initKnownList() {
    setKnownList(new TrapKnownList(this));
  }

  @Override
  public void updateAbnormalEffect() {}

  @Override
  public boolean isAutoAttackable(L2Character attacker) {
    return !canBeSeen(attacker);
  }

  @Override
  public L2Weapon getActiveWeaponItem() {
    return null;
  }

  @Override
  public L2ItemInstance getSecondaryWeaponInstance() {
    return null;
  }

  @Override
  public L2Weapon getSecondaryWeaponItem() {
    return null;
  }

  @Override
  public void onSpawn() {
    super.onSpawn();
    _isInArena = isInsideZone(ZoneId.PVP) && !isInsideZone(ZoneId.SIEGE);
    _playersWhoDetectedMe.clear();
  }

  @Override
  public boolean deleteMe() {
    if (owner != null) {
      owner.setTrap(null);
      owner = null;
    }
    return super.deleteMe();
  }

  @Override
  public void sendInfo(L2PcInstance activeChar) {
    if (_isTriggered || canBeSeen(activeChar)) {
      activeChar.sendPacket(new TrapInfo(this, activeChar));
    }
  }

  public boolean checkTarget(L2Character target) {
    // Range seems to be reduced from Freya(300) to H5(150)
    if (!target.isInsideRadius(this, 150, false, false)) {
      return false;
    }

    if (!Skill.checkForAreaOffensiveSkills(this, target, _skill.getSkill(), _isInArena)) {
      return false;
    }

    // observers
    if (target.isPlayer() && target.getActingPlayer().inObserverMode()) {
      return false;
    }

    // olympiad own team and their summons not attacked
    if ((owner != null) && owner.isInOlympiadMode()) {
      final L2PcInstance player = target.getActingPlayer();
      if ((player != null)
          && player.isInOlympiadMode()
          && (player.getOlympiadSide() == owner.getOlympiadSide())) {
        return false;
      }
    }

    if (_isInArena) {
      return true;
    }

    // trap owned by players not attack non-flagged players
    if (owner != null) {
      if (target instanceof L2Attackable) {
        return true;
      }

      final L2PcInstance player = target.getActingPlayer();
      return (player != null) && ((player.getPvpFlag() != 0) || (player.getKarma() != 0));
    }
    return true;
  }

  public int getKarma() {
    return owner != null ? owner.getKarma() : 0;
  }

  public byte getPvpFlag() {
    return owner != null ? owner.getPvpFlag() : 0;
  }

  public Skill getSkill() {
    return _skill.getSkill();
  }

  @Override
  public boolean isTrap() {
    return true;
  }

  @Override
  public L2PcInstance getActingPlayer() {
    return owner;
  }

  /**
   * Checks is triggered
   *
   * @return True if trap is triggered.
   */
  public boolean isTriggered() {
    return _isTriggered;
  }

  public void setDetected(L2Character detector) {
    if (_isInArena) {
      if (detector.isPlayable()) {
        sendInfo(detector.getActingPlayer());
      }
      return;
    }

    if ((owner != null) && (owner.getPvpFlag() == 0) && (owner.getKarma() == 0)) {
      return;
    }

    _playersWhoDetectedMe.add(detector.getObjectId());

    // Notify to scripts
    EventDispatcher.getInstance()
        .notifyEventAsync(new OnTrapAction(this, detector, TrapAction.TRAP_DETECTED), this);

    if (detector.isPlayable()) {
      sendInfo(detector.getActingPlayer());
    }
  }

  public void stopDecay() {
    DecayTaskManager.getInstance().cancel(this);
  }

  /**
   * Trigger the trap.
   *
   * @param target the target
   */
  public void triggerTrap(L2Character target) {
    if (_trapTask != null) {
      _trapTask.cancel(true);
      _trapTask = null;
    }

    _isTriggered = true;
    broadcastPacket(new TrapInfo(this, null));
    setTarget(target);

    EventDispatcher.getInstance()
        .notifyEventAsync(new OnTrapAction(this, target, TrapAction.TRAP_TRIGGERED), this);

    ThreadPoolManager.getInstance().scheduleGeneral(new TrapTriggerTask(this), 500);
  }

  public void unSummon() {
    if (_trapTask != null) {
      _trapTask.cancel(true);
      _trapTask = null;
    }

    if (owner != null) {
      owner.setTrap(null);
      owner = null;
    }

    if (isVisible() && !isDead()) {
      if (getWorldRegion() != null) {
        getWorldRegion().removeFromZones(this);
      }

      deleteMe();
    }
  }

  public boolean hasLifeTime() {
    return _hasLifeTime;
  }

  public void setHasLifeTime(boolean val) {
    _hasLifeTime = val;
  }

  public int getRemainingTime() {
    return _remainingTime;
  }

  public void setRemainingTime(int time) {
    _remainingTime = time;
  }

  public int getLifeTime() {
    return _lifeTime;
  }
}
