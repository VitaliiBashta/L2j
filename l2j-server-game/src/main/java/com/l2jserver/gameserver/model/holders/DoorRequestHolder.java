package com.l2jserver.gameserver.model.holders;

import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;

public class DoorRequestHolder {
  private final L2DoorInstance door;

  public DoorRequestHolder(L2DoorInstance door) {
    this.door = door;
  }

  public L2DoorInstance getDoor() {
    return door;
  }
}
