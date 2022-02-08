package com.l2jserver.gameserver.util;

import com.l2jserver.gameserver.Shutdown;
import com.l2jserver.gameserver.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.*;
import java.util.Arrays;

import static com.l2jserver.gameserver.config.Configuration.general;

public class DeadLockDetector extends Thread {
  private static final Logger _log = LogManager.getLogger(DeadLockDetector.class);

  private final ThreadMXBean tmx;

  public DeadLockDetector() {
    super("DeadLockDetector");
    tmx = ManagementFactory.getThreadMXBean();
  }

  @Override
  public final void run() {
    boolean deadlock = false;
    while (!deadlock) {

      long[] ids = tmx.findDeadlockedThreads();

      if (ids != null) {
        deadlock = true;
        ThreadInfo[] tis = tmx.getThreadInfo(ids, true, true);
        StringBuilder info = new StringBuilder();
        info.append("DeadLock Found!");
        info.append(Configuration.EOL);
        Arrays.stream(tis).map(ThreadInfo::toString).forEach(info::append);

        for (ThreadInfo ti : tis) {
          LockInfo[] locks = ti.getLockedSynchronizers();
          MonitorInfo[] monitors = ti.getLockedMonitors();
          if ((locks.length == 0) && (monitors.length == 0)) {
            continue;
          }

          formMessage(info, ti);
        }
        String message = info.toString();
        _log.warn(message);

        if (general().restartOnDeadlock()) {
          Broadcast.toAllOnlinePlayers("Server has stability issues - restarting now.");
          Shutdown.getInstance().startTelnetShutdown("DeadLockDetector - Auto Restart", 60, true);
        }
      }
      wait(general().getDeadLockCheckInterval());
    }
  }

  private void wait(Integer interval) {
    try {
      Thread.sleep(interval);
    } catch (InterruptedException e) {
      _log.warn("DeadLockDetector: ", e);
    }
  }

  private void formMessage(StringBuilder info, ThreadInfo ti) {
    info.append("Java-level deadlock:")
        .append(Configuration.EOL)
        .append('\t')
        .append(ti.getThreadName())
        .append(" is waiting to lock ")
        .append(ti.getLockInfo().toString())
        .append(" which is held by ")
        .append(ti.getLockOwnerName())
        .append(Configuration.EOL);
    while ((ti = tmx.getThreadInfo(new long[] {ti.getLockOwnerId()}, true, true)[0]).getThreadId()
        != ti.getThreadId()) {
      info.append('\t');
      info.append(ti.getThreadName());
      info.append(" is waiting to lock ");
      info.append(ti.getLockInfo().toString());
      info.append(" which is held by ");
      info.append(ti.getLockOwnerName());
      info.append(Configuration.EOL);
    }
  }
}
