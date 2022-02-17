package com.l2jserver.gameserver;

import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.l2jserver.gameserver.config.Configuration.general;

@Service
public class ThreadPoolManager {
  protected static final Logger LOG = LoggerFactory.getLogger(ThreadPoolManager.class);
  private final ScheduledThreadPoolExecutor effectsScheduledThreadPool;
  private final ScheduledThreadPoolExecutor generalScheduledThreadPool;
  private final ScheduledThreadPoolExecutor aiScheduledThreadPool;
  private final ScheduledThreadPoolExecutor eventScheduledThreadPool;
  private final ThreadPoolExecutor generalPacketsThreadPool;
  private final ThreadPoolExecutor ioPacketsThreadPool;
  private final ThreadPoolExecutor generalThreadPool;
  private final ThreadPoolExecutor eventThreadPool;
  private boolean _shutdown;

  protected ThreadPoolManager() {
    effectsScheduledThreadPool =
        new ScheduledThreadPoolExecutor(
            general().getThreadPoolSizeEffects(),
            new PriorityThreadFactory("EffectsSTPool", Thread.NORM_PRIORITY));
    generalScheduledThreadPool =
        new ScheduledThreadPoolExecutor(
            general().getThreadPoolSizeGeneral(),
            new PriorityThreadFactory("GeneralSTPool", Thread.NORM_PRIORITY));
    eventScheduledThreadPool =
        new ScheduledThreadPoolExecutor(
            general().getThreadPoolSizeEvents(),
            new PriorityThreadFactory("EventSTPool", Thread.NORM_PRIORITY));
    ioPacketsThreadPool =
        new ThreadPoolExecutor(
            general().getUrgentPacketThreadCoreSize(),
            Integer.MAX_VALUE,
            5L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new PriorityThreadFactory("I/O Packet Pool", Thread.NORM_PRIORITY + 1));
    generalPacketsThreadPool =
        new ThreadPoolExecutor(
            general().getGeneralPacketThreadCoreSize(),
            general().getGeneralPacketThreadCoreSize() + 2,
            15L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new PriorityThreadFactory("Normal Packet Pool", Thread.NORM_PRIORITY + 1));
    generalThreadPool =
        new ThreadPoolExecutor(
            general().getGeneralThreadCoreSize(),
            general().getGeneralThreadCoreSize() + 2,
            5L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new PriorityThreadFactory("General Pool", Thread.NORM_PRIORITY));
    aiScheduledThreadPool =
        new ScheduledThreadPoolExecutor(
            general().getAiMaxThread(),
            new PriorityThreadFactory("AISTPool", Thread.NORM_PRIORITY));
    eventThreadPool =
        new ThreadPoolExecutor(
            general().getEventsMaxThread(),
            general().getEventsMaxThread() + 2,
            5L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new PriorityThreadFactory("Event Pool", Thread.NORM_PRIORITY));

    //    scheduleGeneralAtFixedRate(
    //        new PurgeTask(
    //            _effectsScheduledThreadPool,
    //            _generalScheduledThreadPool,
    //            _aiScheduledThreadPool,
    //            _eventThreadPool),
    //        10,
    //        5,
    //        TimeUnit.MINUTES);
  }

  public static ThreadPoolManager getInstance() {
    return SingletonHolder._instance;
  }

  /**
   * Schedules an effect task to be executed after the given delay.
   *
   * @param task the task to execute
   * @param delay the delay in the given time unit
   * @param unit the time unit of the delay parameter
   * @return a ScheduledFuture representing pending completion of the task, and whose get() method
   *     will throw an exception upon cancellation
   */
  public ScheduledFuture<?> scheduleEffect(Runnable task, long delay, TimeUnit unit) {
    try {
      return effectsScheduledThreadPool.schedule(new RunnableWrapper(task), delay, unit);
    } catch (RejectedExecutionException e) {
      return null;
    }
  }

  /**
   * Schedules an effect task to be executed after the given delay.
   *
   * @param task the task to execute
   * @param delay the delay in milliseconds
   * @return a ScheduledFuture representing pending completion of the task, and whose get() method
   *     will throw an exception upon cancellation
   */
  public ScheduledFuture<?> scheduleEffect(Runnable task, long delay) {
    return scheduleEffect(task, delay, TimeUnit.MILLISECONDS);
  }

