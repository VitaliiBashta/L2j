package com.l2jserver.gameserver.model;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.model.actor.*;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.model.zone.type.L2PeaceZone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import static com.l2jserver.gameserver.config.Configuration.general;

public final class L2WorldRegion {
  private static final Logger LOG = LogManager.getLogger(L2WorldRegion.class);

  /** Map containing all playable characters in game in this world region. */
  private final Map<Integer, L2Playable> _allPlayable = new ConcurrentHashMap<>();

  /** Map containing visible objects in this world region. */
  private final Map<Integer, L2Object> _visibleObjects = new ConcurrentHashMap<>();

  private final Queue<L2WorldRegion> _surroundingRegions = new ConcurrentLinkedQueue<>();
  private final int _tileX, _tileY;
  private final List<L2ZoneType> _zones = new CopyOnWriteArrayList<>();
  private boolean _active;
  private ScheduledFuture<?> _neighborsTask = null;

  public L2WorldRegion(int pTileX, int pTileY) {
    _tileX = pTileX;
    _tileY = pTileY;

    // default a newly initialized region to inactive, unless always on is specified
    _active = general().gridsAlwaysOn();
  }

  public List<L2ZoneType> getZones() {
    return _zones;
  }

  public void addZone(L2ZoneType zone) {
    _zones.add(zone);
  }

  public void removeZone(L2ZoneType zone) {
    _zones.remove(zone);
  }

  public void revalidateZones(L2Character character) {
    // do NOT update the world region while the character is still in the process of teleporting
    // Once the teleport is COMPLETED, revalidation occurs safely, at that time.

    if (character.isTeleporting()) {
      return;
    }

    for (L2ZoneType z : getZones()) {
      if (z != null) {
        z.revalidateInZone(character);
      }
    }
  }

  public void removeFromZones(L2Character character) {
    for (L2ZoneType z : getZones()) {
      if (z != null) {
        z.removeCharacter(character);
      }
    }
  }

  public boolean containsZone(int zoneId) {
    for (L2ZoneType z : getZones()) {
      if (z.getId() == zoneId) {
        return true;
      }
    }
    return false;
  }

  public boolean checkEffectRangeInsidePeaceZone(
      Skill skill, final int x, final int y, final int z) {
    final int range = skill.getEffectRange();
    final int up = y + range;
    final int down = y - range;
    final int left = x + range;
    final int right = x - range;

    for (L2ZoneType e : getZones()) {
      if (e instanceof L2PeaceZone) {
        if (e.isInsideZone(x, up, z)) {
          return false;
        }

        if (e.isInsideZone(x, down, z)) {
          return false;
        }

        if (e.isInsideZone(left, y, z)) {
          return false;
        }

        if (e.isInsideZone(right, y, z)) {
          return false;
        }

        if (e.isInsideZone(x, y, z)) {
          return false;
        }
      }
    }
    return true;
  }

  public void onDeath(L2Character character) {
    for (L2ZoneType z : getZones()) {
      if (z != null) {
        z.onDieInside(character);
      }
    }
  }

  public void onRevive(L2Character character) {
    for (L2ZoneType z : getZones()) {
      if (z != null) {
        z.onReviveInside(character);
      }
    }
  }

  private void switchAI(boolean isOn) {
    int c = 0;
    if (!isOn) {
      for (L2Object o : _visibleObjects.values()) {
        if (o instanceof L2Attackable) {
          c++;
          L2Attackable mob = (L2Attackable) o;

          // Set target to null and cancel Attack or Cast
          mob.setTarget(null);

          // Stop movement
          mob.stopMove(null);

          // Stop all active skills effects in progress on the L2Character
          mob.stopAllEffects();

          mob.clearAggroList();
          mob.getAttackByList().clear();
          mob.getKnownList().removeAllKnownObjects();

          // stop the ai tasks
          if (mob.hasAI()) {
            mob.getAI().setIntention(com.l2jserver.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE);
            mob.getAI().stopAITask();
          }
        } else if (o instanceof L2Vehicle) {
          c++;
          ((L2Vehicle) o).getKnownList().removeAllKnownObjects();
        }
      }

      LOG.trace("{} mobs were turned off", c);
    } else {
      for (L2Object o : _visibleObjects.values()) {
        if (o instanceof L2Attackable) {
          c++;
          // Start HP/MP/CP Regeneration task
          ((L2Attackable) o).getStatus().startHpMpRegeneration();
        } else if (o instanceof L2Npc) {
          ((L2Npc) o).startRandomAnimationTimer();
        }
      }

      LOG.trace("{} mobs were turned on", c);
    }
  }

  public boolean isActive() {
    return _active;
  }

  /**
   * this function turns this region's AI and geodata on or off
   *
   * @param value
   */
  public void setActive(boolean value) {
    if (_active == value) {
      return;
    }

    _active = value;

    // turn the AI on or off to match the region's activation.
    switchAI(value);

    // TODO
    // turn the geodata on or off to match the region's activation.
    if (value) {
      LOG.trace("Starting Grid {},{}", _tileX, _tileY);
    } else {
      LOG.trace("Stoping Grid {},{}", _tileX, _tileY);
    }
  }

  // check if all 9 neighbors (including self) are inactive or active but with no players.
  // returns true if the above condition is met.
  public boolean areNeighborsEmpty() {
    // if this region is occupied, return false.
    if (isActive() && !_allPlayable.isEmpty()) {
      return false;
    }

    // if any one of the neighbors is occupied, return false
    for (L2WorldRegion neighbor : _surroundingRegions) {
      if (neighbor.isActive() && !neighbor._allPlayable.isEmpty()) {
        return false;
      }
    }

    // in all other cases, return true.
    return true;
  }

