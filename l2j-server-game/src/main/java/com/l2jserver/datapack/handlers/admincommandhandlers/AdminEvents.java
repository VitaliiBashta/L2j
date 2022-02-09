package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Event;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.StringTokenizer;

@Service
public class AdminEvents implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {
    "admin_event_menu",
    "admin_event_start",
    "admin_event_stop",
    "admin_event_start_menu",
    "admin_event_stop_menu",
    "admin_event_bypass"
  };

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    if (activeChar == null) {
      return false;
    }

    String event_name = "";
    String _event_bypass = "";
    StringTokenizer st = new StringTokenizer(command, " ");
    st.nextToken();
    if (st.hasMoreTokens()) {
      event_name = st.nextToken();
    }
    if (st.hasMoreTokens()) {
      _event_bypass = st.nextToken();
    }

    if (command.contains("_menu")) {
      showMenu(activeChar);
    }

    if (command.startsWith("admin_event_start")) {
      try {
        if (event_name != null) {
          Event event = (Event) QuestManager.getInstance().getQuest(event_name);
          if (event != null) {
            if (event.eventStart(activeChar)) {
              activeChar.sendMessage("Event " + event_name + " started.");
              return true;
            }

            activeChar.sendMessage("There is problem starting " + event_name + " event.");
            return true;
          }
        }
      } catch (Exception e) {
        activeChar.sendMessage("Usage: //event_start <eventname>");
        e.printStackTrace();
        return false;
      }
    } else if (command.startsWith("admin_event_stop")) {
      try {
        if (event_name != null) {
          Event event = (Event) QuestManager.getInstance().getQuest(event_name);
          if (event != null) {
            if (event.eventStop()) {
              activeChar.sendMessage("Event " + event_name + " stopped.");
              return true;
            }

            activeChar.sendMessage("There is problem with stoping " + event_name + " event.");
            return true;
          }
        }
      } catch (Exception e) {
        activeChar.sendMessage("Usage: //event_start <eventname>");
        e.printStackTrace();
        return false;
      }
    } else if (command.startsWith("admin_event_bypass")) {
      try {
        if (event_name != null) {
          Event event = (Event) QuestManager.getInstance().getQuest(event_name);
          if (event != null) {
            event.eventBypass(activeChar, _event_bypass);
          }
        }
      } catch (Exception e) {
        activeChar.sendMessage("Usage: //event_bypass <eventname> <bypass>");
        e.printStackTrace();
        return false;
      }
    }
    return false;
  }

  private void showMenu(L2PcInstance activeChar) {
    final NpcHtmlMessage html = new NpcHtmlMessage();
    html.setFile(activeChar.getHtmlPrefix(), "data/html/admin/gm_events.htm");
    final StringBuilder cList = new StringBuilder(500);
    for (Quest event : QuestManager.getInstance().getScripts().values()) {
      if (event instanceof Event) {
        StringUtil.append(
            cList,
            "<font color=\"LEVEL\">" + event.getName() + ":</font><br1>",
            "<table width=270><tr>",
            "<td><button value=\"Start\" action=\"bypass -h admin_event_start_menu "
                + event.getName()
                + "\" width=80 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>",
            "<td><button value=\"Stop\" action=\"bypass -h admin_event_stop_menu "
                + event.getName()
                + "\" width=80 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>",
            "<td><button value=\"Menu\" action=\"bypass -h admin_event_bypass "
                + event.getName()
                + "\" width=80 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>",
            "</tr></table><br>");
      }
    }
    html.replace("%LIST%", cList.toString());
    activeChar.sendPacket(html);
  }
}