  /**
   * Schedules an effect task to be executed at fixed rate.
   *
   * @param task the task to execute
   * @param initialDelay the initial delay in the given time unit
   * @param period the period between executions in the given time unit
   * @param unit the time unit of the initialDelay and period parameters
   * @return a ScheduledFuture representing pending completion of the task, and whose get() method
   *     will throw an exception upon cancellation
   */
  public ScheduledFuture<?> scheduleEffectAtFixedRate(
      Runnable task, long initialDelay, long period, TimeUnit unit) {
    try {
      return effectsScheduledThreadPool.scheduleAtFixedRate(
          new RunnableWrapper(task), initialDelay, period, unit);
    } catch (RejectedExecutionException e) {
      return null; /* shutdown, ignore */
    }
  }

  /**
   * Schedules an effect task to be executed at fixed rate.
   *
   * @param task the task to execute
   * @param initialDelay the initial delay in milliseconds
   * @param period the period between executions in milliseconds
   * @return a ScheduledFuture representing pending completion of the task, and whose get() method
   *     will throw an exception upon cancellation
   */
  public ScheduledFuture<?> scheduleEffectAtFixedRate(
      Runnable task, long initialDelay, long period) {
    return scheduleEffectAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
  }

  /**
   * Schedules a general task to be executed after the given delay.
   *
   * @param task the task to execute
   * @param delay the delay in the given time unit
   * @param unit the time unit of the delay parameter
   * @return a ScheduledFuture representing pending completion of the task, and whose get() method
   *     will throw an exception upon cancellation
   */
  public ScheduledFuture<?> scheduleGeneral(Runnable task, long delay, TimeUnit unit) {
    try {
      return generalScheduledThreadPool.schedule(new RunnableWrapper(task), delay, unit);
    } catch (RejectedExecutionException e) {
      return null; /* shutdown, ignore */
    }
  }

  /**
   * Schedules a general task to be executed after the given delay.
   *
   * @param task the task to execute
   * @param delay the delay in milliseconds
   * @return a ScheduledFuture representing pending completion of the task, and whose get() method
   *     will throw an exception upon cancellation
   */
  public ScheduledFuture<?> scheduleGeneral(Runnable task, long delay) {
    return scheduleGeneral(task, delay, TimeUnit.MILLISECONDS);
  }

  /**
   * Schedules a general task to be executed at fixed rate.
   *
   * @param task the task to execute
   * @param initialDelay the initial delay in the given time unit
   * @param period the period between executions in the given time unit
   * @param unit the time unit of the initialDelay and period parameters
   * @return a ScheduledFuture representing pending completion of the task, and whose get() method
   *     will throw an exception upon cancellation
   */
  public ScheduledFuture<?> scheduleGeneralAtFixedRate(
      Runnable task, long initialDelay, long period, TimeUnit unit) {
    try {
      return generalScheduledThreadPool.scheduleAtFixedRate(
          new RunnableWrapper(task), initialDelay, period, unit);
    } catch (RejectedExecutionException e) {
      return null; /* shutdown, ignore */
    }
  }

  /**
   * Schedules a event task to be executed after the given delay.
   *
   * @param task the task to execute
   * @param delay the delay in the given time unit
   * @param unit the time unit of the delay parameter
   * @return a ScheduledFuture representing pending completion of the task, and whose get() method
   *     will throw an exception upon cancellation
   */
  public ScheduledFuture<?> scheduleEvent(Runnable task, long delay, TimeUnit unit) {
    try {
      return eventScheduledThreadPool.schedule(new RunnableWrapper(task), delay, unit);
    } catch (RejectedExecutionException e) {
      return null; /* shutdown, ignore */
    }
  }

  /**
   * Schedules a event task to be executed after the given delay.
   *
   * @param task the task to execute
   * @param delay the delay in milliseconds
   * @return a ScheduledFuture representing pending completion of the task, and whose get() method
   *     will throw an exception upon cancellation
   */
  public ScheduledFuture<?> scheduleEvent(Runnable task, long delay) {
    return scheduleEvent(task, delay, TimeUnit.MILLISECONDS);
  }

