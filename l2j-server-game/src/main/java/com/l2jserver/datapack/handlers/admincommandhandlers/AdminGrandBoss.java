package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.datapack.ai.individual.Antharas.Antharas;
import com.l2jserver.datapack.ai.individual.Baium.Baium;
import com.l2jserver.datapack.ai.individual.QueenAnt;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.GrandBossManager;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.zone.type.L2NoRestartZone;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.StringTokenizer;

@Service
public class AdminGrandBoss implements IAdminCommandHandler {
  private static final int ANTHARAS = 29068; // Antharas
  private static final int ANTHARAS_ZONE = 70050; // Antharas Nest
  private static final int VALAKAS = 29028; // Valakas
  private static final int BAIUM = 29020; // Baium
  private static final int BAIUM_ZONE = 70051; // Baium Nest
  private static final int QUEENANT = 29001; // Queen Ant
  private static final int ORFEN = 29014; // Orfen
  private static final int CORE = 29006; // Core

  private static final String[] ADMIN_COMMANDS = {
    "admin_grandboss",
    "admin_grandboss_skip",
    "admin_grandboss_respawn",
    "admin_grandboss_minions",
    "admin_grandboss_abort",
  };
  private final QuestManager questManager;
  private final GrandBossManager grandBossManager;

  public AdminGrandBoss(QuestManager questManager, GrandBossManager grandBossManager) {
    this.questManager = questManager;
    this.grandBossManager = grandBossManager;
  }

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    final StringTokenizer st = new StringTokenizer(command, " ");
    final String actualCommand = st.nextToken();
    switch (actualCommand.toLowerCase()) {
      case "admin_grandboss":
        {
          if (st.hasMoreTokens()) {
            final int grandBossId = Integer.parseInt(st.nextToken());
            manageHtml(activeChar, grandBossId);
          } else {
            NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
            html.setHtml(
                HtmCache.getInstance()
                    .getHtm(activeChar.getHtmlPrefix(), "data/html/admin/grandboss.htm"));
            activeChar.sendPacket(html);
          }
          break;
        }

      case "admin_grandboss_skip":
        {
          if (st.hasMoreTokens()) {
            final int grandBossId = Integer.parseInt(st.nextToken());

            if (grandBossId == ANTHARAS) {
              getAntharasAI().notifyEvent("SKIP_WAITING", null, activeChar);
              manageHtml(activeChar, grandBossId);
            } else {
              activeChar.sendMessage("Wrong ID!");
            }
          } else {
            activeChar.sendMessage("Usage: //grandboss_skip Id");
          }
          break;
        }
      case "admin_grandboss_respawn":
        {
          if (st.hasMoreTokens()) {
            final int grandBossId = Integer.parseInt(st.nextToken());

            switch (grandBossId) {
              case ANTHARAS:
                {
                  getAntharasAI().notifyEvent("RESPAWN_ANTHARAS", null, activeChar);
                  manageHtml(activeChar, grandBossId);
                  break;
                }
              case BAIUM:
                {
                  getBaiumAI().notifyEvent("RESPAWN_BAIUM", null, activeChar);
                  manageHtml(activeChar, grandBossId);
                  break;
                }
              case QUEENANT:
                {
                  getQueenAntAI().notifyEvent("RESPAWN_QUEEN", null, activeChar);
                  manageHtml(activeChar, grandBossId);
                  break;
                }
              default:
                {
                  activeChar.sendMessage("Wrong ID!");
                }
            }
          } else {
            activeChar.sendMessage("Usage: //grandboss_respawn Id");
          }
          break;
        }
      case "admin_grandboss_minions":
        {
          if (st.hasMoreTokens()) {
            final int grandBossId = Integer.parseInt(st.nextToken());

            switch (grandBossId) {
              case ANTHARAS:
                {
                  getAntharasAI().notifyEvent("DESPAWN_MINIONS", null, activeChar);
                  break;
                }
              case BAIUM:
                {
                  getBaiumAI().notifyEvent("DESPAWN_MINIONS", null, activeChar);
                  break;
                }
              default:
                {
                  activeChar.sendMessage("Wrong ID!");
                }
            }
          } else {
            activeChar.sendMessage("Usage: //grandboss_minions Id");
          }
          break;
        }
      case "admin_grandboss_abort":
        {
          if (st.hasMoreTokens()) {
            final int grandBossId = Integer.parseInt(st.nextToken());

            switch (grandBossId) {
              case ANTHARAS:
                {
                  getAntharasAI().notifyEvent("ABORT_FIGHT", null, activeChar);
                  manageHtml(activeChar, grandBossId);
                  break;
                }
              case BAIUM:
                {
                  getBaiumAI().notifyEvent("ABORT_FIGHT", null, activeChar);
                  manageHtml(activeChar, grandBossId);
                  break;
                }
              default:
                {
                  activeChar.sendMessage("Wrong ID!");
                }
            }
          } else {
            activeChar.sendMessage("Usage: //grandboss_abort Id");
          }
        }
        break;
    }
    return true;
  }

  private void manageHtml(L2PcInstance activeChar, int grandBossId) {
    if (Arrays.asList(ANTHARAS, VALAKAS, BAIUM, QUEENANT, ORFEN, CORE).contains(grandBossId)) {
      final int bossStatus = grandBossManager.getBossStatus(grandBossId);
      L2NoRestartZone bossZone = null;
      String textColor = null;
      String text = null;
      String htmlPatch = null;
      int deadStatus = 0;

      switch (grandBossId) {
        case ANTHARAS:
          {
            bossZone = ZoneManager.getInstance().getZoneById(ANTHARAS_ZONE, L2NoRestartZone.class);
            htmlPatch = "data/html/admin/grandboss_antharas.htm";
            break;
          }
        case VALAKAS:
          {
            htmlPatch = "data/html/admin/grandboss_valakas.htm";
            break;
          }
        case BAIUM:
          {
            bossZone = ZoneManager.getInstance().getZoneById(BAIUM_ZONE, L2NoRestartZone.class);
            htmlPatch = "data/html/admin/grandboss_baium.htm";
            break;
          }
        case QUEENANT:
          {
            htmlPatch = "data/html/admin/grandboss_queenant.htm";
            break;
          }
        case ORFEN:
          {
            htmlPatch = "data/html/admin/grandboss_orfen.htm";
            break;
          }
        case CORE:
          {
            htmlPatch = "data/html/admin/grandboss_core.htm";
            break;
          }
      }

      if (Arrays.asList(ANTHARAS, VALAKAS, BAIUM).contains(grandBossId)) {
        deadStatus = 3;
        switch (bossStatus) {
          case 0:
            {
              textColor = "00FF00"; // Green
              text = "Alive";
              break;
            }
          case 1:
            {
              textColor = "FFFF00"; // Yellow
              text = "Waiting";
              break;
            }
          case 2:
            {
              textColor = "FF9900"; // Orange
              text = "In Fight";
              break;
            }
          case 3:
            {
              textColor = "FF0000"; // Red
              text = "Dead";
              break;
            }
        }
      } else {
        deadStatus = 1;
        switch (bossStatus) {
          case 0:
            {
              textColor = "00FF00"; // Green
              text = "Alive";
              break;
            }
          case 1:
            {
              textColor = "FF0000"; // Red
              text = "Dead";
              break;
            }
        }
      }

      final StatsSet info = grandBossManager.getStatsSet(grandBossId);
      final String bossRespawn =
          new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(info.getLong("respawn_time"));

      NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
      html.setHtml(HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), htmlPatch));
      html.replace("%bossStatus%", text);
      html.replace("%bossColor%", textColor);
      html.replace("%respawnTime%", bossStatus == deadStatus ? bossRespawn : "Already respawned!");
      html.replace(
          "%playersInside%",
          bossZone != null
              ? String.valueOf(bossZone.getPlayersInside().size())
              : "Zone not found!");
      activeChar.sendPacket(html);
    } else {
      activeChar.sendMessage("Wrong ID!");
    }
  }

  private Quest getAntharasAI() {
    return questManager.getQuest(Antharas.class.getSimpleName());
  }

  private Quest getBaiumAI() {
    return questManager.getQuest(Baium.class.getSimpleName());
  }

  private Quest getQueenAntAI() {
    return questManager.getQuest(QueenAnt.class.getSimpleName());
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }
}
