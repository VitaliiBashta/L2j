package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.data.xml.impl.DoorData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import org.springframework.stereotype.Service;

/**
 * This class handles following admin commands: - open1 = open coloseum door 24190001 - open2 = open
 * coloseum door 24190002 - open3 = open coloseum door 24190003 - open4 = open coloseum door
 * 24190004 - openall = open all coloseum door - close1 = close coloseum door 24190001 - close2 =
 * close coloseum door 24190002 - close3 = close coloseum door 24190003 - close4 = close coloseum
 * door 24190004 - closeall = close all coloseum door - open = open selected door - close = close
 * selected door
 *
 * @version $Revision: 1.2.4.5 $ $Date: 2005/04/11 10:06:06 $
 */
@Service
public class AdminDoorControl implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {
    "admin_open", "admin_close", "admin_openall", "admin_closeall"
  };
  private final DoorData doorData;

  public AdminDoorControl(DoorData doorData) {
    this.doorData = doorData;
  }

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    try {
      if (command.startsWith("admin_open ")) {
        int doorId = Integer.parseInt(command.substring(11));
        if (doorData.getDoor(doorId) != null) {
          doorData.getDoor(doorId).openMe();
        } else {
          for (Castle castle : CastleManager.getInstance().getCastles()) {
            if (castle.getDoor(doorId) != null) {
              castle.getDoor(doorId).openMe();
            }
          }
        }
      } else if (command.startsWith("admin_close ")) {
        int doorId = Integer.parseInt(command.substring(12));
        if (doorData.getDoor(doorId) != null) {
          doorData.getDoor(doorId).closeMe();
        } else {
          for (Castle castle : CastleManager.getInstance().getCastles()) {
            if (castle.getDoor(doorId) != null) {
              castle.getDoor(doorId).closeMe();
            }
          }
        }
      }
      if (command.equals("admin_closeall")) {
        for (L2DoorInstance door : doorData.getDoors()) {
          door.closeMe();
        }
        for (Castle castle : CastleManager.getInstance().getCastles()) {
          for (L2DoorInstance door : castle.getDoors()) {
            door.closeMe();
          }
        }
      }
      if (command.equals("admin_openall")) {
        for (L2DoorInstance door : doorData.getDoors()) {
          door.openMe();
        }
        for (Castle castle : CastleManager.getInstance().getCastles()) {
          for (L2DoorInstance door : castle.getDoors()) {
            door.openMe();
          }
        }
      }
      if (command.equals("admin_open")) {
        L2Object target = activeChar.getTarget();
        if (target instanceof L2DoorInstance) {
          ((L2DoorInstance) target).openMe();
        } else {
          activeChar.sendMessage("Incorrect target.");
        }
      }

      if (command.equals("admin_close")) {
        L2Object target = activeChar.getTarget();
        if (target instanceof L2DoorInstance) {
          ((L2DoorInstance) target).closeMe();
        } else {
          activeChar.sendMessage("Incorrect target.");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }
}