  /**
   * Schedules a event task to be executed at fixed rate.
   *
   * @param task the task to execute
   * @param initialDelay the initial delay in the given time unit
   * @param period the period between executions in the given time unit
   * @param unit the time unit of the initialDelay and period parameters
   * @return a ScheduledFuture representing pending completion of the task, and whose get() method
   *     will throw an exception upon cancellation
   */
  public ScheduledFuture<?> scheduleEventAtFixedRate(
      Runnable task, long initialDelay, long period, TimeUnit unit) {
    try {
      return eventScheduledThreadPool.scheduleAtFixedRate(
          new RunnableWrapper(task), initialDelay, period, unit);
    } catch (RejectedExecutionException e) {
      return null; /* shutdown, ignore */
    }
  }

  /**
   * Schedules a general task to be executed at fixed rate.
   *
   * @param task the task to execute
   * @param initialDelay the initial delay in milliseconds
   * @param period the period between executions in milliseconds
   * @return a ScheduledFuture representing pending completion of the task, and whose get() method
   *     will throw an exception upon cancellation
   */
  public ScheduledFuture<?> scheduleGeneralAtFixedRate(
      Runnable task, long initialDelay, long period) {
    return scheduleGeneralAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
  }

  /**
   * Schedules an AI task to be executed after the given delay.
   *
   * @param task the task to execute
   * @param delay the delay in the given time unit
   * @param unit the time unit of the delay parameter
   * @return a ScheduledFuture representing pending completion of the task, and whose get() method
   *     will throw an exception upon cancellation
   */
  public ScheduledFuture<?> scheduleAi(Runnable task, long delay, TimeUnit unit) {
    try {
      return aiScheduledThreadPool.schedule(new RunnableWrapper(task), delay, unit);
    } catch (RejectedExecutionException e) {
      return null; /* shutdown, ignore */
    }
  }

  /**
   * Schedules an AI task to be executed after the given delay.
   *
   * @param task the task to execute
   * @param delay the delay in milliseconds
   * @return a ScheduledFuture representing pending completion of the task, and whose get() method
   *     will throw an exception upon cancellation
   */
  public ScheduledFuture<?> scheduleAi(Runnable task, long delay) {
    return scheduleAi(task, delay, TimeUnit.MILLISECONDS);
  }

  /**
   * Schedules a general task to be executed at fixed rate.
   *
   * @param task the task to execute
   * @param initialDelay the initial delay in the given time unit
   * @param period the period between executions in the given time unit
   * @param unit the time unit of the initialDelay and period parameters
   * @return a ScheduledFuture representing pending completion of the task, and whose get() method
   *     will throw an exception upon cancellation
   */
  public ScheduledFuture<?> scheduleAiAtFixedRate(
      Runnable task, long initialDelay, long period, TimeUnit unit) {
    try {
      return aiScheduledThreadPool.scheduleAtFixedRate(
          new RunnableWrapper(task), initialDelay, period, unit);
    } catch (RejectedExecutionException e) {
      return null; /* shutdown, ignore */
    }
  }

  /**
   * Schedules a general task to be executed at fixed rate.
   *
   * @param task the task to execute
   * @param initialDelay the initial delay in milliseconds
   * @param period the period between executions in milliseconds
   * @return a ScheduledFuture representing pending completion of the task, and whose get() method
   *     will throw an exception upon cancellation
   */
  public ScheduledFuture<?> scheduleAiAtFixedRate(Runnable task, long initialDelay, long period) {
    return scheduleAiAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
  }

  /**
   * Executes a packet task sometime in future in another thread.
   *
   * @param task the task to execute
   */
  public void executePacket(Runnable task) {
    try {
      generalPacketsThreadPool.execute(task);
    } catch (RejectedExecutionException e) {
      /* shutdown, ignore */
    }
  }

  /**
   * Executes an IO packet task sometime in future in another thread.
   *
   * @param task the task to execute
   */
  public void executeIOPacket(Runnable task) {
    try {
      ioPacketsThreadPool.execute(task);
    } catch (RejectedExecutionException e) {
      /* shutdown, ignore */
    }
  }

  /**
   * Executes a general task sometime in future in another thread.
   *
   * @param task the task to execute
   */
  public void executeGeneral(Runnable task) {
    try {
      generalThreadPool.execute(new RunnableWrapper(task));
    } catch (RejectedExecutionException e) {
      /* shutdown, ignore */
    }
  }

  /**
   * Executes an AI task sometime in future in another thread.
   *
   * @param task the task to execute
   */
  public void executeAi(Runnable task) {
    try {
      aiScheduledThreadPool.execute(new RunnableWrapper(task));
    } catch (RejectedExecutionException e) {
      /* shutdown, ignore */
    }
  }

