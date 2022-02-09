package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.data.xml.impl.TransformData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import org.springframework.stereotype.Service;

@Service
public class AdminRide implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {
    "admin_ride_horse",
    "admin_ride_bike",
    "admin_ride_wyvern",
    "admin_ride_strider",
    "admin_unride_wyvern",
    "admin_unride_strider",
    "admin_unride",
    "admin_ride_wolf",
    "admin_unride_wolf",
  };
  private static final int PURPLE_MANED_HORSE_TRANSFORMATION_ID = 106;
  private static final int JET_BIKE_TRANSFORMATION_ID = 20001;
  private int _petRideId;

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    L2PcInstance player = getRideTarget(activeChar);
    if (player == null) {
      return false;
    }

    if (command.startsWith("admin_ride")) {
      if (player.isMounted() || player.hasSummon()) {
        activeChar.sendMessage("Target already have a summon.");
        return false;
      }
      if (command.startsWith("admin_ride_wyvern")) {
        _petRideId = 12621;
      } else if (command.startsWith("admin_ride_strider")) {
        _petRideId = 12526;
      } else if (command.startsWith("admin_ride_wolf")) {
        _petRideId = 16041;
      } else if (command.startsWith("admin_ride_horse")) // handled using transformation
      {
        if (player.isTransformed() || player.isInStance()) {
          activeChar.sendPacket(SystemMessageId.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
        } else {
          TransformData.getInstance().transformPlayer(PURPLE_MANED_HORSE_TRANSFORMATION_ID, player);
        }

        return true;
      } else if (command.startsWith("admin_ride_bike")) // handled using transformation
      {
        if (player.isTransformed() || player.isInStance()) {
          activeChar.sendPacket(SystemMessageId.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
        } else {
          TransformData.getInstance().transformPlayer(JET_BIKE_TRANSFORMATION_ID, player);
        }

        return true;
      } else {
        activeChar.sendMessage("Command '" + command + "' not recognized");
        return false;
      }

      player.mount(_petRideId, 0, false);

      return false;
    } else if (command.startsWith("admin_unride")) {
      if (player.getTransformationId() == PURPLE_MANED_HORSE_TRANSFORMATION_ID) {
        player.untransform();
      }

      if (player.getTransformationId() == JET_BIKE_TRANSFORMATION_ID) {
        player.untransform();
      } else {
        player.dismount();
      }
    }
    return true;
  }

  private L2PcInstance getRideTarget(L2PcInstance activeChar) {
    L2PcInstance player = null;

    if ((activeChar.getTarget() == null)
        || (activeChar.getTarget().getObjectId() == activeChar.getObjectId())
        || !(activeChar.getTarget() instanceof L2PcInstance)) {
      player = activeChar;
    } else {
      player = (L2PcInstance) activeChar.getTarget();
    }

    return player;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }
}
