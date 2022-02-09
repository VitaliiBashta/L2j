package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.SevenSigns;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.AutoSpawnHandler;
import com.l2jserver.gameserver.model.AutoSpawnHandler.AutoSpawnInstance;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import org.springframework.stereotype.Service;

/** Admin Command Handler for Mammon NPCs */
@Service
public class AdminMammon implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {
    "admin_mammon_find", "admin_mammon_respawn",
  };

  private final boolean _isSealValidation = SevenSigns.getInstance().isSealValidationPeriod();

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    int teleportIndex = -1;
    final AutoSpawnInstance blackSpawnInst =
        AutoSpawnHandler.getInstance().getAutoSpawnInstance(SevenSigns.MAMMON_BLACKSMITH_ID, false);
    final AutoSpawnInstance merchSpawnInst =
        AutoSpawnHandler.getInstance().getAutoSpawnInstance(SevenSigns.MAMMON_MERCHANT_ID, false);

    if (command.startsWith("admin_mammon_find")) {
      try {
        if (command.length() > 17) {
          teleportIndex = Integer.parseInt(command.substring(18));
        }
      } catch (Exception NumberFormatException) {
        activeChar.sendMessage(
            "Usage: //mammon_find [teleportIndex] (where 1 = Blacksmith, 2 = Merchant)");
        return false;
      }

      if (!_isSealValidation) {
        activeChar.sendPacket(SystemMessageId.SSQ_COMPETITION_UNDERWAY);
        return false;
      }

      if (blackSpawnInst != null) {
        final L2Npc blackInst = blackSpawnInst.getNPCInstanceList().peek();
        if (blackInst != null) {
          activeChar.sendMessage(
              "Blacksmith of Mammon: "
                  + blackInst.getX()
                  + " "
                  + blackInst.getY()
                  + " "
                  + blackInst.getZ());
          if (teleportIndex == 1) {
            activeChar.teleToLocation(blackInst.getLocation(), true);
          }
        }
      } else {
        activeChar.sendMessage("Blacksmith of Mammon isn't registered for spawn.");
      }

      if (merchSpawnInst != null) {
        final L2Npc merchInst = merchSpawnInst.getNPCInstanceList().peek();
        if (merchInst != null) {
          activeChar.sendMessage(
              "Merchant of Mammon: "
                  + merchInst.getX()
                  + " "
                  + merchInst.getY()
                  + " "
                  + merchInst.getZ());
          if (teleportIndex == 2) {
            activeChar.teleToLocation(merchInst.getLocation(), true);
          }
        }
      } else {
        activeChar.sendMessage("Merchant of Mammon isn't registered for spawn.");
      }
    } else if (command.startsWith("admin_mammon_respawn")) {
      if (!_isSealValidation) {
        activeChar.sendPacket(SystemMessageId.SSQ_COMPETITION_UNDERWAY);
        return true;
      }

      if (merchSpawnInst != null) {
        long merchRespawn = AutoSpawnHandler.getInstance().getTimeToNextSpawn(merchSpawnInst);
        activeChar.sendMessage(
            "The Merchant of Mammon will respawn in " + (merchRespawn / 60000) + " minute(s).");
      } else {
        activeChar.sendMessage("Merchant of Mammon isn't registered for spawn.");
      }

      if (blackSpawnInst != null) {
        long blackRespawn = AutoSpawnHandler.getInstance().getTimeToNextSpawn(blackSpawnInst);
        activeChar.sendMessage(
            "The Blacksmith of Mammon will respawn in " + (blackRespawn / 60000) + " minute(s).");
      } else {
        activeChar.sendMessage("Blacksmith of Mammon isn't registered for spawn.");
      }
    }
    return true;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }
}