  /**
   * Executes an Event task sometime in future in another thread.
   *
   * @param task the task to execute
   */
  public void executeEvent(Runnable task) {
    try {
      eventThreadPool.execute(new RunnableWrapper(task));
    } catch (RejectedExecutionException e) {
      /* shutdown, ignore */
    }
  }

  public String[] getStats() {
    return new String[] {
      "STP:",
      " + Effects:",
      " |- ActiveThreads:   " + effectsScheduledThreadPool.getActiveCount(),
      " |- getCorePoolSize: " + effectsScheduledThreadPool.getCorePoolSize(),
      " |- PoolSize:        " + effectsScheduledThreadPool.getPoolSize(),
      " |- MaximumPoolSize: " + effectsScheduledThreadPool.getMaximumPoolSize(),
      " |- CompletedTasks:  " + effectsScheduledThreadPool.getCompletedTaskCount(),
      " |- ScheduledTasks:  " + effectsScheduledThreadPool.getQueue().size(),
      " | -------",
      " + General:",
      " |- ActiveThreads:   " + generalScheduledThreadPool.getActiveCount(),
      " |- getCorePoolSize: " + generalScheduledThreadPool.getCorePoolSize(),
      " |- PoolSize:        " + generalScheduledThreadPool.getPoolSize(),
      " |- MaximumPoolSize: " + generalScheduledThreadPool.getMaximumPoolSize(),
      " |- CompletedTasks:  " + generalScheduledThreadPool.getCompletedTaskCount(),
      " |- ScheduledTasks:  " + generalScheduledThreadPool.getQueue().size(),
      " | -------",
      " + AI:",
      " |- ActiveThreads:   " + aiScheduledThreadPool.getActiveCount(),
      " |- getCorePoolSize: " + aiScheduledThreadPool.getCorePoolSize(),
      " |- PoolSize:        " + aiScheduledThreadPool.getPoolSize(),
      " |- MaximumPoolSize: " + aiScheduledThreadPool.getMaximumPoolSize(),
      " |- CompletedTasks:  " + aiScheduledThreadPool.getCompletedTaskCount(),
      " |- ScheduledTasks:  " + aiScheduledThreadPool.getQueue().size(),
      " | -------",
      " + Event:",
      " |- ActiveThreads:   " + eventScheduledThreadPool.getActiveCount(),
      " |- getCorePoolSize: " + eventScheduledThreadPool.getCorePoolSize(),
      " |- PoolSize:        " + eventScheduledThreadPool.getPoolSize(),
      " |- MaximumPoolSize: " + eventScheduledThreadPool.getMaximumPoolSize(),
      " |- CompletedTasks:  " + eventScheduledThreadPool.getCompletedTaskCount(),
      " |- ScheduledTasks:  " + eventScheduledThreadPool.getQueue().size(),
      "TP:",
      " + Packets:",
      " |- ActiveThreads:   " + generalPacketsThreadPool.getActiveCount(),
      " |- getCorePoolSize: " + generalPacketsThreadPool.getCorePoolSize(),
      " |- MaximumPoolSize: " + generalPacketsThreadPool.getMaximumPoolSize(),
      " |- LargestPoolSize: " + generalPacketsThreadPool.getLargestPoolSize(),
      " |- PoolSize:        " + generalPacketsThreadPool.getPoolSize(),
      " |- CompletedTasks:  " + generalPacketsThreadPool.getCompletedTaskCount(),
      " |- QueuedTasks:     " + generalPacketsThreadPool.getQueue().size(),
      " | -------",
      " + I/O Packets:",
      " |- ActiveThreads:   " + ioPacketsThreadPool.getActiveCount(),
      " |- getCorePoolSize: " + ioPacketsThreadPool.getCorePoolSize(),
      " |- MaximumPoolSize: " + ioPacketsThreadPool.getMaximumPoolSize(),
      " |- LargestPoolSize: " + ioPacketsThreadPool.getLargestPoolSize(),
      " |- PoolSize:        " + ioPacketsThreadPool.getPoolSize(),
      " |- CompletedTasks:  " + ioPacketsThreadPool.getCompletedTaskCount(),
      " |- QueuedTasks:     " + ioPacketsThreadPool.getQueue().size(),
      " | -------",
      " + General Tasks:",
      " |- ActiveThreads:   " + generalThreadPool.getActiveCount(),
      " |- getCorePoolSize: " + generalThreadPool.getCorePoolSize(),
      " |- MaximumPoolSize: " + generalThreadPool.getMaximumPoolSize(),
      " |- LargestPoolSize: " + generalThreadPool.getLargestPoolSize(),
      " |- PoolSize:        " + generalThreadPool.getPoolSize(),
      " |- CompletedTasks:  " + generalThreadPool.getCompletedTaskCount(),
      " |- QueuedTasks:     " + generalThreadPool.getQueue().size(),
      " | -------",
      " + Event Tasks:",
      " |- ActiveThreads:   " + eventThreadPool.getActiveCount(),
      " |- getCorePoolSize: " + eventThreadPool.getCorePoolSize(),
      " |- MaximumPoolSize: " + eventThreadPool.getMaximumPoolSize(),
      " |- LargestPoolSize: " + eventThreadPool.getLargestPoolSize(),
      " |- PoolSize:        " + eventThreadPool.getPoolSize(),
      " |- CompletedTasks:  " + eventThreadPool.getCompletedTaskCount(),
      " |- QueuedTasks:     " + eventThreadPool.getQueue().size(),
      " | -------"
    };
  }

