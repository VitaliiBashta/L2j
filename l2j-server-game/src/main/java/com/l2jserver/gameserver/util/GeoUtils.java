package com.l2jserver.gameserver.util;

import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExServerPrimitive;
import com.l2jserver.geodriver.Cell;

import java.awt.*;

public final class GeoUtils {

  private GeoUtils() {}

  private static Color getDirectionColor(int x, int y, int z, int nswe) {
    if (GeoData.getInstance().checkNearestNswe(x, y, z, nswe)) {
      return Color.GREEN;
    }
    return Color.RED;
  }

  public static void debugGrid(L2PcInstance player) {
    int geoRadius = 10;
    int blocksPerPacket = 49;

    int iBlock = blocksPerPacket;
    int iPacket = 0;

    ExServerPrimitive exsp = null;
    GeoData gd = GeoData.getInstance();
    int playerGx = gd.getGeoX(player.getX());
    int playerGy = gd.getGeoY(player.getY());
    for (int dx = -geoRadius; dx <= geoRadius; ++dx) {
      for (int dy = -geoRadius; dy <= geoRadius; ++dy) {
        if (iBlock >= blocksPerPacket) {
          iBlock = 0;
          if (exsp != null) {
            ++iPacket;
            player.sendPacket(exsp);
          }
          exsp =
              new ExServerPrimitive("DebugGrid_" + iPacket, player.getX(), player.getY(), -16000);
        }

        int gx = playerGx + dx;
        int gy = playerGy + dy;

        int x = gd.getWorldX(gx);
        int y = gd.getWorldY(gy);
        int z = gd.getNearestZ(gx, gy, player.getZ());

        // north arrow
        Color col = getDirectionColor(gx, gy, z, Cell.NSWE_NORTH);
        exsp.addLine(col, x - 1, y - 7, z, x + 1, y - 7, z);
        exsp.addLine(col, x - 2, y - 6, z, x + 2, y - 6, z);
        exsp.addLine(col, x - 3, y - 5, z, x + 3, y - 5, z);
        exsp.addLine(col, x - 4, y - 4, z, x + 4, y - 4, z);

        // east arrow
        col = getDirectionColor(gx, gy, z, Cell.NSWE_EAST);
        exsp.addLine(col, x + 7, y - 1, z, x + 7, y + 1, z);
        exsp.addLine(col, x + 6, y - 2, z, x + 6, y + 2, z);
        exsp.addLine(col, x + 5, y - 3, z, x + 5, y + 3, z);
        exsp.addLine(col, x + 4, y - 4, z, x + 4, y + 4, z);

        // south arrow
        col = getDirectionColor(gx, gy, z, Cell.NSWE_SOUTH);
        exsp.addLine(col, x - 1, y + 7, z, x + 1, y + 7, z);
        exsp.addLine(col, x - 2, y + 6, z, x + 2, y + 6, z);
        exsp.addLine(col, x - 3, y + 5, z, x + 3, y + 5, z);
        exsp.addLine(col, x - 4, y + 4, z, x + 4, y + 4, z);

        col = getDirectionColor(gx, gy, z, Cell.NSWE_WEST);
        exsp.addLine(col, x - 7, y - 1, z, x - 7, y + 1, z);
        exsp.addLine(col, x - 6, y - 2, z, x - 6, y + 2, z);
        exsp.addLine(col, x - 5, y - 3, z, x - 5, y + 3, z);
        exsp.addLine(col, x - 4, y - 4, z, x - 4, y + 4, z);

        ++iBlock;
      }
    }

    player.sendPacket(exsp);
  }

  /** difference between x values: never above 1, difference between y values: never above 1 */
  public static int computeNswe(int lastX, int lastY, int x, int y) {
    if (x > lastX) { // east
      if (y > lastY) {
        return Cell.NSWE_SOUTH_EAST;
      } else if (y < lastY) {
        return Cell.NSWE_NORTH_EAST;
      } else {
        return Cell.NSWE_EAST;
      }
    } else if (x < lastX) { // west
      if (y > lastY) {
        return Cell.NSWE_SOUTH_WEST;
      } else if (y < lastY) {
        return Cell.NSWE_NORTH_WEST;
      } else {
        return Cell.NSWE_WEST;
      }
    } else { // unchanged x
      if (y > lastY) {
        return Cell.NSWE_SOUTH;
      } else if (y < lastY) {
        return Cell.NSWE_NORTH;
      }
      throw new RuntimeException();
    }
  }
}
