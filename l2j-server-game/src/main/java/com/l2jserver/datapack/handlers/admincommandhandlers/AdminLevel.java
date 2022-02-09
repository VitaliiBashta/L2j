package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.data.json.ExperienceData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import org.springframework.stereotype.Service;

import java.util.StringTokenizer;

@Service
public class AdminLevel implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {"admin_add_level", "admin_set_level"};

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    L2Object targetChar = activeChar.getTarget();
    StringTokenizer st = new StringTokenizer(command, " ");
    String actualCommand = st.nextToken(); // Get actual command

    String val = "";
    if (st.countTokens() >= 1) {
      val = st.nextToken();
    }

    if (actualCommand.equalsIgnoreCase("admin_add_level")) {
      try {
        if (targetChar.isPlayer()) {
          L2PcInstance targetPlayer = (L2PcInstance) targetChar;
          targetPlayer.addLevel(Integer.parseInt(val));
        }
      } catch (NumberFormatException e) {
        activeChar.sendMessage("Wrong Number Format");
      }
    } else if (actualCommand.equalsIgnoreCase("admin_set_level")) {
      try {
        if (!(targetChar instanceof L2PcInstance)) {
          activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
          return false;
        }
        L2PcInstance targetPlayer = (L2PcInstance) targetChar;
        int oldLevel = targetPlayer.getLevel();
        int newLevel = Integer.parseInt(val);

        if (newLevel < 1) {
          newLevel = 1;
        }
        targetPlayer.setLevel(newLevel);
        targetPlayer.setExp(
            ExperienceData.getInstance()
                .getExpForLevel(Math.min(newLevel, targetPlayer.getMaxExpLevel())));
        targetPlayer.onLevelChange(newLevel > oldLevel);
        targetPlayer.broadcastInfo();
      } catch (NumberFormatException e) {
        activeChar.sendMessage("Level require number as value!");
        return false;
      }
    }
    return true;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }
}