  public void shutdown() {
    _shutdown = true;
    try {
      effectsScheduledThreadPool.awaitTermination(1, TimeUnit.SECONDS);
      generalScheduledThreadPool.awaitTermination(1, TimeUnit.SECONDS);
      generalPacketsThreadPool.awaitTermination(1, TimeUnit.SECONDS);
      ioPacketsThreadPool.awaitTermination(1, TimeUnit.SECONDS);
      generalThreadPool.awaitTermination(1, TimeUnit.SECONDS);
      eventThreadPool.awaitTermination(1, TimeUnit.SECONDS);
      effectsScheduledThreadPool.shutdown();
      generalScheduledThreadPool.shutdown();
      generalPacketsThreadPool.shutdown();
      ioPacketsThreadPool.shutdown();
      generalThreadPool.shutdown();
      eventThreadPool.shutdown();
      LOG.info("All ThreadPools are now stopped");

    } catch (InterruptedException e) {
      LOG.warn("There has been a problem shutting down the thread pool manager!", e);
    }
  }

  public boolean isShutdown() {
    return _shutdown;
  }

  public void purge() {
    effectsScheduledThreadPool.purge();
    generalScheduledThreadPool.purge();
    aiScheduledThreadPool.purge();
    eventScheduledThreadPool.purge();
    ioPacketsThreadPool.purge();
    generalPacketsThreadPool.purge();
    generalThreadPool.purge();
    eventThreadPool.purge();
  }

  public String getPacketStats() {
    final StringBuilder sb = new StringBuilder(1000);
    ThreadFactory tf = generalPacketsThreadPool.getThreadFactory();
    if (tf instanceof PriorityThreadFactory) {
      PriorityThreadFactory ptf = (PriorityThreadFactory) tf;
      int count = ptf.getGroup().activeCount();
      Thread[] threads = new Thread[count + 2];
      ptf.getGroup().enumerate(threads);
      StringUtil.append(
          sb,
          "General Packet Thread Pool:" + Configuration.EOL + "Tasks in the queue: ",
          String.valueOf(generalPacketsThreadPool.getQueue().size()),
          Configuration.EOL
              + "Showing threads stack trace:"
              + Configuration.EOL
              + "There should be ",
          String.valueOf(count),
          " Threads" + Configuration.EOL);
      for (Thread t : threads) {
        if (t == null) {
          continue;
        }

        StringUtil.append(sb, t.getName(), Configuration.EOL);
        for (StackTraceElement ste : t.getStackTrace()) {
          StringUtil.append(sb, ste.toString(), Configuration.EOL);
        }
      }
    }

    sb.append("Packet Tp stack traces printed.");
    sb.append(Configuration.EOL);
    return sb.toString();
  }

