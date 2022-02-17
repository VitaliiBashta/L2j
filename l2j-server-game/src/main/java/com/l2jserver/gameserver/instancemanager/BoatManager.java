package com.l2jserver.gameserver.instancemanager;

import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.VehiclePathPoint;
import com.l2jserver.gameserver.model.actor.instance.L2BoatInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.L2CharTemplate;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.l2jserver.gameserver.config.Configuration.general;

@Service
public class BoatManager {
  public static final int TALKING_ISLAND = 1;
  public static final int GLUDIN_HARBOR = 2;
  public static final int RUNE_HARBOR = 3;
  private final Map<Integer, L2BoatInstance> _boats = new ConcurrentHashMap<>();
  private final boolean[] _docksBusy = new boolean[3];

  protected BoatManager() {
    Arrays.fill(_docksBusy, false);
  }

  public static BoatManager getInstance() {
    return SingletonHolder._instance;
  }

  public L2BoatInstance getNewBoat(int boatId, int x, int y, int z, int heading) {
    if (!general().allowBoat()) {
      return null;
    }

    StatsSet npcDat = new StatsSet();
    npcDat.set("npcId", boatId);
    npcDat.set("level", 0);
    npcDat.set("jClass", "boat");

    npcDat.set("baseSTR", 0);
    npcDat.set("baseCON", 0);
    npcDat.set("baseDEX", 0);
    npcDat.set("baseINT", 0);
    npcDat.set("baseWIT", 0);
    npcDat.set("baseMEN", 0);

    npcDat.set("baseShldDef", 0);
    npcDat.set("baseShldRate", 0);
    npcDat.set("baseAccCombat", 38);
    npcDat.set("baseEvasRate", 38);
    npcDat.set("baseCritRate", 38);

    // npcDat.set("name", "");
    npcDat.set("collision_radius", 0);
    npcDat.set("collision_height", 0);
    npcDat.set("sex", "male");
    npcDat.set("type", "");
    npcDat.set("baseAtkRange", 0);
    npcDat.set("baseMpMax", 0);
    npcDat.set("baseCpMax", 0);
    npcDat.set("rewardExp", 0);
    npcDat.set("rewardSp", 0);
    npcDat.set("basePAtk", 0);
    npcDat.set("baseMAtk", 0);
    npcDat.set("basePAtkSpd", 0);
    npcDat.set("aggroRange", 0);
    npcDat.set("baseMAtkSpd", 0);
    npcDat.set("rhand", 0);
    npcDat.set("lhand", 0);
    npcDat.set("armor", 0);
    npcDat.set("baseWalkSpd", 0);
    npcDat.set("baseRunSpd", 0);
    npcDat.set("baseHpMax", 50000);
    npcDat.set("baseHpReg", 3.e-3f);
    npcDat.set("baseMpReg", 3.e-3f);
    npcDat.set("basePDef", 100);
    npcDat.set("baseMDef", 100);

    final L2BoatInstance boat = new L2BoatInstance(new L2CharTemplate(npcDat));
    boat.setHeading(heading);
    boat.setXYZInvisible(x, y, z);
    boat.spawnMe();

    _boats.put(boat.getObjectId(), boat);
    return boat;
  }

  /**
   * @param boatId
   * @return
   */
  public L2BoatInstance getBoat(int boatId) {
    return _boats.get(boatId);
  }

  /**
   * Lock/unlock dock so only one ship can be docked
   *
   * @param h Dock Id
   * @param value True if dock is locked
   */
  public void dockShip(int h, boolean value) {
    try {
      _docksBusy[h] = value;
    } catch (ArrayIndexOutOfBoundsException e) {
    }
  }

  /**
   * Check if dock is busy
   *
   * @param h Dock Id
   * @return Trye if dock is locked
   */
  public boolean dockBusy(int h) {
    try {
      return _docksBusy[h];
    } catch (ArrayIndexOutOfBoundsException e) {
      return false;
    }
  }

  /** Broadcast one packet in both path points */
  public void broadcastPacket(
      VehiclePathPoint point1, VehiclePathPoint point2, L2GameServerPacket packet) {
    broadcastPacketsToPlayers(point1, point2, packet);
  }

  private void broadcastPacketsToPlayers(
      VehiclePathPoint point1, VehiclePathPoint point2, L2GameServerPacket... packets) {
    for (L2PcInstance player : L2World.getInstance().getPlayers()) {
      if (Math.hypot(player.getX() - point1.getX(), player.getY() - point1.getY())
          < general().getBoatBroadcastRadius()) {
        for (L2GameServerPacket p : packets) {
          player.sendPacket(p);
        }
      } else {
        if (Math.hypot(player.getX() - point2.getX(), player.getY() - point2.getY())
            < general().getBoatBroadcastRadius()) {
          for (L2GameServerPacket p : packets) {
            player.sendPacket(p);
          }
        }
      }
    }
  }

  /** Broadcast several packets in both path points */
  public void broadcastPackets(
      VehiclePathPoint point1, VehiclePathPoint point2, L2GameServerPacket... packets) {
    broadcastPacketsToPlayers(point1, point2, packets);
  }

  private static class SingletonHolder {
    protected static final BoatManager _instance = new BoatManager();
  }
}
