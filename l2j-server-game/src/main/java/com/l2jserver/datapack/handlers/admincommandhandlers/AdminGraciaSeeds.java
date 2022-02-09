package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.GraciaSeedsManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.StringTokenizer;

@Service
public class AdminGraciaSeeds implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {
    "admin_gracia_seeds", "admin_kill_tiat", "admin_set_sodstate"
  };

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    StringTokenizer st = new StringTokenizer(command, " ");
    String actualCommand = st.nextToken(); // Get actual command

    String val = "";
    if (st.countTokens() >= 1) {
      val = st.nextToken();
    }

    if (actualCommand.equalsIgnoreCase("admin_kill_tiat")) {
      GraciaSeedsManager.getInstance().increaseSoDTiatKilled();
    } else if (actualCommand.equalsIgnoreCase("admin_set_sodstate")) {
      GraciaSeedsManager.getInstance().setSoDState(Integer.parseInt(val), true);
    }

    showMenu(activeChar);
    return true;
  }

  private void showMenu(L2PcInstance activeChar) {
    final NpcHtmlMessage html = new NpcHtmlMessage();
    html.setFile(activeChar.getHtmlPrefix(), "data/html/admin/graciaseeds.htm");
    html.replace("%sodstate%", String.valueOf(GraciaSeedsManager.getInstance().getSoDState()));
    html.replace(
        "%sodtiatkill%", String.valueOf(GraciaSeedsManager.getInstance().getSoDTiatKilled()));
    if (GraciaSeedsManager.getInstance().getSoDTimeForNextStateChange() > 0) {
      Calendar nextChangeDate = Calendar.getInstance();
      nextChangeDate.setTimeInMillis(
          System.currentTimeMillis()
              + GraciaSeedsManager.getInstance().getSoDTimeForNextStateChange());
      html.replace("%sodtime%", nextChangeDate.getTime().toString());
    } else {
      html.replace("%sodtime%", "-1");
    }
    activeChar.sendPacket(html);
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }
}
