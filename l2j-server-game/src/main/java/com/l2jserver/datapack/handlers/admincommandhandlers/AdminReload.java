package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.data.sql.impl.CrestTable;
import com.l2jserver.gameserver.data.sql.impl.TeleportLocationTable;
import com.l2jserver.gameserver.data.xml.impl.*;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.instancemanager.WalkingManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.util.Util;
import org.aeonbits.owner.Reloadable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.StringTokenizer;

import static com.l2jserver.gameserver.config.Configuration.server;

@Service
public class AdminReload implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {"admin_reload"};

  private static final String RELOAD_USAGE =
      "Usage: //reload <config|access|npc|quest [quest_id|quest_name]|walker|htm[l] [file|directory]|multisell|buylist|teleport|skill|item|door|effect|handler|enchant|creationpoint>";

  private final BuyListData buyListData;
  private final AdminData adminData;
  private final AdminHtml adminHtml;

  public AdminReload(BuyListData buyListData, AdminData adminData, AdminHtml adminHtml) {
    this.buyListData = buyListData;
    this.adminData = adminData;
    this.adminHtml = adminHtml;
  }

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    final StringTokenizer st = new StringTokenizer(command, " ");
    final String actualCommand = st.nextToken();
    if (actualCommand.equalsIgnoreCase("admin_reload")) {
      if (!st.hasMoreTokens()) {
        AdminHtml.showAdminHtml(activeChar, "reload.htm");
        activeChar.sendMessage(RELOAD_USAGE);
        return true;
      }

      final String type = st.nextToken();
      switch (type.toLowerCase()) {
        case "config":
          {
            if (st.hasMoreElements()) {
              final var configName = st.nextToken();
              try {
                final var field = Configuration.class.getDeclaredField(configName);
                if (Reloadable.class.isAssignableFrom(field.getType())) {
                  field.setAccessible(true);
                  ((Reloadable) field.get(null)).reload();
                  adminData.broadcastMessageToGMs(
                      activeChar.getName() + ": Reloaded " + configName + " configuration.");
                } else {
                  activeChar.sendMessage(configName + " configuration cannot be reloaded.");
                }
              } catch (Exception ex) {
                activeChar.sendMessage("Failed to reload configuration " + configName + ".");
              }
            } else {
              for (var field : Configuration.class.getDeclaredFields()) {
                if (Reloadable.class.isAssignableFrom(field.getType())) {
                  try {
                    field.setAccessible(true);
                    ((Reloadable) field.get(null)).reload();
                  } catch (Exception ex) {
                    activeChar.sendMessage(
                        "Failed to reload configuration " + field.getName() + ".");
                  }
                }
              }
              adminData.broadcastMessageToGMs(
                  activeChar.getName() + ": Reloaded all configurations.");
            }
            break;
          }
        case "access":
          {
            adminData.load();
            adminData.broadcastMessageToGMs(activeChar.getName() + ": Reloaded Access.");
            break;
          }
        case "npc":
          {
            NpcData.getInstance().load();
            adminData.broadcastMessageToGMs(activeChar.getName() + ": Reloaded Npcs.");
            break;
          }
        case "quest":
          {
            if (st.hasMoreElements()) {
              String value = st.nextToken();
              if (!Util.isDigit(value)) {
                QuestManager.getInstance().reload(value);
                adminData.broadcastMessageToGMs(
                    activeChar.getName() + ": Reloaded Quest Name:" + value + ".");
              } else {
                final int questId = Integer.parseInt(value);
                QuestManager.getInstance().reload(questId);
                adminData.broadcastMessageToGMs(
                    activeChar.getName() + ": Reloaded Quest ID:" + questId + ".");
              }
            } else {
              QuestManager.getInstance().reloadAllScripts();
              activeChar.sendMessage("All scripts have been reloaded.");
              adminData.broadcastMessageToGMs(activeChar.getName() + ": Reloaded Quests.");
            }
            break;
          }
        case "walker":
          {
            WalkingManager.getInstance().load();
            activeChar.sendMessage("All walkers have been reloaded");
            adminData.broadcastMessageToGMs(activeChar.getName() + ": Reloaded Walkers.");
            break;
          }
        case "htm":
        case "html":
          {
            if (st.hasMoreElements()) {
              final String path = st.nextToken();
              final File file = new File(server().getDatapackRoot(), "data/html/" + path);
              if (file.exists()) {
                HtmCache.getInstance().reload(file);
                adminData.broadcastMessageToGMs(
                    activeChar.getName() + ": Reloaded Htm File:" + file.getName() + ".");
              } else {
                activeChar.sendMessage("File or Directory does not exist.");
              }
            } else {
              HtmCache.getInstance().reload();
              activeChar.sendMessage(
                  "Cache[HTML]: "
                      + HtmCache.getInstance().getMemoryUsage()
                      + " megabytes on "
                      + HtmCache.getInstance().getLoadedFiles()
                      + " files loaded");
              adminData.broadcastMessageToGMs(activeChar.getName() + ": Reloaded Htms.");
            }
            break;
          }
        case "multisell":
          {
            MultisellData.getInstance().load();
            adminData.broadcastMessageToGMs(activeChar.getName() + ": Reloaded Multisells.");
            break;
          }
        case "buylist":
          {
            buyListData.load();
            adminData.broadcastMessageToGMs(activeChar.getName() + ": Reloaded Buylists.");
            break;
          }
        case "teleport":
          {
            TeleportLocationTable.getInstance().reloadAll();
            adminData.broadcastMessageToGMs(activeChar.getName() + ": Reloaded Teleports.");
            break;
          }
        case "skill":
          {
            SkillData.getInstance().reload();
            adminData.broadcastMessageToGMs(activeChar.getName() + ": Reloaded Skills.");
            break;
          }
        case "item":
          {
            ItemTable.getInstance().reload();
            adminData.broadcastMessageToGMs(activeChar.getName() + ": Reloaded Items.");
            break;
          }
        case "door":
          {
            DoorData.getInstance().load();
            adminData.broadcastMessageToGMs(activeChar.getName() + ": Reloaded Doors.");
            break;
          }
        case "zone":
          {
            ZoneManager.getInstance().reload();
            adminData.broadcastMessageToGMs(activeChar.getName() + ": Reloaded Zones.");
            break;
          }
        case "cw":
          {
            CursedWeaponsManager.getInstance().reload();
            adminData.broadcastMessageToGMs(activeChar.getName() + ": Reloaded Cursed Weapons.");
            break;
          }
        case "crest":
          {
            CrestTable.getInstance().load();
            adminData.broadcastMessageToGMs(activeChar.getName() + ": Reloaded Crests.");
            break;
          }
        case "enchant":
          {
            EnchantItemGroupsData.getInstance().load();
            EnchantItemData.getInstance().load();
            adminData.broadcastMessageToGMs(
                activeChar.getName() + ": Reloaded item enchanting data.");
            break;
          }
        case "transform":
          {
            TransformData.getInstance().load();
            adminData.broadcastMessageToGMs(activeChar.getName() + ": Reloaded transform data.");
            break;
          }
        case "creationpoint":
          {
            PlayerCreationPointData.getInstance().load();
            adminData.broadcastMessageToGMs(
                activeChar.getName() + ": Reloaded creation points data.");
            break;
          }
        default:
          {
            activeChar.sendMessage(RELOAD_USAGE);
            return true;
          }
      }
      activeChar.sendMessage(
          "WARNING: There are several known issues regarding this feature. Reloading server data during runtime is STRONGLY NOT RECOMMENDED for live servers, just for developing environments.");
    }
    return true;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }
}
