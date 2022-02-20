package com.l2jserver.gameserver.ai;

import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.instance.L2AirShipInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExMoveToLocationAirShip;
import com.l2jserver.gameserver.network.serverpackets.ExStopMoveAirShip;

public class L2AirShipAI extends L2VehicleAI {
  public L2AirShipAI(L2AirShipInstance creature) {
    super(creature);
  }

  @Override
  protected void moveTo(int x, int y, int z) {
    if (!_actor.isMovementDisabled()) {
      _clientMoving = true;
      _actor.moveToLocation(x, y, z, 0);
      _actor.broadcastPacket(new ExMoveToLocationAirShip(getActor()));
    }
  }

  @Override
  protected void clientStopMoving(Location loc) {
    if (_actor.isMoving()) {
      _actor.stopMove(loc);
    }

    if (_clientMoving || (loc != null)) {
      _clientMoving = false;
      _actor.broadcastPacket(new ExStopMoveAirShip(getActor()));
    }
  }

  @Override
  public void describeStateToPlayer(L2PcInstance player) {
    if (_clientMoving) {
      player.sendPacket(new ExMoveToLocationAirShip(getActor()));
    }
  }

  @Override
  public L2AirShipInstance getActor() {
    return (L2AirShipInstance) _actor;
  }
}
