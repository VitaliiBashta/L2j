package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.Shutdown;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.Util;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class handles following admin commands: - server_shutdown [sec] = shows menu or shuts down
 * server in sec seconds
 */
@Service
public class AdminShutdown implements IAdminCommandHandler {
  // private static Logger _log = Logger.getLogger(AdminShutdown.class.getName());

  private static final String[] ADMIN_COMMANDS = {
    "admin_server_shutdown", "admin_server_restart", "admin_server_abort"
  };

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    if (command.startsWith("admin_server_shutdown")) {
      try {
        final String val = command.substring(22);
        if (Util.isDigit(val)) {
          serverShutdown(activeChar, Integer.valueOf(val), false);
        } else {
          activeChar.sendMessage("Usage: //server_shutdown <seconds>");
          sendHtmlForm(activeChar);
        }
      } catch (StringIndexOutOfBoundsException e) {
        sendHtmlForm(activeChar);
      }
    } else if (command.startsWith("admin_server_restart")) {
      try {
        final String val = command.substring(21);
        if (Util.isDigit(val)) {
          serverShutdown(activeChar, Integer.parseInt(val), true);
        } else {
          activeChar.sendMessage("Usage: //server_restart <seconds>");
          sendHtmlForm(activeChar);
        }
      } catch (StringIndexOutOfBoundsException e) {
        sendHtmlForm(activeChar);
      }
    } else if (command.startsWith("admin_server_abort")) {
      serverAbort(activeChar);
    }

    return true;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }

  private void sendHtmlForm(L2PcInstance activeChar) {
    final NpcHtmlMessage adminReply = new NpcHtmlMessage();
    int t = GameTimeController.getInstance().getGameTime();
    int h = t / 60;
    int m = t % 60;
    SimpleDateFormat format = new SimpleDateFormat("h:mm a");
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, h);
    cal.set(Calendar.MINUTE, m);
    adminReply.setFile(activeChar.getHtmlPrefix(), "data/html/admin/shutdown.htm");
    adminReply.replace("%count%", String.valueOf(L2World.getInstance().getAllPlayersCount()));
    adminReply.replace(
        "%used%",
        String.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
    adminReply.replace("%time%", String.valueOf(format.format(cal.getTime())));
    activeChar.sendPacket(adminReply);
  }

  private void serverShutdown(L2PcInstance activeChar, int seconds, boolean restart) {
    Shutdown.getInstance().startShutdown(activeChar, seconds, restart);
  }

  private void serverAbort(L2PcInstance activeChar) {
    Shutdown.getInstance().abort(activeChar);
  }
}
