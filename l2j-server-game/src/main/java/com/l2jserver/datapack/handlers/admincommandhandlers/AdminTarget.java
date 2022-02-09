package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class AdminTarget implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {"admin_target"};

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    if (command.startsWith("admin_target")) {
      handleTarget(command, activeChar);
    }
    return true;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }

  private void handleTarget(String command, L2PcInstance activeChar) {
    try {
      String targetName = command.substring(13);
      L2PcInstance player = L2World.getInstance().getPlayer(targetName);
      if (player != null) {
        player.onAction(activeChar);
      } else {
        activeChar.sendMessage("Player " + targetName + " not found");
      }
    } catch (IndexOutOfBoundsException e) {
      activeChar.sendMessage("Please specify correct name.");
    }
  }
}
