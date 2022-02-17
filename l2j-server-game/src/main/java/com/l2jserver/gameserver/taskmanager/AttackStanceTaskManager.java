package com.l2jserver.gameserver.taskmanager;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2CubicInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.AutoAttackStop;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AttackStanceTaskManager {
  protected static final Logger _log = Logger.getLogger(AttackStanceTaskManager.class.getName());

  protected static final Map<L2Character, Long> _attackStanceTasks = new ConcurrentHashMap<>();
  private final ThreadPoolManager threadPoolManager;

  protected AttackStanceTaskManager(ThreadPoolManager threadPoolManager) {
    this.threadPoolManager = threadPoolManager;
    this.threadPoolManager.scheduleAiAtFixedRate(new FightModeScheduler(), 0, 1000);
  }

  public static AttackStanceTaskManager getInstance() {
    return SingletonHolder._instance;
  }

  public void addAttackStanceTask(L2Character actor) {
    if (actor != null) {
      if (actor.isPlayable()) {
        final L2PcInstance player = actor.getActingPlayer();
        for (L2CubicInstance cubic : player.getCubics().values()) {
          if (cubic.getId() != L2CubicInstance.LIFE_CUBIC) {
            cubic.doAction();
          }
        }
      }
      _attackStanceTasks.put(actor, System.currentTimeMillis());
    }
  }

  public void removeAttackStanceTask(L2Character actor) {
    if (actor != null) {
      if (actor.isSummon()) {
        actor = actor.getActingPlayer();
      }
      _attackStanceTasks.remove(actor);
    }
  }

  /**
   * Checks for attack stance task.<br>
   *
   * @param actor the actor
   * @return {@code true} if the character has an attack stance task, {@code false} otherwise
   */
  public boolean hasAttackStanceTask(L2Character actor) {
    if (actor != null) {
      if (actor.isSummon()) {
        actor = actor.getActingPlayer();
      }
      return _attackStanceTasks.containsKey(actor);
    }
    return false;
  }

  protected static class FightModeScheduler implements Runnable {
    @Override
    public void run() {
      long current = System.currentTimeMillis();
      try {
        final Iterator<Entry<L2Character, Long>> iter = _attackStanceTasks.entrySet().iterator();
        Entry<L2Character, Long> e;
        L2Character actor;
        while (iter.hasNext()) {
          e = iter.next();
          if ((current - e.getValue()) > 15000) {
            actor = e.getKey();
            if (actor != null) {
              actor.broadcastPacket(new AutoAttackStop(actor.getObjectId()));
              actor.getAI().setAutoAttacking(false);
              if (actor.isPlayer() && actor.hasSummon()) {
                actor
                    .getSummon()
                    .broadcastPacket(new AutoAttackStop(actor.getSummon().getObjectId()));
              }
            }
            iter.remove();
          }
        }
      } catch (Exception e) {
        // Unless caught here, players remain in attack positions.
        _log.log(Level.WARNING, "Error in FightModeScheduler: " + e.getMessage(), e);
      }
    }
  }

  private static class SingletonHolder {
    protected static final AttackStanceTaskManager _instance = new AttackStanceTaskManager(null);
  }
}
