package com.l2jserver.gameserver;

import com.l2jserver.gameserver.instancemanager.DayNightSpawnManager;
import com.l2jserver.gameserver.model.actor.L2Character;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameTimeController extends Thread {
  public static final int TICKS_PER_SECOND =
      10; // not able to change this without checking through code
  public static final int MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;
  public static final int IG_DAYS_PER_DAY = 6;
  public static final int MILLIS_PER_IG_DAY = (3600000 * 24) / IG_DAYS_PER_DAY;
  public static final int SECONDS_PER_IG_DAY = MILLIS_PER_IG_DAY / 1000;
  public static final int MINUTES_PER_IG_DAY = SECONDS_PER_IG_DAY / 60;
  public static final int TICKS_PER_IG_DAY = SECONDS_PER_IG_DAY * TICKS_PER_SECOND;
  public static final int TICKS_SUN_STATE_CHANGE = TICKS_PER_IG_DAY / 4;
  private static final Logger LOG = LoggerFactory.getLogger(GameTimeController.class);
  private static GameTimeController _instance;

  private final Set<L2Character> _movingObjects = ConcurrentHashMap.newKeySet();
  private final long referenceTime;
  private final ThreadPoolManager threadPoolManager;

  private GameTimeController(ThreadPoolManager threadPoolManager) {
    super("GameTimeController");
    this.threadPoolManager = threadPoolManager;
    super.setDaemon(true);
    super.setPriority(MAX_PRIORITY);

    final Calendar c = Calendar.getInstance();
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    referenceTime = c.getTimeInMillis();
  }

  public static GameTimeController getInstance() {
    return _instance;
  }

  public int getGameMinute() {
    return getGameTime() % 60;
  }

  public int getGameTime() {
    return (getGameTicks() % TICKS_PER_IG_DAY) / MILLIS_IN_TICK;
  }

  /**
   * The true GameTime tick. Directly taken from current time. This represents the tick of the time.
   *
   * @return
   */
  public int getGameTicks() {
    return (int) ((System.currentTimeMillis() - referenceTime) / MILLIS_IN_TICK);
  }

  /**
   * Add a L2Character to movingObjects of GameTimeController.
   *
   * @param cha The L2Character to add to movingObjects of GameTimeController
   */
  public void registerMovingObject(final L2Character cha) {
    if (cha == null) {
      return;
    }

    _movingObjects.add(cha);
  }

  public void stopTimer() {
    super.interrupt();
    LOG.info("Stopping {}", getClass().getSimpleName());
  }

  @Override
  public void run() {
    LOG.debug("{}: Started.", getClass().getSimpleName());

    long nextTickTime, sleepTime;
    boolean isNight = isNight();

    if (isNight) {
      threadPoolManager.executeAi(() -> DayNightSpawnManager.getInstance().notifyChangeMode());
    }

    while (true) {
      nextTickTime = ((System.currentTimeMillis() / MILLIS_IN_TICK) * MILLIS_IN_TICK) + 100;

      try {
        moveObjects();
      } catch (final Throwable e) {
        LOG.warn("Unable to move objects!", e);
      }

      sleepTime = nextTickTime - System.currentTimeMillis();
      if (sleepTime > 0) {
        try {
          Thread.sleep(sleepTime);
        } catch (final InterruptedException e) {

        }
      }

      if (isNight() != isNight) {
        isNight = !isNight;

        ThreadPoolManager.getInstance()
            .executeAi(() -> DayNightSpawnManager.getInstance().notifyChangeMode());
      }
    }
  }

  public boolean isNight() {
    return getGameHour() < 6;
  }

  public int getGameHour() {
    return getGameTime() / 60;
  }

  /**
   * Move all L2Characters contained in movingObjects of GameTimeController.<br>
   * <B><U> Concept</U> :</B><br>
   * All L2Character in movement are identified in <B>movingObjects</B> of GameTimeController.<br>
   * <B><U> Actions</U> :</B><br>
   *
   * <ul>
   *   <li>Update the position of each L2Character
   *   <li>If movement is finished, the L2Character is removed from movingObjects
   *   <li>Create a task to update the _knownObject and _knowPlayers of each L2Character that
   *       finished its movement and of their already known L2Object then notify AI with EVT_ARRIVED
   * </ul>
   */
  private void moveObjects() {
    _movingObjects.removeIf(L2Character::updatePosition);
  }
}