  /**
   * Immediately sets self as active and starts a timer to set neighbors as active this timer is to
   * avoid turning on neighbors in the case when a person just teleported into a region and then
   * teleported out immediately...there is no reason to activate all the neighbors in that case.
   */
  private void startActivation() {
    // first set self to active and do self-tasks...
    setActive(true);

    // if the timer to deactivate neighbors is running, cancel it.
    synchronized (this) {
      if (_neighborsTask != null) {
        _neighborsTask.cancel(true);
        _neighborsTask = null;
      }

      // then, set a timer to activate the neighbors
      _neighborsTask =
          ThreadPoolManager.getInstance()
              .scheduleGeneral(
                  new NeighborsTask(true), 1000 * general().getGridNeighborTurnOnTime());
    }
  }

  /**
   * starts a timer to set neighbors (including self) as inactive this timer is to avoid turning off
   * neighbors in the case when a person just moved out of a region that he may very soon return to.
   * There is no reason to turn self & neighbors off in that case.
   */
  private void startDeactivation() {
    // if the timer to activate neighbors is running, cancel it.
    synchronized (this) {
      if (_neighborsTask != null) {
        _neighborsTask.cancel(true);
        _neighborsTask = null;
      }

      // start a timer to "suggest" a deactivate to self and neighbors.
      // suggest means: first check if a neighbor has L2PcInstances in it. If not, deactivate.
      _neighborsTask =
          ThreadPoolManager.getInstance()
              .scheduleGeneral(
                  new NeighborsTask(false), 1000 * general().getGridNeighborTurnOffTime());
    }
  }

  /**
   * Add the L2Object in the L2ObjectHashSet(L2Object) _visibleObjects containing L2Object visible
   * in this L2WorldRegion <br>
   * If L2Object is a L2PcInstance, Add the L2PcInstance in the L2ObjectHashSet(L2PcInstance)
   * _allPlayable containing L2PcInstance of all player in game in this L2WorldRegion <br>
   * Assert : object.getCurrentWorldRegion() == this
   *
   * @param object
   */
  public void addVisibleObject(L2Object object) {
    if (object == null) {
      return;
    }

    assert object.getWorldRegion() == this;

    _visibleObjects.put(object.getObjectId(), object);

    if (object instanceof L2Playable) {
      _allPlayable.put(object.getObjectId(), (L2Playable) object);

      // if this is the first player to enter the region, activate self & neighbors
      if ((_allPlayable.size() == 1) && !general().gridsAlwaysOn()) {
        startActivation();
      }
    }
  }

  /**
   * Remove the L2Object from the L2ObjectHashSet(L2Object) _visibleObjects in this L2WorldRegion.
   * If L2Object is a L2PcInstance, remove it from the L2ObjectHashSet(L2PcInstance) _allPlayable of
   * this L2WorldRegion <br>
   * Assert : object.getCurrentWorldRegion() == this || object.getCurrentWorldRegion() == null
   *
   * @param object
   */
  public void removeVisibleObject(L2Object object) {
    if (object == null) {
      return;
    }

    assert (object.getWorldRegion() == this) || (object.getWorldRegion() == null);

    _visibleObjects.remove(object.getObjectId());

    if (object instanceof L2Playable) {
      _allPlayable.remove(object.getObjectId());

      if (_allPlayable.isEmpty() && !general().gridsAlwaysOn()) {
        startDeactivation();
      }
    }
  }

  public void addSurroundingRegion(L2WorldRegion region) {
    _surroundingRegions.add(region);
  }

  /** @return the list containing all L2WorldRegion around the current world region */
  public Queue<L2WorldRegion> getSurroundingRegions() {
    return _surroundingRegions;
  }

  public Map<Integer, L2Playable> getVisiblePlayable() {
    return _allPlayable;
  }

  public Map<Integer, L2Object> getVisibleObjects() {
    return _visibleObjects;
  }

  public String getName() {
    return "(" + _tileX + ", " + _tileY + ")";
  }

  /** Deleted all spawns in the world. */
  public void deleteVisibleNpcSpawns() {
    LOG.trace("Deleting all visible NPC's in Region: {}", getName());
    for (L2Object obj : _visibleObjects.values()) {
      if (obj instanceof L2Npc) {
        L2Npc target = (L2Npc) obj;
        target.deleteMe();
        L2Spawn spawn = target.getSpawn();
        if (spawn != null) {
          spawn.stopRespawn();
          SpawnTable.getInstance().deleteSpawn(spawn, false);
        }
        LOG.trace("Removed NPC {}", target.getObjectId());
      }
    }
    LOG.info("All visible NPC's deleted in Region: {}", getName());
  }

  /** Task of AI notification */
  public class NeighborsTask implements Runnable {
    private final boolean _isActivating;

    public NeighborsTask(boolean isActivating) {
      _isActivating = isActivating;
    }

    @Override
    public void run() {
      if (_isActivating) {
        // for each neighbor, if it's not active, activate.
        for (L2WorldRegion neighbor : getSurroundingRegions()) {
          neighbor.setActive(true);
        }
      } else {
        if (areNeighborsEmpty()) {
          setActive(false);
        }

        // check and deactivate
        for (L2WorldRegion neighbor : getSurroundingRegions()) {
          if (neighbor.areNeighborsEmpty()) {
            neighbor.setActive(false);
          }
        }
      }
    }
  }
}
