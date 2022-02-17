package com.l2jserver.datapack.vehicles;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.instancemanager.BoatManager;
import com.l2jserver.gameserver.model.VehiclePathPoint;
import com.l2jserver.gameserver.model.actor.instance.L2BoatInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;


@Service
public class BoatTalkingGludin implements Runnable {
  private static final Logger LOG = LogManager.getLogger(BoatTalkingGludin.class.getName());

  // Time: 919s
  private static final VehiclePathPoint[] TALKING_TO_GLUDIN = {
    new VehiclePathPoint(-121385, 261660, -3610, 180, 800),
    new VehiclePathPoint(-127694, 253312, -3610, 200, 800),
    new VehiclePathPoint(-129274, 237060, -3610, 250, 800),
    new VehiclePathPoint(-114688, 139040, -3610, 200, 800),
    new VehiclePathPoint(-109663, 135704, -3610, 180, 800),
    new VehiclePathPoint(-102151, 135704, -3610, 180, 800),
    new VehiclePathPoint(-96686, 140595, -3610, 180, 800),
    new VehiclePathPoint(-95686, 147718, -3610, 180, 800),
    new VehiclePathPoint(-95686, 148718, -3610, 180, 800),
    new VehiclePathPoint(-95686, 149718, -3610, 150, 800)
  };

  private static final VehiclePathPoint GLUDIN_DOCK =
    new VehiclePathPoint(-95686, 150514, -3610, 150, 800)  ;

  // Time: 780s
  private static final VehiclePathPoint[] GLUDIN_TO_TALKING = {
    new VehiclePathPoint(-95686, 155514, -3610, 180, 800),
    new VehiclePathPoint(-95686, 185514, -3610, 250, 800),
    new VehiclePathPoint(-60136, 238816, -3610, 200, 800),
    new VehiclePathPoint(-60520, 259609, -3610, 180, 1800),
    new VehiclePathPoint(-65344, 261460, -3610, 180, 1800),
    new VehiclePathPoint(-83344, 261560, -3610, 180, 1800),
    new VehiclePathPoint(-88344, 261660, -3610, 180, 1800),
    new VehiclePathPoint(-92344, 261660, -3610, 150, 1800),
    new VehiclePathPoint(-94242, 261659, -3610, 150, 1800)
  };

  private static final VehiclePathPoint[] TALKING_DOCK = {
    new VehiclePathPoint(-96622, 261660, -3610, 150, 1800)
  };
  private final BoatManager boatManager;

  private final L2BoatInstance boat;
  private final CreatureSay ARRIVED_AT_TALKING;
  private final CreatureSay ARRIVED_AT_TALKING_2;
  private final CreatureSay LEAVE_TALKING5;
  private final CreatureSay LEAVE_TALKING1;
  private final CreatureSay LEAVE_TALKING1_2;
  private final CreatureSay LEAVE_TALKING0;
  private final CreatureSay LEAVING_TALKING;
  private final CreatureSay ARRIVED_AT_GLUDIN;
  private final CreatureSay ARRIVED_AT_GLUDIN_2;
  private final CreatureSay LEAVE_GLUDIN5;
  private final CreatureSay LEAVE_GLUDIN1;
  private final CreatureSay LEAVE_GLUDIN0;
  private final CreatureSay LEAVING_GLUDIN;
  private final CreatureSay BUSY_TALKING;
  private final CreatureSay BUSY_GLUDIN;
  private final CreatureSay ARRIVAL_GLUDIN10;
  private final CreatureSay ARRIVAL_GLUDIN5;
  private final CreatureSay ARRIVAL_GLUDIN1;
  private final CreatureSay ARRIVAL_TALKING10;
  private final CreatureSay ARRIVAL_TALKING5;
  private final CreatureSay ARRIVAL_TALKING1;
  private final ThreadPoolManager threadPoolManager;
  private int cycle;
  private int shoutCount = 0;

