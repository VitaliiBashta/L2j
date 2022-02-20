package com.l2jserver.gameserver.model.entity;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.interfaces.IEventListener;

public final class TvTEventListener implements IEventListener {
  private final L2PcInstance player;

  protected TvTEventListener(L2PcInstance player) {
    this.player = player;
  }

  @Override
  public boolean isOnEvent() {
    return TvTEvent.isStarted() && TvTEvent.isPlayerParticipant(player.getObjectId());
  }

  @Override
  public boolean isBlockingExit() {
    return true;
  }

  @Override
  public boolean isBlockingDeathPenalty() {
    return true;
  }

  @Override
  public boolean canRevive() {
    return false;
  }
}
