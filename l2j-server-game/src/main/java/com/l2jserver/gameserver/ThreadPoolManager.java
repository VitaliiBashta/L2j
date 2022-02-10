package com.l2jserver.gameserver;

import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.l2jserver.gameserver.config.Configuration.general;

@Service
public class ThreadPoolManager {
  protected static final Logger LOG = LoggerFactory.getLogger(ThreadPoolManager.class);
  private final ScheduledThreadPoolExecutor _effectsScheduledThreadPool;
  private final ScheduledThreadPoolExecutor _generalScheduledThreadPool;
  private final ScheduledThreadPoolExecutor _aiScheduledThreadPool;
  private final ScheduledThreadPoolExecutor _eventScheduledThreadPool;
  private final ThreadPoolExecutor _generalPacketsThreadPool;
  private final ThreadPoolExecutor _ioPacketsThreadPool;
  private final ThreadPoolExecutor _generalThreadPool;
  private final ThreadPoolExecutor _eventThreadPool;
  private boolean _shutdown;

  protected ThreadPoolManager() {
    _effectsScheduledThreadPool =
        new ScheduledThreadPoolExecutor(
            general().getThreadPoolSizeEffects(),
            new PriorityThreadFactory("EffectsSTPool", Thread.NORM_PRIORITY));
    _generalScheduledThreadPool =
        new ScheduledThreadPoolExecutor(
            general().getThreadPoolSizeGeneral(),
            new PriorityThreadFactory("GeneralSTPool", Thread.NORM_PRIORITY));
    _eventScheduledThreadPool =
        new ScheduledThreadPoolExecutor(
            general().getThreadPoolSizeEvents(),
            new PriorityThreadFactory("EventSTPool", Thread.NORM_PRIORITY));
    _ioPacketsThreadPool =
        new ThreadPoolExecutor(
            general().getUrgentPacketThreadCoreSize(),
            Integer.MAX_VALUE,
            5L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new PriorityThreadFactory("I/O Packet Pool", Thread.NORM_PRIORITY + 1));
    _generalPacketsThreadPool =
        new ThreadPoolExecutor(
            general().getGeneralPacketThreadCoreSize(),
            general().getGeneralPacketThreadCoreSize() + 2,
            15L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new PriorityThreadFactory("Normal Packet Pool", Thread.NORM_PRIORITY + 1));
    _generalThreadPool =
        new ThreadPoolExecutor(
            general().getGeneralThreadCoreSize(),
            general().getGeneralThreadCoreSize() + 2,
            5L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new PriorityThreadFactory("General Pool", Thread.NORM_PRIORITY));
    _aiScheduledThreadPool =
        new ScheduledThreadPoolExecutor(
            general().getAiMaxThread(),
            new PriorityThreadFactory("AISTPool", Thread.NORM_PRIORITY));
    _eventThreadPool =
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
      return _effectsScheduledThreadPool.schedule(new RunnableWrapper(task), delay, unit);
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
      return _effectsScheduledThreadPool.scheduleAtFixedRate(
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
      return _generalScheduledThreadPool.schedule(new RunnableWrapper(task), delay, unit);
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
      return _generalScheduledThreadPool.scheduleAtFixedRate(
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
      return _eventScheduledThreadPool.schedule(new RunnableWrapper(task), delay, unit);
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
      return _eventScheduledThreadPool.scheduleAtFixedRate(
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
      return _aiScheduledThreadPool.schedule(new RunnableWrapper(task), delay, unit);
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
      return _aiScheduledThreadPool.scheduleAtFixedRate(
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
      _generalPacketsThreadPool.execute(task);
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
      _ioPacketsThreadPool.execute(task);
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
      _generalThreadPool.execute(new RunnableWrapper(task));
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
      _aiScheduledThreadPool.execute(new RunnableWrapper(task));
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
      _eventThreadPool.execute(new RunnableWrapper(task));
    } catch (RejectedExecutionException e) {
      /* shutdown, ignore */
    }
  }

  public String[] getStats() {
    return new String[] {
      "STP:",
      " + Effects:",
      " |- ActiveThreads:   " + _effectsScheduledThreadPool.getActiveCount(),
      " |- getCorePoolSize: " + _effectsScheduledThreadPool.getCorePoolSize(),
      " |- PoolSize:        " + _effectsScheduledThreadPool.getPoolSize(),
      " |- MaximumPoolSize: " + _effectsScheduledThreadPool.getMaximumPoolSize(),
      " |- CompletedTasks:  " + _effectsScheduledThreadPool.getCompletedTaskCount(),
      " |- ScheduledTasks:  " + _effectsScheduledThreadPool.getQueue().size(),
      " | -------",
      " + General:",
      " |- ActiveThreads:   " + _generalScheduledThreadPool.getActiveCount(),
      " |- getCorePoolSize: " + _generalScheduledThreadPool.getCorePoolSize(),
      " |- PoolSize:        " + _generalScheduledThreadPool.getPoolSize(),
      " |- MaximumPoolSize: " + _generalScheduledThreadPool.getMaximumPoolSize(),
      " |- CompletedTasks:  " + _generalScheduledThreadPool.getCompletedTaskCount(),
      " |- ScheduledTasks:  " + _generalScheduledThreadPool.getQueue().size(),
      " | -------",
      " + AI:",
      " |- ActiveThreads:   " + _aiScheduledThreadPool.getActiveCount(),
      " |- getCorePoolSize: " + _aiScheduledThreadPool.getCorePoolSize(),
      " |- PoolSize:        " + _aiScheduledThreadPool.getPoolSize(),
      " |- MaximumPoolSize: " + _aiScheduledThreadPool.getMaximumPoolSize(),
      " |- CompletedTasks:  " + _aiScheduledThreadPool.getCompletedTaskCount(),
      " |- ScheduledTasks:  " + _aiScheduledThreadPool.getQueue().size(),
      " | -------",
      " + Event:",
      " |- ActiveThreads:   " + _eventScheduledThreadPool.getActiveCount(),
      " |- getCorePoolSize: " + _eventScheduledThreadPool.getCorePoolSize(),
      " |- PoolSize:        " + _eventScheduledThreadPool.getPoolSize(),
      " |- MaximumPoolSize: " + _eventScheduledThreadPool.getMaximumPoolSize(),
      " |- CompletedTasks:  " + _eventScheduledThreadPool.getCompletedTaskCount(),
      " |- ScheduledTasks:  " + _eventScheduledThreadPool.getQueue().size(),
      "TP:",
      " + Packets:",
      " |- ActiveThreads:   " + _generalPacketsThreadPool.getActiveCount(),
      " |- getCorePoolSize: " + _generalPacketsThreadPool.getCorePoolSize(),
      " |- MaximumPoolSize: " + _generalPacketsThreadPool.getMaximumPoolSize(),
      " |- LargestPoolSize: " + _generalPacketsThreadPool.getLargestPoolSize(),
      " |- PoolSize:        " + _generalPacketsThreadPool.getPoolSize(),
      " |- CompletedTasks:  " + _generalPacketsThreadPool.getCompletedTaskCount(),
      " |- QueuedTasks:     " + _generalPacketsThreadPool.getQueue().size(),
      " | -------",
      " + I/O Packets:",
      " |- ActiveThreads:   " + _ioPacketsThreadPool.getActiveCount(),
      " |- getCorePoolSize: " + _ioPacketsThreadPool.getCorePoolSize(),
      " |- MaximumPoolSize: " + _ioPacketsThreadPool.getMaximumPoolSize(),
      " |- LargestPoolSize: " + _ioPacketsThreadPool.getLargestPoolSize(),
      " |- PoolSize:        " + _ioPacketsThreadPool.getPoolSize(),
      " |- CompletedTasks:  " + _ioPacketsThreadPool.getCompletedTaskCount(),
      " |- QueuedTasks:     " + _ioPacketsThreadPool.getQueue().size(),
      " | -------",
      " + General Tasks:",
      " |- ActiveThreads:   " + _generalThreadPool.getActiveCount(),
      " |- getCorePoolSize: " + _generalThreadPool.getCorePoolSize(),
      " |- MaximumPoolSize: " + _generalThreadPool.getMaximumPoolSize(),
      " |- LargestPoolSize: " + _generalThreadPool.getLargestPoolSize(),
      " |- PoolSize:        " + _generalThreadPool.getPoolSize(),
      " |- CompletedTasks:  " + _generalThreadPool.getCompletedTaskCount(),
      " |- QueuedTasks:     " + _generalThreadPool.getQueue().size(),
      " | -------",
      " + Event Tasks:",
      " |- ActiveThreads:   " + _eventThreadPool.getActiveCount(),
      " |- getCorePoolSize: " + _eventThreadPool.getCorePoolSize(),
      " |- MaximumPoolSize: " + _eventThreadPool.getMaximumPoolSize(),
      " |- LargestPoolSize: " + _eventThreadPool.getLargestPoolSize(),
      " |- PoolSize:        " + _eventThreadPool.getPoolSize(),
      " |- CompletedTasks:  " + _eventThreadPool.getCompletedTaskCount(),
      " |- QueuedTasks:     " + _eventThreadPool.getQueue().size(),
      " | -------"
    };
  }

  public void shutdown() {
    _shutdown = true;
    try {
      _effectsScheduledThreadPool.awaitTermination(1, TimeUnit.SECONDS);
      _generalScheduledThreadPool.awaitTermination(1, TimeUnit.SECONDS);
      _generalPacketsThreadPool.awaitTermination(1, TimeUnit.SECONDS);
      _ioPacketsThreadPool.awaitTermination(1, TimeUnit.SECONDS);
      _generalThreadPool.awaitTermination(1, TimeUnit.SECONDS);
      _eventThreadPool.awaitTermination(1, TimeUnit.SECONDS);
      _effectsScheduledThreadPool.shutdown();
      _generalScheduledThreadPool.shutdown();
      _generalPacketsThreadPool.shutdown();
      _ioPacketsThreadPool.shutdown();
      _generalThreadPool.shutdown();
      _eventThreadPool.shutdown();
      LOG.info("All ThreadPools are now stopped");

    } catch (InterruptedException e) {
      LOG.warn("There has been a problem shutting down the thread pool manager!", e);
    }
  }

  public boolean isShutdown() {
    return _shutdown;
  }

  public void purge() {
    _effectsScheduledThreadPool.purge();
    _generalScheduledThreadPool.purge();
    _aiScheduledThreadPool.purge();
    _eventScheduledThreadPool.purge();
    _ioPacketsThreadPool.purge();
    _generalPacketsThreadPool.purge();
    _generalThreadPool.purge();
    _eventThreadPool.purge();
  }

  public String getPacketStats() {
    final StringBuilder sb = new StringBuilder(1000);
    ThreadFactory tf = _generalPacketsThreadPool.getThreadFactory();
    if (tf instanceof PriorityThreadFactory) {
      PriorityThreadFactory ptf = (PriorityThreadFactory) tf;
      int count = ptf.getGroup().activeCount();
      Thread[] threads = new Thread[count + 2];
      ptf.getGroup().enumerate(threads);
      StringUtil.append(
          sb,
          "General Packet Thread Pool:" + Configuration.EOL + "Tasks in the queue: ",
          String.valueOf(_generalPacketsThreadPool.getQueue().size()),
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
    ThreadFactory tf = _ioPacketsThreadPool.getThreadFactory();

    if (tf instanceof PriorityThreadFactory) {
      PriorityThreadFactory ptf = (PriorityThreadFactory) tf;
      int count = ptf.getGroup().activeCount();
      Thread[] threads = new Thread[count + 2];
      ptf.getGroup().enumerate(threads);
      StringUtil.append(
          sb,
          "I/O Packet Thread Pool:" + Configuration.EOL + "Tasks in the queue: ",
          String.valueOf(_ioPacketsThreadPool.getQueue().size()),
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
    ThreadFactory tf = _generalThreadPool.getThreadFactory();

    if (tf instanceof PriorityThreadFactory) {
      PriorityThreadFactory ptf = (PriorityThreadFactory) tf;
      int count = ptf.getGroup().activeCount();
      Thread[] threads = new Thread[count + 2];
      ptf.getGroup().enumerate(threads);
      StringUtil.append(
          sb,
          "General Thread Pool:" + Configuration.EOL + "Tasks in the queue: ",
          String.valueOf(_generalThreadPool.getQueue().size()),
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
