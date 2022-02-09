package com.l2jserver.gameserver.status;

import com.l2jserver.commons.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Status extends Thread {

  private static final Logger LOG = LogManager.getLogger(Status.class);

  private final ServerSocket statusServerSocket;

  private final int uptime;

  private String statusPw;

  public Status(int statusPort, String statusPw) throws IOException {
    super("Status");
    this.statusPw = statusPw;

    if (this.statusPw == null) {
      this.statusPw = Util.randomPassword(10);
      LOG.info("Server's Telnet function has no password defined!");
      LOG.info("A password has been automatically created!");
      LOG.info("Password has been set to: {}", this.statusPw);
    }

    statusServerSocket = new ServerSocket(statusPort);
    uptime = (int) System.currentTimeMillis();
    LOG.info("Telnet server started successfully, listening on port {}.", statusPort);
  }

  @Override
  public void run() {
    setPriority(Thread.MAX_PRIORITY);

    while (!isInterrupted()) {
      try {
        Socket connection = statusServerSocket.accept();
        new GameStatusThread(connection, uptime, statusPw);

        if (isInterrupted()) {
          try {
            statusServerSocket.close();
          } catch (Exception ex) {
            LOG.warn("There has been an error closing status server socket!", ex);
          }
          break;
        }
      } catch (Exception ex1) {
        if (isInterrupted()) {
          try {
            statusServerSocket.close();
          } catch (Exception ex2) {
            LOG.warn("There has been an error closing status server socket!", ex2);
          }
          break;
        }
      }
    }
  }
}
