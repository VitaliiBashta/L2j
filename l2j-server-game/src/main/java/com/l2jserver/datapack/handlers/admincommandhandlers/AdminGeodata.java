package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.GeoUtils;
import org.springframework.stereotype.Service;

import java.util.StringTokenizer;

@Service
public class AdminGeodata implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {
    "admin_geo_pos",
    "admin_geo_spawn_pos",
    "admin_geo_can_move",
    "admin_geo_can_see",
    "admin_geogrid",
  };

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    final StringTokenizer st = new StringTokenizer(command, " ");
    final String actualCommand = st.nextToken();
    switch (actualCommand.toLowerCase()) {
      case "admin_geo_pos":
        {
          final int worldX = activeChar.getX();
          final int worldY = activeChar.getY();
          final int worldZ = activeChar.getZ();
          final int geoX = GeoData.getInstance().getGeoX(worldX);
          final int geoY = GeoData.getInstance().getGeoY(worldY);

          if (GeoData.getInstance().hasGeoPos(geoX, geoY)) {
            activeChar.sendMessage(
                "WorldX: "
                    + worldX
                    + ", WorldY: "
                    + worldY
                    + ", WorldZ: "
                    + worldZ
                    + ", GeoX: "
                    + geoX
                    + ", GeoY: "
                    + geoY
                    + ", GeoZ: "
                    + GeoData.getInstance().getNearestZ(geoX, geoY, worldZ));
          } else {
            activeChar.sendMessage("There is no geodata at this position.");
          }
          break;
        }
      case "admin_geo_spawn_pos":
        {
          final int worldX = activeChar.getX();
          final int worldY = activeChar.getY();
          final int worldZ = activeChar.getZ();
          final int geoX = GeoData.getInstance().getGeoX(worldX);
          final int geoY = GeoData.getInstance().getGeoY(worldY);

          if (GeoData.getInstance().hasGeoPos(geoX, geoY)) {
            activeChar.sendMessage(
                "WorldX: "
                    + worldX
                    + ", WorldY: "
                    + worldY
                    + ", WorldZ: "
                    + worldZ
                    + ", GeoX: "
                    + geoX
                    + ", GeoY: "
                    + geoY
                    + ", GeoZ: "
                    + GeoData.getInstance().getSpawnHeight(worldX, worldY, worldZ));
          } else {
            activeChar.sendMessage("There is no geodata at this position.");
          }
          break;
        }
      case "admin_geo_can_move":
        {
          final L2Object target = activeChar.getTarget();
          if (target != null) {
            if (GeoData.getInstance().canSeeTarget(activeChar, target)) {
              activeChar.sendMessage("Can move beeline.");
            } else {
              activeChar.sendMessage("Can not move beeline!");
            }
          } else {
            activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
          }
          break;
        }
      case "admin_geo_can_see":
        {
          final L2Object target = activeChar.getTarget();
          if (target != null) {
            if (GeoData.getInstance().canSeeTarget(activeChar, target)) {
              activeChar.sendMessage("Can see target.");
            } else {
              activeChar.sendPacket(
                  SystemMessage.getSystemMessage(SystemMessageId.CANT_SEE_TARGET));
            }
          } else {
            activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
          }
          break;
        }
      case "admin_geogrid":
        {
          GeoUtils.debugGrid(activeChar);
          break;
        }
    }
    return true;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }
}
