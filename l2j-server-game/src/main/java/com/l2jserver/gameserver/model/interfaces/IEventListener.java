package com.l2jserver.gameserver.model.interfaces;

public interface IEventListener {

  boolean isOnEvent();

  boolean isBlockingExit();

  boolean isBlockingDeathPenalty();

  boolean canRevive();

}
