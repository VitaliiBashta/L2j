package com.l2jserver.gameserver.model;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.data.sql.impl.TerritoryTable;
import com.l2jserver.gameserver.datatables.NpcPersonalAIData;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.interfaces.ILocational;
import com.l2jserver.gameserver.model.interfaces.IPositionable;
import com.l2jserver.gameserver.model.interfaces.Identifiable;
import com.l2jserver.gameserver.model.zone.type.NpcSpawnTerritory;

import java.lang.reflect.Constructor;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.l2jserver.gameserver.config.Configuration.customs;
import static com.l2jserver.gameserver.config.Configuration.general;

/**
 * This class manages the spawn and respawn of a group of L2NpcInstance that are in the same are and
 * have the same type.<br>
 * <B><U>Concept</U>:</B><br>
 * L2NpcInstance can be spawned either in a random position into a location area (if Lox=0 and
 * Locy=0), either at an exact position.<br>
 * The heading of the L2NpcInstance can be a random heading if not defined (value= -1) or an exact
 * heading (ex : merchant...).
 *
 * @author Nightmare
 */
public class L2Spawn implements IPositionable, Identifiable {
  protected static final Logger _log = Logger.getLogger(L2Spawn.class.getName());
  private static final List<SpawnListener> _spawnListeners = new CopyOnWriteArrayList<>();
  /**
   * The link on the L2NpcTemplate object containing generic and static properties of this spawn (ex
   * : RewardExp, RewardSP, AggroRange...)
   */
  private final L2NpcTemplate template;

  private final Deque<L2Npc> _spawnedNpcs = new ConcurrentLinkedDeque<>();
  /** The current number of SpawnTask in progress or stand by of this L2Spawn */
  protected int _scheduledCount;
  /** String identifier of this spawn */
  private String name;
  /** The maximum number of L2NpcInstance that can manage this L2Spawn */
  private int _maximumCount;
  /** The current number of L2NpcInstance managed by this L2Spawn */
  private int _currentCount;
  /** The identifier of the location area where L2NpcInstance can be spwaned */
  private int _locationId;
  /** The Location of this NPC spawn. */
  private Location _location = new Location(0, 0, 0, 0, 0);
  /** Link to NPC spawn territory */
  private NpcSpawnTerritory _spawnTerritory = null;
  /** Minimum respawn delay */
  private int _respawnMinDelay;
  /** Maximum respawn delay */
  private int _respawnMaxDelay;
  /** The generic constructor of L2NpcInstance managed by this L2Spawn */
  private Constructor<? extends L2Npc> _constructor;
  /** If True a L2NpcInstance is respawned each time that another is killed */
  private boolean _doRespawn;
  /** If true then spawn is custom */
  private boolean _customSpawn;

  private Map<Integer, Location> _lastSpawnPoints;
  private boolean _isNoRndWalk = false; // Is no random walk
  private String _areaName;
  private int _globalMapId;

  /**
   * Constructor of L2Spawn.<br>
   * <B><U>Concept</U>:</B><br>
   * Each L2Spawn owns generic and static properties (ex : RewardExp, RewardSP, AggroRange...).<br>
   * All of those properties are stored in a different L2NpcTemplate for each type of L2Spawn. Each
   * template is loaded once in the server cache memory (reduce memory use).<br>
   * When a new instance of L2Spawn is created, server just create a link between the instance and
   * the template.<br>
   * This link is stored in <B>_template</B> Each L2NpcInstance is linked to a L2Spawn that manages
   * its spawn and respawn (delay, location...).<br>
   * This link is stored in <B>_spawn</B> of the L2NpcInstance.<br>
   * <B><U> Actions</U>:</B><br>
   *
   * <ul>
   *   <li>Set the _template of the L2Spawn
   *   <li>Calculate the implementationName used to generate the generic constructor of
   *       L2NpcInstance managed by this L2Spawn
   *   <li>Create the generic constructor of L2NpcInstance managed by this L2Spawn
   * </ul>
   *
   * @param template The L2NpcTemplate to link to this L2Spawn
   * @throws ClassCastException when template type is not subclass of L2Npc
   */
  public L2Spawn(L2NpcTemplate template)
      throws SecurityException, ClassNotFoundException, NoSuchMethodException, ClassCastException {
    // Set the _template of the L2Spawn
    this.template = template;

    String className =
        "com.l2jserver.gameserver.model.actor.instance." + this.template.getType() + "Instance";

    // Create the generic constructor of L2Npc managed by this L2Spawn
    _constructor =
        Class.forName(className).asSubclass(L2Npc.class).getConstructor(L2NpcTemplate.class);
  }