  public BoatTalkingGludin(BoatManager boatManager, ThreadPoolManager threadPoolManager) {
    this.boatManager = boatManager;
    this.threadPoolManager = threadPoolManager;
    boat = boatManager.getNewBoat(1, -96622, 261660, -3610, 32768);
    if (boat != null) {
      boat.registerEngine(this);
      boat.runEngine(180000);
      boatManager.dockShip(BoatManager.TALKING_ISLAND, true);
    }
    cycle = 0;

    ARRIVED_AT_TALKING =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_ARRIVED_AT_TALKING);
    ARRIVED_AT_TALKING_2 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_LEAVE_FOR_GLUDIN_AFTER_10_MINUTES);
    LEAVE_TALKING5 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_LEAVE_FOR_GLUDIN_IN_5_MINUTES);
    LEAVE_TALKING1 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_LEAVE_FOR_GLUDIN_IN_1_MINUTE);
    LEAVE_TALKING1_2 = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.MAKE_HASTE_GET_ON_BOAT);
    LEAVE_TALKING0 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_LEAVE_SOON_FOR_GLUDIN);
    LEAVING_TALKING = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_LEAVING_FOR_GLUDIN);
    ARRIVED_AT_GLUDIN = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_ARRIVED_AT_GLUDIN);
    ARRIVED_AT_GLUDIN_2 =
        new CreatureSay(
            0, Say2.BOAT, 801, SystemMessageId.FERRY_LEAVE_FOR_TALKING_AFTER_10_MINUTES);
    LEAVE_GLUDIN5 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_LEAVE_FOR_TALKING_IN_5_MINUTES);
    LEAVE_GLUDIN1 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_LEAVE_FOR_TALKING_IN_1_MINUTE);
    LEAVE_GLUDIN0 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_LEAVE_SOON_FOR_TALKING);
    LEAVING_GLUDIN = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_LEAVING_FOR_TALKING);
    BUSY_TALKING = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_GLUDIN_TALKING_DELAYED);
    BUSY_GLUDIN = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_TALKING_GLUDIN_DELAYED);

    ARRIVAL_GLUDIN10 =
        new CreatureSay(
            0, Say2.BOAT, 801, SystemMessageId.FERRY_FROM_TALKING_ARRIVE_AT_GLUDIN_10_MINUTES);
    ARRIVAL_GLUDIN5 =
        new CreatureSay(
            0, Say2.BOAT, 801, SystemMessageId.FERRY_FROM_TALKING_ARRIVE_AT_GLUDIN_5_MINUTES);
    ARRIVAL_GLUDIN1 =
        new CreatureSay(
            0, Say2.BOAT, 801, SystemMessageId.FERRY_FROM_TALKING_ARRIVE_AT_GLUDIN_1_MINUTE);
    ARRIVAL_TALKING10 =
        new CreatureSay(
            0, Say2.BOAT, 801, SystemMessageId.FERRY_FROM_GLUDIN_ARRIVE_AT_TALKING_10_MINUTES);
    ARRIVAL_TALKING5 =
        new CreatureSay(
            0, Say2.BOAT, 801, SystemMessageId.FERRY_FROM_GLUDIN_ARRIVE_AT_TALKING_5_MINUTES);
    ARRIVAL_TALKING1 =
        new CreatureSay(
            0, Say2.BOAT, 801, SystemMessageId.FERRY_FROM_GLUDIN_ARRIVE_AT_TALKING_1_MINUTE);
  }

  @Override
  public void run() {
    try {
      if (actionOnCycle(cycle)) return;
      shoutCount = 0;
      cycle++;
      if (cycle > 17) {
        cycle = 0;
      }
    } catch (Exception e) {
      LOG.warn(e.getMessage());
    }
  }

  private boolean actionOnCycle(int cycle) {
    switch (cycle) {
      case 0 -> {
        boatManager.broadcastPacket(TALKING_DOCK[0], GLUDIN_DOCK, LEAVE_TALKING5);

        threadPoolManager.scheduleGeneral(this, 240000);
      }
      case 1 -> {
        boatManager.broadcastPackets(
                TALKING_DOCK[0], GLUDIN_DOCK, LEAVE_TALKING1, LEAVE_TALKING1_2);
        threadPoolManager.scheduleGeneral(this, 40000);
      }
      case 2 -> {
        boatManager.broadcastPacket(TALKING_DOCK[0], GLUDIN_DOCK, LEAVE_TALKING0);
        threadPoolManager.scheduleGeneral(this, 20000);
      }
      case 3 -> {
        boatManager.dockShip(BoatManager.TALKING_ISLAND, false);
        boatManager.broadcastPackets(TALKING_DOCK[0], GLUDIN_DOCK, LEAVING_TALKING);
        boat.broadcastPacket(Sound.ITEMSOUND_SHIP_ARRIVAL_DEPARTURE.withObject(boat));
        boat.payForRide(1074, 1, -96777, 258970, -3623);
        boat.executePath(TALKING_TO_GLUDIN);
        threadPoolManager.scheduleGeneral(this, 300000);
      }
      case 4 -> {
        boatManager.broadcastPacket(GLUDIN_DOCK, TALKING_DOCK[0], ARRIVAL_GLUDIN10);
        threadPoolManager.scheduleGeneral(this, 300000);
      }
      case 5 -> {
        boatManager.broadcastPacket(GLUDIN_DOCK, TALKING_DOCK[0], ARRIVAL_GLUDIN5);
        threadPoolManager.scheduleGeneral(this, 240000);
      }
      case 6 -> boatManager.broadcastPacket(GLUDIN_DOCK, TALKING_DOCK[0], ARRIVAL_GLUDIN1);
      case 7 -> {
        if (boatManager.dockBusy(BoatManager.GLUDIN_HARBOR)) {
          if (shoutCount == 0) {
            boatManager.broadcastPacket(GLUDIN_DOCK, TALKING_DOCK[0], BUSY_GLUDIN);
          }

          shoutCount++;
          if (shoutCount > 35) {
            shoutCount = 0;
          }

          threadPoolManager.scheduleGeneral(this, 5000);
          return true;
        }
        boat.executePath(new VehiclePathPoint[]{GLUDIN_DOCK});
      }
      case 8 -> {
        boatManager.dockShip(BoatManager.GLUDIN_HARBOR, true);
        boatManager.broadcastPackets(
                GLUDIN_DOCK, TALKING_DOCK[0], ARRIVED_AT_GLUDIN, ARRIVED_AT_GLUDIN_2);
        boat.broadcastPacket(Sound.ITEMSOUND_SHIP_ARRIVAL_DEPARTURE.withObject(boat));
        threadPoolManager.scheduleGeneral(this, 300000);
      }
      case 9 -> {
        boatManager.broadcastPacket(TALKING_DOCK[0], GLUDIN_DOCK, LEAVE_GLUDIN5);
        threadPoolManager.scheduleGeneral(this, 240000);
      }
      case 10 -> {
        boatManager.broadcastPackets(
                TALKING_DOCK[0], GLUDIN_DOCK, LEAVE_GLUDIN1, LEAVE_TALKING1_2);
        threadPoolManager.scheduleGeneral(this, 40000);
      }
      case 11 -> {
        boatManager.broadcastPacket(TALKING_DOCK[0], GLUDIN_DOCK, LEAVE_GLUDIN0);
        threadPoolManager.scheduleGeneral(this, 20000);
      }
      case 12 -> {
        boatManager.dockShip(BoatManager.GLUDIN_HARBOR, false);
        boatManager.broadcastPackets(TALKING_DOCK[0], GLUDIN_DOCK, LEAVING_GLUDIN);
        boat.broadcastPacket(Sound.ITEMSOUND_SHIP_ARRIVAL_DEPARTURE.withObject(boat));
        boat.payForRide(1075, 1, -90015, 150422, -3610);
        boat.executePath(GLUDIN_TO_TALKING);
        threadPoolManager.scheduleGeneral(this, 150000);
      }
      case 13 -> {
        boatManager.broadcastPacket(TALKING_DOCK[0], GLUDIN_DOCK, ARRIVAL_TALKING10);
        threadPoolManager.scheduleGeneral(this, 300000);
      }
      case 14 -> {
        boatManager.broadcastPacket(TALKING_DOCK[0], GLUDIN_DOCK, ARRIVAL_TALKING5);
        threadPoolManager.scheduleGeneral(this, 240000);
      }
      case 15 -> boatManager.broadcastPacket(TALKING_DOCK[0], GLUDIN_DOCK, ARRIVAL_TALKING1);
      case 16 -> {
        if (boatManager.dockBusy(BoatManager.TALKING_ISLAND)) {
          if (shoutCount == 0) {
            boatManager.broadcastPacket(TALKING_DOCK[0], GLUDIN_DOCK, BUSY_TALKING);
          }

          shoutCount++;
          if (shoutCount > 35) {
            shoutCount = 0;
          }

          threadPoolManager.scheduleGeneral(this, 5000);
          return true;
        }
        boat.executePath(TALKING_DOCK);
      }
      case 17 -> {
        boatManager.dockShip(BoatManager.TALKING_ISLAND, true);
        boatManager.broadcastPackets(
                TALKING_DOCK[0], GLUDIN_DOCK, ARRIVED_AT_TALKING, ARRIVED_AT_TALKING_2);
        boat.broadcastPacket(Sound.ITEMSOUND_SHIP_ARRIVAL_DEPARTURE.withObject(boat));
        threadPoolManager.scheduleGeneral(this, 300000);
      }
    }
    return false;
  }
}
