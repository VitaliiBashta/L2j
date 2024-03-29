package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.ListenerRegisterType;
import com.l2jserver.gameserver.model.events.listeners.AbstractEventListener;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestTimer;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.Util;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class AdminQuest implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {
    "admin_quest_reload",
    "admin_script_load",
    "admin_script_unload",
    "admin_show_quests",
    "admin_quest_info"
  };

  private final QuestManager questManager;

  public AdminQuest(QuestManager questManager) {
    this.questManager = questManager;
  }

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    if (activeChar == null) {
      return false;
    }

    // syntax will either be:
    // //quest_reload <id>
    // //quest_reload <questName>
    // The questName MUST start with a non-numeric character for this to work,
    // regardless which of the two formats is used.
    // Example: //quest_reload orc_occupation_change_1
    // Example: //quest_reload chests
    // Example: //quest_reload SagasSuperclass
    // Example: //quest_reload 12
    if (command.startsWith("admin_quest_reload")) {
      String[] parts = command.split(" ");
      if (parts.length < 2) {
        activeChar.sendMessage(
            "Usage: //quest_reload <questFolder>.<questSubFolders...>.questName> or //quest_reload <id>");
      } else {
        // try the first param as id
        try {
          int questId = Integer.parseInt(parts[1]);
          if (questManager.reload(questId)) {
            activeChar.sendMessage("Quest Reloaded Successfully.");
          } else {
            activeChar.sendMessage("Quest Reloaded Failed");
          }
        } catch (NumberFormatException e) {
          if (questManager.reload(parts[1])) {
            activeChar.sendMessage("Quest Reloaded Successfully.");
          } else {
            activeChar.sendMessage("Quest Reloaded Failed");
          }
        }
      }

    } else if (command.startsWith("admin_script_unload")) {
      String[] parts = command.split(" ");
      if (parts.length < 2) {
        activeChar.sendMessage("Example: //script_unload questName/questId");
      } else {
        Quest q =
            Util.isDigit(parts[1])
                ? questManager.getQuest(Integer.parseInt(parts[1]))
                : questManager.getQuest(parts[1]);

        if (q != null) {
          if (q.unload()) {
            activeChar.sendMessage(
                "Script Successfully Unloaded [" + q.getName() + "/" + q.getId() + "]");
          } else {
            activeChar.sendMessage("Failed unloading [" + q.getName() + "/" + q.getId() + "].");
          }
        } else {
          activeChar.sendMessage("The quest [" + parts[1] + "] was not found!.");
        }
      }
    } else if (command.startsWith("admin_show_quests")) {
      if (activeChar.getTarget() == null) {
        activeChar.sendMessage("Get a target first.");
      } else if (!activeChar.getTarget().isCharacter()) {
        activeChar.sendMessage("Invalid Target.");
      } else {
        final L2Character character = (L2Character) activeChar.getTarget();
        final StringBuilder sb = new StringBuilder();
        final Set<String> questNames = new TreeSet<>();
        for (EventType type : EventType.values()) {
          for (AbstractEventListener listener : character.getListeners(type)) {
            if (listener.getOwner() instanceof Quest) {
              final Quest quest = (Quest) listener.getOwner();
              if (questNames.contains(quest.getName())) {
                continue;
              }
              sb.append(
                  "<tr><td colspan=\"4\"><font color=\"LEVEL\"><a action=\"bypass -h admin_quest_info "
                      + quest.getName()
                      + "\">"
                      + quest.getName()
                      + "</a></font></td></tr>");
              questNames.add(quest.getName());
            }
          }
        }

        final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1);
        msg.setFile(activeChar.getHtmlPrefix(), "data/html/admin/npc-quests.htm");
        msg.replace("%quests%", sb.toString());
        msg.replace("%objid%", character.getObjectId());
        msg.replace("%questName%", "");
        activeChar.sendPacket(msg);
      }
    } else if (command.startsWith("admin_quest_info ")) {
      final String questName = command.substring("admin_quest_info ".length());
      final Quest quest = questManager.getQuest(questName);
      String events = "", npcs = "", items = "", timers = "";
      int counter = 0;
      if (quest == null) {
        activeChar.sendMessage("Couldn't find quest or script with name " + questName + " !");
        return false;
      }

      final Set<EventType> listenerTypes = new TreeSet<>();
      for (AbstractEventListener listener : quest.getListeners()) {
        if (!listenerTypes.contains(listener.getType())) {
          events += ", " + listener.getType().name();
          listenerTypes.add(listener.getType());
          counter++;
        }
        if (counter > 10) {
          counter = 0;
          break;
        }
      }

      final Set<Integer> npcIds = new TreeSet<>(quest.getRegisteredIds(ListenerRegisterType.NPC));
      for (int npcId : npcIds) {
        npcs += ", " + npcId;
        counter++;
        if (counter > 50) {
          counter = 0;
          break;
        }
      }

      if (!events.isEmpty()) {
        events = listenerTypes.size() + ": " + events.substring(2);
      }

      if (!npcs.isEmpty()) {
        npcs = npcIds.size() + ": " + npcs.substring(2);
      }

      if (quest.getRegisteredItemIds() != null) {
        for (int itemId : quest.getRegisteredItemIds()) {
          items += ", " + itemId;
          counter++;
          if (counter > 20) {
            counter = 0;
            break;
          }
        }
        items = quest.getRegisteredItemIds().length + ":" + items.substring(2);
      }

      for (List<QuestTimer> list : quest.getQuestTimers().values()) {
        for (QuestTimer timer : list) {
          timers +=
              "<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">"
                  + timer.getName()
                  + ":</font> <font color=00FF00>Active: "
                  + timer.getIsActive()
                  + " Repeatable: "
                  + timer.getIsRepeating()
                  + " Player: "
                  + timer.getPlayer()
                  + " Npc: "
                  + timer.getNpc()
                  + "</font></td></tr></table></td></tr>";
          counter++;
          if (counter > 10) {
            break;
          }
        }
      }

      final StringBuilder sb = new StringBuilder();
      sb.append(
          "<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">ID:</font> <font color=00FF00>"
              + quest.getId()
              + "</font></td></tr></table></td></tr>");
      sb.append(
          "<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Name:</font> <font color=00FF00>"
              + quest.getName()
              + "</font></td></tr></table></td></tr>");
      sb.append(
          "<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Descr:</font> <font color=00FF00>"
              + quest.getDescr()
              + "</font></td></tr></table></td></tr>");
      sb.append(
          "<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Path:</font> <font color=00FF00>"
              + quest
                  .getClass()
                  .getName()
                  .substring(0, quest.getClass().getName().lastIndexOf('.'))
                  .replaceAll("\\.", "/")
              + "</font></td></tr></table></td></tr>");
      sb.append(
          "<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Events:</font> <font color=00FF00>"
              + events
              + "</font></td></tr></table></td></tr>");
      if (!npcs.isEmpty()) {
        sb.append(
            "<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">NPCs:</font> <font color=00FF00>"
                + npcs
                + "</font></td></tr></table></td></tr>");
      }
      if (!items.isEmpty()) {
        sb.append(
            "<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Items:</font> <font color=00FF00>"
                + items
                + "</font></td></tr></table></td></tr>");
      }
      if (!timers.isEmpty()) {
        sb.append(
            "<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Timers:</font> <font color=00FF00></font></td></tr></table></td></tr>");
        sb.append(timers);
      }

      final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1);
      msg.setFile(activeChar.getHtmlPrefix(), "data/html/admin/npc-quests.htm");
      msg.replace("%quests%", sb.toString());
      msg.replace(
          "%questName%",
          "<table><tr><td width=\"50\" align=\"left\"><a action=\"bypass -h admin_script_load "
              + quest.getName()
              + "\">Reload</a></td> <td width=\"150\"  align=\"center\"><a action=\"bypass -h admin_quest_info "
              + quest.getName()
              + "\">"
              + quest.getName()
              + "</a></td> <td width=\"50\" align=\"right\"><a action=\"bypass -h admin_script_unload "
              + quest.getName()
              + "\">Unload</a></tr></td></table>");
      activeChar.sendPacket(msg);
    }
    return true;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }
}