  /** Creates a spawn. */
  public L2Spawn(int npcId) {
    throw new IllegalArgumentException("Create spawn with template not id: " + npcId);
  }

  public static void addSpawnListener(SpawnListener listener) {
    _spawnListeners.add(listener);
  }

  public static void removeSpawnListener(SpawnListener listener) {
    _spawnListeners.remove(listener);
  }

  /** @return the maximum number of L2NpcInstance that this L2Spawn can manage. */
  public int getAmount() {
    return _maximumCount;
  }

  /**
   * Set the maximum number of L2NpcInstance that this L2Spawn can manage.
   *
   * @param amount
   */
  public void setAmount(int amount) {
    _maximumCount = amount;
  }

  public static void notifyNpcSpawned(L2Npc npc) {
    for (SpawnListener listener : _spawnListeners) {
      listener.npcSpawned(npc);
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Location getLocation(L2Object obj) {
    return ((_lastSpawnPoints == null)
            || (obj == null)
            || !_lastSpawnPoints.containsKey(obj.getObjectId()))
        ? _location
        : _lastSpawnPoints.get(obj.getObjectId());
  }

  /**
   * @param obj object to check
   * @return the Y position of the last spawn point of given NPC.
   */
  public int getY(L2Object obj) {
    return getLocation(obj).getY();
  }

  /**
   * @param obj object to check
   * @return the Z position of the last spawn point of given NPC.
   */
  public int getZ(L2Object obj) {
    return getLocation(obj).getZ();
  }

  /** @return min respawn delay. */
  public int getRespawnMinDelay() {
    return _respawnMinDelay;
  }

  /**
   * Set Minimum Respawn Delay.
   *
   * @param date
   */
  public void setRespawnMinDelay(int date) {
    _respawnMinDelay = date;
  }

  /** @return max respawn delay. */
  public int getRespawnMaxDelay() {
    return _respawnMaxDelay;
  }

  /**
   * Set Maximum Respawn Delay.
   *
   * @param date
   */
  public void setRespawnMaxDelay(int date) {
    _respawnMaxDelay = date;
  }

  /** @return type of spawn. */
  public boolean isCustom() {
    return _customSpawn;
  }

  /**
   * Set the spawn as custom.<br>
   *
   * @param custom
   */
  public void setCustom(boolean custom) {
    _customSpawn = custom;
  }

  /**
   * Decrease the current number of NPC instances of this spawn and if necessary create a spawn task
   * to launch after the respawn delay.<br>
   * <B><U>Actions</U>:</B>
   * <li>Decrease the current number of L2NpcInstance of this L2Spawn
   * <li>Check if respawn is possible to prevent multiple respawning caused by lag
   * <li>Update the current number of SpawnTask in progress or stand by of this L2Spawn
   * <li>Create a new SpawnTask to launch after the respawn Delay
   * </ul>
   *
   * <FONT COLOR=#FF0000><B><U>Caution</U>: A respawn is possible ONLY if _doRespawn=True and
   * _scheduledCount + _currentCount < _maximumCount</B></FONT>
   *
   * @param oldNpc
   */
  public void decreaseCount(L2Npc oldNpc) {
    // sanity check
    if (_currentCount <= 0) {
      return;
    }

    // Decrease the current number of L2NpcInstance of this L2Spawn
    _currentCount--;

    // Remove this NPC from list of spawned
    _spawnedNpcs.remove(oldNpc);

    // Remove spawn point for old NPC
    if (_lastSpawnPoints != null) {
      _lastSpawnPoints.remove(oldNpc.getObjectId());
    }

    // Check if respawn is possible to prevent multiple respawning caused by lag
    if (_doRespawn && ((_scheduledCount + _currentCount) < _maximumCount)) {
      // Update the current number of SpawnTask in progress or stand by of this L2Spawn
      _scheduledCount++;

      // Create a new SpawnTask to launch after the respawn Delay
      // ClientScheduler.getInstance().scheduleLow(new SpawnTask(npcId), _respawnDelay);
      ThreadPoolManager.getInstance()
          .scheduleGeneral(
              new SpawnTask(oldNpc),
              hasRespawnRandom() ? Rnd.get(_respawnMinDelay, _respawnMaxDelay) : _respawnMinDelay);
    }
  }

  /**
   * @param obj object to check
   * @return the X position of the last spawn point of given NPC.
   */
  public int getX(L2Object obj) {
    return getLocation(obj).getX();
  }

  /**
   * Create the initial spawning and set _doRespawn to False, if respawn time set to 0, or set it to
   * True otherwise.
   *
   * @return The number of L2NpcInstance that were spawned
   */
  public int init() {
    while (_currentCount < _maximumCount) {
      doSpawn();
    }
    _doRespawn = _respawnMinDelay != 0;

    return _currentCount;
  }

  public L2Npc doSpawn() {
    return doSpawn(false);
  }

  public boolean hasRespawnRandom() {
    return _respawnMinDelay != _respawnMaxDelay;
  }

  /**
   * @param mob
   * @return
   */
  private L2Npc initializeNpcInstance(L2Npc mob) {
    int newlocx = 0;
    int newlocy = 0;
    int newlocz = 0;

    // If Locx and Locy are not defined, the L2NpcInstance must be spawned in an area defined by
    // location or spawn territory
    // New method
    if (isTerritoryBased()) {
      int[] p = _spawnTerritory.getRandomPoint();
      newlocx = p[0];
      newlocy = p[1];
      newlocz = p[2];
    }
    // Old method (for backward compatibility)
    else if ((getX() == 0) && (getY() == 0)) {
      if (getLocationId() == 0) {
        return mob;
      }

      // Calculate the random position in the location area
      final Location location = TerritoryTable.getInstance().getRandomPoint(getLocationId());

      // Set the calculated position of the L2NpcInstance
      if (location != null) {
        newlocx = location.getX();
        newlocy = location.getY();
        newlocz = location.getZ();
      }
    } else {
      // The L2NpcInstance is spawned at the exact position (Lox, Locy, Locz)
      newlocx = getX();
      newlocy = getY();
      newlocz = getZ();
    }

    // don't correct z of flying npc's
    if (!mob.isFlying()) {
      newlocz = GeoData.getInstance().getSpawnHeight(newlocx, newlocy, newlocz);
    }

    mob.stopAllEffects();

    mob.setIsDead(false);
    // Reset decay info
    mob.setDecayed(false);
    // Set the HP and MP of the L2NpcInstance to the max
    mob.setCurrentHpMp(mob.getMaxHp(), mob.getMaxMp());
    // Clear script variables
    if (mob.hasVariables()) {
      mob.getVariables().getSet().clear();
    }
    // Set is not random walk default value
    mob.setIsNoRndWalk(isNoRndWalk());

    // Set the heading of the L2NpcInstance (random heading if not defined)
    if (getHeading() == -1) {
      mob.setHeading(Rnd.nextInt(61794));
    } else {
      mob.setHeading(getHeading());
    }

    if (mob instanceof L2Attackable) {
      ((L2Attackable) mob).setChampion(false);
    }

    if (customs().championEnable()) {
      // Set champion on next spawn
      if (mob.isMonster()
          && !getTemplate().isUndying()
          && !mob.isRaid()
          && !mob.isRaidMinion()
          && //
          (customs().getChampionFrequency() > 0)
          && //
          (mob.getLevel() >= customs().getChampionMinLevel())
          && //
          (mob.getLevel() <= customs().getChampionMaxLevel())
          && //
          (customs().championEnableInInstances() || (getInstanceId() == 0))) {
        if (Rnd.get(100) < customs().getChampionFrequency()) {
          ((L2Attackable) mob).setChampion(true);
        }
      }
    }

    // Reset summoner
    mob.setSummoner(null);
    // Reset summoned list
    mob.resetSummonedNpcs();
    // Link the L2NpcInstance to this L2Spawn
    mob.setSpawn(this);

    // Spawn NPC
    mob.spawnMe(newlocx, newlocy, newlocz);

    notifyNpcSpawned(mob);

    _spawnedNpcs.add(mob);
    if (_lastSpawnPoints != null) {
      _lastSpawnPoints.put(mob.getObjectId(), new Location(newlocx, newlocy, newlocz));
    }

    if (general().debug()) {
      _log.finest(
          "Spawned Mob Id: "
              + template.getId()
              + " , at: X: "
              + mob.getX()
              + " Y: "
              + mob.getY()
              + " Z: "
              + mob.getZ());
    }
    // Increase the current number of L2NpcInstance managed by this L2Spawn
    _currentCount++;
    return mob;
  }

  /** @return the Identifier of the location area where L2NpcInstance can be spwaned. */
  public int getLocationId() {
    return _locationId;
  }

  /**
   * Set the Identifier of the location area where L2NpcInstance can be spawned.
   *
   * @param id
   */
  public void setLocationId(int id) {
    _locationId = id;
  }

  @Override
  public int getX() {
    return _location.getX();
  }

  /**
   * Set the X position of the spawn point.
   *
   * @param x the x coordinate
   */
  @Override
  public void setX(int x) {
    _location.setX(x);
  }

  @Override
  public int getY() {
    return _location.getY();
  }

  /**
   * Set the Y position of the spawn point.
   *
   * @param y the y coordinate
   */
  @Override
  public void setY(int y) {
    _location.setY(y);
  }

  @Override
  public int getZ() {
    return _location.getZ();
  }

  /**
   * Set the Z position of the spawn point.
   *
   * @param z the z coordinate
   */
  @Override
  public void setZ(int z) {
    _location.setZ(z);
  }

  /**
   * Set the x, y, z position of the spawn point.
   *
   * @param x The x coordinate.
   * @param y The y coordinate.
   * @param z The z coordinate.
   */
  @Override
  public void setXYZ(int x, int y, int z) {
    setX(x);
    setY(y);
    setZ(z);
  }

  /**
   * Set the x, y, z position of the spawn point.
   *
   * @param loc The location.
   */
  @Override
  public void setXYZ(ILocational loc) {
    setXYZ(loc.getX(), loc.getY(), loc.getZ());
  }

  /** @return the heading of L2NpcInstance when they are spawned. */
  @Override
  public int getHeading() {
    return _location.getHeading();
  }

  /**
   * Set the heading of L2NpcInstance when they are spawned.
   *
   * @param heading
   */
  @Override
  public void setHeading(int heading) {
    _location.setHeading(heading);
  }

  @Override
  public int getInstanceId() {
    return _location.getInstanceId();
  }

  @Override
  public Location getLocation() {
    return _location;
  }

  /**
   * Set the XYZ position of the spawn point.
   *
   * @param loc
   */
  @Override
  public void setLocation(Location loc) {
    _location = loc;
  }

  @Override
  public void setInstanceId(int instanceId) {
    _location.setInstanceId(instanceId);
  }

  public boolean isTerritoryBased() {
    return (_spawnTerritory != null) && (_location.getX() == 0) && (_location.getY() == 0);
  }

  public L2NpcTemplate getTemplate() {
    return template;
  }

  public final boolean isNoRndWalk() {
    return _isNoRndWalk;
  }

  /**
   * Create a L2NpcInstance in this L2Spawn.
   *
   * @param val
   * @return
   */
  public L2Npc spawnOne(boolean val) {
    return doSpawn(val);
  }

  /**
   * Create the L2NpcInstance, add it to the world and launch its OnSpawn action.<br>
   * <B><U>Concept</U>:</B><br>
   * L2NpcInstance can be spawned either in a random position into a location area (if Lox=0 and
   * Locy=0), either at an exact position.<br>
   * The heading of the L2NpcInstance can be a random heading if not defined (value= -1) or an exact
   * heading (ex : merchant...).<br>
   * <B><U>Actions for an random spawn into location area</U>:<I> (if Locx=0 and Locy=0)</I></B>
   *
   * <ul>
   *   <li>Get L2NpcInstance Init parameters and its generate an Identifier
   *   <li>Call the constructor of the L2NpcInstance
   *   <li>Calculate the random position in the location area (if Locx=0 and Locy=0) or get its
   *       exact position from the L2Spawn
   *   <li>Set the position of the L2NpcInstance
   *   <li>Set the HP and MP of the L2NpcInstance to the max
   *   <li>Set the heading of the L2NpcInstance (random heading if not defined : value=-1)
   *   <li>Link the L2NpcInstance to this L2Spawn
   *   <li>Init other values of the L2NpcInstance (ex : from its L2CharTemplate for INT, STR,
   *       DEX...) and add it in the world
   *   <li>Launch the action OnSpawn fo the L2NpcInstance
   *   <li>Increase the current number of L2NpcInstance managed by this L2Spawn
   * </ul>
   *
   * @param isSummonSpawn
   * @return
   */
  public L2Npc doSpawn(boolean isSummonSpawn) {
    try {
      // Check if the L2Spawn is not a L2Pet or L2Minion or L2Decoy spawn
      if (template.isType("L2Pet") || template.isType("L2Decoy") || template.isType("L2Trap")) {
        _currentCount++;

        return null;
      }

      // Call the constructor of the L2Npc
      L2Npc npc = _constructor.newInstance(template);
      npc.setInstanceId(
          getInstanceId()); // Must be done before object is spawned into visible world
      if (isSummonSpawn) {
        npc.setShowSummonAnimation(isSummonSpawn);
      }

      // Check for certain AI data, overridden in spawnlist
      if (name != null) {
        NpcPersonalAIData.getInstance().initializeNpcParameters(npc, this, name);
      }

      return initializeNpcInstance(npc);
    } catch (Exception e) {
      _log.log(Level.WARNING, "NPC " + template.getId() + " class not found", e);
    }
    return null;
  }

  /** @return true if respawn enabled */
  public boolean isRespawnEnabled() {
    return _doRespawn;
  }

  /** Set _doRespawn to False to stop respawn in this L2Spawn. */
  public void stopRespawn() {
    _doRespawn = false;
  }

  /** Set _doRespawn to True to start or restart respawn in this L2Spawn. */
  public void startRespawn() {
    _doRespawn = true;
  }

  public int getRespawnDelay() {
    return (_respawnMinDelay + _respawnMaxDelay) / 2;
  }

  public void setRespawnDelay(int delay) {
    setRespawnDelay(delay, 0);
  }

  /**
   * Set bounds for random calculation and delay for respawn
   *
   * @param delay delay in seconds
   * @param randomInterval random interval in seconds
   */
  public void setRespawnDelay(int delay, int randomInterval) {
    if (delay != 0) {
      if (delay < 0) {
        _log.warning("respawn delay is negative for spawn:" + this);
      }

      int minDelay = delay - randomInterval;
      int maxDelay = delay + randomInterval;

      _respawnMinDelay = Math.max(10, minDelay) * 1000;
      _respawnMaxDelay = Math.max(10, maxDelay) * 1000;
    } else {
      _respawnMinDelay = 0;
      _respawnMaxDelay = 0;
    }
  }

  public NpcSpawnTerritory getSpawnTerritory() {
    return _spawnTerritory;
  }

  public void setSpawnTerritory(NpcSpawnTerritory territory) {
    _spawnTerritory = territory;
    _lastSpawnPoints = new ConcurrentHashMap<>();
  }

  public L2Npc getLastSpawn() {
    return _spawnedNpcs.peekLast();
  }

  public final Deque<L2Npc> getSpawnedNpcs() {
    return _spawnedNpcs;
  }

  /** @param oldNpc */
  public void respawnNpc(L2Npc oldNpc) {
    if (_doRespawn) {
      oldNpc.refreshID();
      initializeNpcInstance(oldNpc);
    }
  }

  public final void setIsNoRndWalk(boolean value) {
    _isNoRndWalk = value;
  }

  public String getAreaName() {
    return _areaName;
  }

  public void setAreaName(String areaName) {
    _areaName = areaName;
  }

  public int getGlobalMapId() {
    return _globalMapId;
  }

  public void setGlobalMapId(int globalMapId) {
    _globalMapId = globalMapId;
  }

  @Override
  public String toString() {
    return "L2Spawn ID: " + getId() + " " + getLocation();
  }

  /**
   * Gets the NPC ID.
   *
   * @return the NPC ID
   */
  @Override
  public int getId() {
    return template.getId();
  }

  /** The task launching the function doSpawn() */
  class SpawnTask implements Runnable {
    private final L2Npc _oldNpc;

    public SpawnTask(L2Npc pOldNpc) {
      _oldNpc = pOldNpc;
    }

    @Override
    public void run() {
      try {
        // doSpawn();
        respawnNpc(_oldNpc);
      } catch (Exception e) {
        _log.log(Level.WARNING, "", e);
      }

      _scheduledCount--;
    }
  }
}