  public String getIOPacketStats() {
    final StringBuilder sb = new StringBuilder(1000);
    ThreadFactory tf = ioPacketsThreadPool.getThreadFactory();

    if (tf instanceof PriorityThreadFactory) {
      PriorityThreadFactory ptf = (PriorityThreadFactory) tf;
      int count = ptf.getGroup().activeCount();
      Thread[] threads = new Thread[count + 2];
      ptf.getGroup().enumerate(threads);
      StringUtil.append(
          sb,
          "I/O Packet Thread Pool:" + Configuration.EOL + "Tasks in the queue: ",
          String.valueOf(ioPacketsThreadPool.getQueue().size()),
          Configuration.EOL
              + "Showing threads stack trace:"
              + Configuration.EOL
              + "There should be ",
          String.valueOf(count),
          " Threads" + Configuration.EOL);

      for (Thread t : threads) {
        if (t == null) {
          continue;
        }

        StringUtil.append(sb, t.getName(), Configuration.EOL);

        for (StackTraceElement ste : t.getStackTrace()) {
          StringUtil.append(sb, ste.toString(), Configuration.EOL);
        }
      }
    }

    sb.append("Packet Tp stack traces printed.");
    sb.append(Configuration.EOL);
    return sb.toString();
  }

  public String getGeneralStats() {
    final StringBuilder sb = new StringBuilder(1000);
    ThreadFactory tf = generalThreadPool.getThreadFactory();

    if (tf instanceof PriorityThreadFactory) {
      PriorityThreadFactory ptf = (PriorityThreadFactory) tf;
      int count = ptf.getGroup().activeCount();
      Thread[] threads = new Thread[count + 2];
      ptf.getGroup().enumerate(threads);
      StringUtil.append(
          sb,
          "General Thread Pool:" + Configuration.EOL + "Tasks in the queue: ",
          String.valueOf(generalThreadPool.getQueue().size()),
          Configuration.EOL
              + "Showing threads stack trace:"
              + Configuration.EOL
              + "There should be ",
          String.valueOf(count),
          " Threads" + Configuration.EOL);

      for (Thread t : threads) {
        if (t == null) {
          continue;
        }

        StringUtil.append(sb, t.getName(), Configuration.EOL);

        for (StackTraceElement ste : t.getStackTrace()) {
          StringUtil.append(sb, ste.toString(), Configuration.EOL);
        }
      }
    }

    sb.append("Packet Tp stack traces printed.");
    sb.append(Configuration.EOL);
    return sb.toString();
  }

  private static final class RunnableWrapper implements Runnable {
    private final Runnable _r;

    public RunnableWrapper(final Runnable r) {
      _r = r;
    }

    @Override
    public void run() {
      try {
        _r.run();
      } catch (final Throwable e) {
        final Thread t = Thread.currentThread();
        final UncaughtExceptionHandler h = t.getUncaughtExceptionHandler();
        if (h != null) {
          h.uncaughtException(t, e);
        }
      }
    }
  }

  private static class PriorityThreadFactory implements ThreadFactory {
    private final int _priority;
    private final String _name;
    private final AtomicInteger _threadNumber = new AtomicInteger(1);
    private final ThreadGroup _group;

    public PriorityThreadFactory(String name, int priority) {
      _priority = priority;
      _name = name;
      _group = new ThreadGroup(_name);
    }

    @Override
    public Thread newThread(Runnable r) {
      Thread t = new Thread(_group, r, _name + "-" + _threadNumber.getAndIncrement());
      t.setPriority(_priority);
      return t;
    }

    public ThreadGroup getGroup() {
      return _group;
    }
  }

  protected static class PurgeTask implements Runnable {
    private final ScheduledThreadPoolExecutor _effectsScheduled;

    private final ScheduledThreadPoolExecutor _generalScheduled;

    private final ScheduledThreadPoolExecutor _aiScheduled;

    private final ThreadPoolExecutor _eventScheduled;

    PurgeTask(
        ScheduledThreadPoolExecutor effectsScheduledThreadPool,
        ScheduledThreadPoolExecutor generalScheduledThreadPool, //
        ScheduledThreadPoolExecutor aiScheduledThreadPool,
        ThreadPoolExecutor eventScheduledThreadPool) {
      _effectsScheduled = effectsScheduledThreadPool;
      _generalScheduled = generalScheduledThreadPool;
      _aiScheduled = aiScheduledThreadPool;
      _eventScheduled = eventScheduledThreadPool;
    }

    @Override
    public void run() {
      _effectsScheduled.purge();
      _generalScheduled.purge();
      _aiScheduled.purge();
      _eventScheduled.purge();
    }
  }

  private static class SingletonHolder {
    protected static final ThreadPoolManager _instance = new ThreadPoolManager();
  }
}
