package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.stat.PcStat;
import org.springframework.stereotype.Service;

import java.util.StringTokenizer;

import static com.l2jserver.gameserver.config.Configuration.vitality;

@Service
public class AdminVitality implements IAdminCommandHandler {

  private static final String[] ADMIN_COMMANDS = {
    "admin_set_vitality",
    "admin_set_vitality_level",
    "admin_full_vitality",
    "admin_empty_vitality",
    "admin_get_vitality"
  };

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    if (activeChar == null) {
      return false;
    }

    if (!vitality().enabled()) {
      activeChar.sendMessage("Vitality is not enabled on the server!");
      return false;
    }

    int level = 0;
    int vitality = 0;

    StringTokenizer st = new StringTokenizer(command, " ");
    String cmd = st.nextToken();

    if (activeChar.getTarget() instanceof L2PcInstance) {
      L2PcInstance target;
      target = (L2PcInstance) activeChar.getTarget();

      if (cmd.equals("admin_set_vitality")) {
        try {
          vitality = Integer.parseInt(st.nextToken());
        } catch (Exception e) {
          activeChar.sendMessage("Incorrect vitality");
        }

        target.setVitalityPoints(vitality, true);
        target.sendMessage("Admin set your Vitality points to " + vitality);
      } else if (cmd.equals("admin_set_vitality_level")) {
        try {
          level = Integer.parseInt(st.nextToken());
        } catch (Exception e) {
          activeChar.sendMessage("Incorrect vitality level (0-4)");
        }

        if ((level >= 0) && (level <= 4)) {
          if (level == 0) {
            vitality = PcStat.MIN_VITALITY_POINTS;
          } else {
            vitality = PcStat.VITALITY_LEVELS[level - 1];
          }
          target.setVitalityPoints(vitality, true);
          target.sendMessage("Admin set your Vitality level to " + level);
        } else {
          activeChar.sendMessage("Incorrect vitality level (0-4)");
        }
      } else if (cmd.equals("admin_full_vitality")) {
        target.setVitalityPoints(PcStat.MAX_VITALITY_POINTS, true);
        target.sendMessage("Admin completly recharged your Vitality");
      } else if (cmd.equals("admin_empty_vitality")) {
        target.setVitalityPoints(PcStat.MIN_VITALITY_POINTS, true);
        target.sendMessage("Admin completly emptied your Vitality");
      } else if (cmd.equals("admin_get_vitality")) {
        level = target.getVitalityLevel();
        vitality = target.getVitalityPoints();

        activeChar.sendMessage("Player vitality level: " + level);
        activeChar.sendMessage("Player vitality points: " + vitality);
      }
      return true;
    }
    activeChar.sendMessage("Target not found or not a player");
    return false;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }
}
