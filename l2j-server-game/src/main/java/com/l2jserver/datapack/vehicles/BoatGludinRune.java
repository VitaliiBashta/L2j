package com.l2jserver.datapack.vehicles;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.instancemanager.BoatManager;
import com.l2jserver.gameserver.model.VehiclePathPoint;
import com.l2jserver.gameserver.model.actor.instance.L2BoatInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class BoatGludinRune implements Runnable {
  private static final Logger _log = Logger.getLogger(BoatGludinRune.class.getName());

  // Time: 1151s
  private static final VehiclePathPoint[] GLUDIN_TO_RUNE = {
    new VehiclePathPoint(-95686, 155514, -3610, 150, 800),
    new VehiclePathPoint(-98112, 159040, -3610, 150, 800),
    new VehiclePathPoint(-104192, 160608, -3610, 200, 1800),
    new VehiclePathPoint(-109952, 159616, -3610, 250, 1800),
    new VehiclePathPoint(-112768, 154784, -3610, 290, 1800),
    new VehiclePathPoint(-114688, 139040, -3610, 290, 1800),
    new VehiclePathPoint(-115232, 134368, -3610, 290, 1800),
    new VehiclePathPoint(-113888, 121696, -3610, 290, 1800),
    new VehiclePathPoint(-107808, 104928, -3610, 290, 1800),
    new VehiclePathPoint(-97152, 75520, -3610, 290, 800),
    new VehiclePathPoint(-85536, 67264, -3610, 290, 1800),
    new VehiclePathPoint(-64640, 55840, -3610, 290, 1800),
    new VehiclePathPoint(-60096, 44672, -3610, 290, 1800),
    new VehiclePathPoint(-52672, 37440, -3610, 290, 1800),
    new VehiclePathPoint(-46144, 33184, -3610, 290, 1800),
    new VehiclePathPoint(-36096, 24928, -3610, 290, 1800),
    new VehiclePathPoint(-33792, 8448, -3610, 290, 1800),
    new VehiclePathPoint(-23776, 3424, -3610, 290, 1000),
    new VehiclePathPoint(-12000, -1760, -3610, 290, 1000),
    new VehiclePathPoint(672, 480, -3610, 290, 1800),
    new VehiclePathPoint(15488, 200, -3610, 290, 1000),
    new VehiclePathPoint(24736, 164, -3610, 290, 1000),
    new VehiclePathPoint(32192, -1156, -3610, 290, 1000),
    new VehiclePathPoint(39200, -8032, -3610, 270, 1000),
    new VehiclePathPoint(44320, -25152, -3610, 270, 1000),
    new VehiclePathPoint(40576, -31616, -3610, 250, 800),
    new VehiclePathPoint(36819, -35315, -3610, 220, 800)
  };

  private static final VehiclePathPoint[] RUNE_DOCK = {
    new VehiclePathPoint(34381, -37680, -3610, 200, 800)
  };

  // Time: 967s
  private static final VehiclePathPoint[] RUNE_TO_GLUDIN = {
    new VehiclePathPoint(32750, -39300, -3610, 150, 800),
    new VehiclePathPoint(27440, -39328, -3610, 180, 1000),
    new VehiclePathPoint(21456, -34272, -3610, 200, 1000),
    new VehiclePathPoint(6608, -29520, -3610, 250, 800),
    new VehiclePathPoint(4160, -27828, -3610, 270, 800),
    new VehiclePathPoint(2432, -25472, -3610, 270, 1000),
    new VehiclePathPoint(-8000, -16272, -3610, 220, 1000),
    new VehiclePathPoint(-18976, -9760, -3610, 290, 800),
    new VehiclePathPoint(-23776, 3408, -3610, 290, 800),
    new VehiclePathPoint(-33792, 8432, -3610, 290, 800),
    new VehiclePathPoint(-36096, 24912, -3610, 290, 800),
    new VehiclePathPoint(-46144, 33184, -3610, 290, 800),
    new VehiclePathPoint(-52688, 37440, -3610, 290, 800),
    new VehiclePathPoint(-60096, 44672, -3610, 290, 800),
    new VehiclePathPoint(-64640, 55840, -3610, 290, 800),
    new VehiclePathPoint(-85552, 67248, -3610, 290, 800),
    new VehiclePathPoint(-97168, 85264, -3610, 290, 800),
    new VehiclePathPoint(-107824, 104912, -3610, 290, 800),
    new VehiclePathPoint(-102151, 135704, -3610, 290, 800),
    new VehiclePathPoint(-96686, 140595, -3610, 290, 800),
    new VehiclePathPoint(-95686, 147717, -3610, 250, 800),
    new VehiclePathPoint(-95686, 148218, -3610, 200, 800)
  };

  private static final VehiclePathPoint[] GLUDIN_DOCK = {
    new VehiclePathPoint(-95686, 150514, -3610, 150, 800)
  };

  private final L2BoatInstance boat;
  private final CreatureSay ARRIVED_AT_GLUDIN;
  private final CreatureSay ARRIVED_AT_GLUDIN_2;
  private final CreatureSay LEAVE_GLUDIN5;
  private final CreatureSay LEAVE_GLUDIN1;
  private final CreatureSay LEAVE_GLUDIN0;
  private final CreatureSay LEAVING_GLUDIN;
  private final CreatureSay ARRIVED_AT_RUNE;
  private final CreatureSay ARRIVED_AT_RUNE_2;
  private final CreatureSay LEAVE_RUNE5;
  private final CreatureSay LEAVE_RUNE1;
  private final CreatureSay LEAVE_RUNE0;
  private final CreatureSay LEAVING_RUNE;
  private final CreatureSay BUSY_GLUDIN;
  private final CreatureSay BUSY_RUNE;
  private final CreatureSay ARRIVAL_RUNE15;
  private final CreatureSay ARRIVAL_RUNE10;
  private final CreatureSay ARRIVAL_RUNE5;
  private final CreatureSay ARRIVAL_RUNE1;
  private final CreatureSay ARRIVAL_GLUDIN15;
  private final CreatureSay ARRIVAL_GLUDIN10;
  private final CreatureSay ARRIVAL_GLUDIN5;
  private final CreatureSay ARRIVAL_GLUDIN1;
  private final BoatManager boatManager;
  private final ThreadPoolManager threadPoolManager;
  private int _cycle = 0;
  private int _shoutCount = 0;

  public BoatGludinRune(BoatManager boatManager, ThreadPoolManager threadPoolManager) {
    this.boatManager = boatManager;
    boat = boatManager.getNewBoat(3, -95686, 150514, -3610, 16723);
    this.threadPoolManager = threadPoolManager;
    if (boat != null) {
      boat.registerEngine(new BoatGludinRune(boatManager, threadPoolManager));
      boat.runEngine(180000);
      boatManager.dockShip(BoatManager.GLUDIN_HARBOR, true);
    }

    ARRIVED_AT_GLUDIN = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_ARRIVED_AT_GLUDIN);
    ARRIVED_AT_GLUDIN_2 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.DEPARTURE_FOR_RUNE_10_MINUTES);
    LEAVE_GLUDIN5 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.DEPARTURE_FOR_RUNE_5_MINUTES);
    LEAVE_GLUDIN1 = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.DEPARTURE_FOR_RUNE_1_MINUTE);
    LEAVE_GLUDIN0 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.DEPARTURE_FOR_GLUDIN_SHORTLY2);
    LEAVING_GLUDIN = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.DEPARTURE_FOR_GLUDIN_NOW);
    ARRIVED_AT_RUNE = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.ARRIVED_AT_RUNE);
    ARRIVED_AT_RUNE_2 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_LEAVE_FOR_GLUDIN_AFTER_10_MINUTES);
    LEAVE_RUNE5 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.DEPARTURE_FOR_GLUDIN_5_MINUTES);
    LEAVE_RUNE1 = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.DEPARTURE_FOR_GLUDIN_1_MINUTE);
    LEAVE_RUNE0 = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.DEPARTURE_FOR_GLUDIN_SHORTLY);
    LEAVING_RUNE = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.DEPARTURE_FOR_GLUDIN_NOW);
    BUSY_GLUDIN = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_RUNE_GLUDIN_DELAYED);
    BUSY_RUNE = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_GLUDIN_RUNE_DELAYED);

    ARRIVAL_RUNE15 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_FROM_GLUDIN_AT_RUNE_15_MINUTES);
    ARRIVAL_RUNE10 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_FROM_GLUDIN_AT_RUNE_10_MINUTES);
    ARRIVAL_RUNE5 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_FROM_GLUDIN_AT_RUNE_5_MINUTES);
    ARRIVAL_RUNE1 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_FROM_GLUDIN_AT_RUNE_1_MINUTE);
    ARRIVAL_GLUDIN15 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_FROM_RUNE_AT_GLUDIN_15_MINUTES);
    ARRIVAL_GLUDIN10 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_FROM_RUNE_AT_GLUDIN_10_MINUTES);
    ARRIVAL_GLUDIN5 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_FROM_RUNE_AT_GLUDIN_5_MINUTES);
    ARRIVAL_GLUDIN1 =
        new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_FROM_RUNE_AT_GLUDIN_1_MINUTE);
  }

  @Override
  public void run() {
    try {
      switch (_cycle) {
        case 0:
          boatManager.broadcastPacket(GLUDIN_DOCK[0], RUNE_DOCK[0], LEAVE_GLUDIN5);
          threadPoolManager.scheduleGeneral(this, 240000);
          break;
        case 1:
          boatManager.broadcastPacket(GLUDIN_DOCK[0], RUNE_DOCK[0], LEAVE_GLUDIN1);
          threadPoolManager.scheduleGeneral(this, 40000);
          break;
        case 2:
          boatManager.broadcastPacket(GLUDIN_DOCK[0], RUNE_DOCK[0], LEAVE_GLUDIN0);
          threadPoolManager.scheduleGeneral(this, 20000);
          break;
        case 3:
          boatManager.dockShip(BoatManager.GLUDIN_HARBOR, false);
          boatManager.broadcastPackets(GLUDIN_DOCK[0], RUNE_DOCK[0], LEAVING_GLUDIN);
          boat.broadcastPacket(Sound.ITEMSOUND_SHIP_ARRIVAL_DEPARTURE.withObject(boat));
          boat.payForRide(7905, 1, -90015, 150422, -3610);
          boat.executePath(GLUDIN_TO_RUNE);
          threadPoolManager.scheduleGeneral(this, 250000);
          break;
        case 4:
          boatManager.broadcastPacket(RUNE_DOCK[0], GLUDIN_DOCK[0], ARRIVAL_RUNE15);
          threadPoolManager.scheduleGeneral(this, 300000);
          break;
        case 5:
          boatManager.broadcastPacket(RUNE_DOCK[0], GLUDIN_DOCK[0], ARRIVAL_RUNE10);
          threadPoolManager.scheduleGeneral(this, 300000);
          break;
        case 6:
          boatManager.broadcastPacket(RUNE_DOCK[0], GLUDIN_DOCK[0], ARRIVAL_RUNE5);
          threadPoolManager.scheduleGeneral(this, 240000);
          break;
        case 7:
          boatManager.broadcastPacket(RUNE_DOCK[0], GLUDIN_DOCK[0], ARRIVAL_RUNE1);
          break;
        case 8:
          if (boatManager.dockBusy(BoatManager.RUNE_HARBOR)) {
            if (_shoutCount == 0) {
              boatManager.broadcastPacket(RUNE_DOCK[0], GLUDIN_DOCK[0], BUSY_RUNE);
            }

            _shoutCount++;
            if (_shoutCount > 35) {
              _shoutCount = 0;
            }

            threadPoolManager.scheduleGeneral(this, 5000);
            return;
          }
          boat.executePath(RUNE_DOCK);
          break;
        case 9:
          boatManager.dockShip(BoatManager.RUNE_HARBOR, true);
          boatManager.broadcastPackets(
              RUNE_DOCK[0], GLUDIN_DOCK[0], ARRIVED_AT_RUNE, ARRIVED_AT_RUNE_2);
          boat.broadcastPacket(Sound.ITEMSOUND_SHIP_ARRIVAL_DEPARTURE.withObject(boat));
          threadPoolManager.scheduleGeneral(this, 300000);
          break;
        case 10:
          boatManager.broadcastPacket(RUNE_DOCK[0], GLUDIN_DOCK[0], LEAVE_RUNE5);
          threadPoolManager.scheduleGeneral(this, 240000);
          break;
        case 11:
          boatManager.broadcastPacket(RUNE_DOCK[0], GLUDIN_DOCK[0], LEAVE_RUNE1);
          threadPoolManager.scheduleGeneral(this, 40000);
          break;
        case 12:
          boatManager.broadcastPacket(RUNE_DOCK[0], GLUDIN_DOCK[0], LEAVE_RUNE0);
          threadPoolManager.scheduleGeneral(this, 20000);
          break;
        case 13:
          boatManager.dockShip(BoatManager.RUNE_HARBOR, false);
          boatManager.broadcastPackets(RUNE_DOCK[0], GLUDIN_DOCK[0], LEAVING_RUNE);
          boat.broadcastPacket(Sound.ITEMSOUND_SHIP_ARRIVAL_DEPARTURE.withObject(boat));
          boat.payForRide(7904, 1, 34513, -38009, -3640);
          boat.executePath(RUNE_TO_GLUDIN);
          threadPoolManager.scheduleGeneral(this, 60000);
          break;
        case 14:
          boatManager.broadcastPacket(GLUDIN_DOCK[0], RUNE_DOCK[0], ARRIVAL_GLUDIN15);
          threadPoolManager.scheduleGeneral(this, 300000);
          break;
        case 15:
          boatManager.broadcastPacket(GLUDIN_DOCK[0], RUNE_DOCK[0], ARRIVAL_GLUDIN10);
          threadPoolManager.scheduleGeneral(this, 300000);
          break;
        case 16:
          boatManager.broadcastPacket(GLUDIN_DOCK[0], RUNE_DOCK[0], ARRIVAL_GLUDIN5);
          threadPoolManager.scheduleGeneral(this, 240000);
          break;
        case 17:
          boatManager.broadcastPacket(GLUDIN_DOCK[0], RUNE_DOCK[0], ARRIVAL_GLUDIN1);
          break;
        case 18:
          if (boatManager.dockBusy(BoatManager.GLUDIN_HARBOR)) {
            if (_shoutCount == 0) {
              boatManager.broadcastPacket(GLUDIN_DOCK[0], RUNE_DOCK[0], BUSY_GLUDIN);
            }

            _shoutCount++;
            if (_shoutCount > 35) {
              _shoutCount = 0;
            }

            threadPoolManager.scheduleGeneral(this, 5000);
            return;
          }
          boat.executePath(GLUDIN_DOCK);
          break;
        case 19:
          boatManager.dockShip(BoatManager.GLUDIN_HARBOR, true);
          boatManager.broadcastPackets(
              GLUDIN_DOCK[0], RUNE_DOCK[0], ARRIVED_AT_GLUDIN, ARRIVED_AT_GLUDIN_2);
          boat.broadcastPacket(Sound.ITEMSOUND_SHIP_ARRIVAL_DEPARTURE.withObject(boat));
          threadPoolManager.scheduleGeneral(this, 300000);
          break;
      }
      _shoutCount = 0;
      _cycle++;
      if (_cycle > 19) {
        _cycle = 0;
      }
    } catch (Exception e) {
      _log.log(Level.WARNING, e.getMessage());
    }
  }
}
