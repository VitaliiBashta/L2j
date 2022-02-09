package com.l2jserver.gameserver.model.zone.type;

import com.l2jserver.gameserver.GameServer;
import com.l2jserver.gameserver.model.TeleportWhereType;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.model.zone.ZoneId;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class L2NoRestartZone extends L2ZoneType {
  private int restartAllowedTime = 0;
  private int restartTime = 0;
  private boolean enabled = true;

  public L2NoRestartZone(int id) {
    super(id);
  }

  @Override
  public void setParameter(String name, String value) {
    if (name.equalsIgnoreCase("default_enabled")) {
      enabled = Boolean.parseBoolean(value);
    } else if (name.equalsIgnoreCase("restartAllowedTime")) {
      restartAllowedTime = Integer.parseInt(value) * 1000;
    } else if (name.equalsIgnoreCase("restartTime")) {
      restartTime = Integer.parseInt(value) * 1000;
    } else if (name.equalsIgnoreCase("instanceId")) {
      // Do nothing.
    } else {
      super.setParameter(name, value);
    }
  }

  @Override
  protected void onEnter(L2Character character) {
    if (!enabled) {
      return;
    }

    if (character.isPlayer()) {
      character.setInsideZone(ZoneId.NO_RESTART, true);
    }
  }

  @Override
  protected void onExit(L2Character character) {
    if (!enabled) {
      return;
    }

    if (character.isPlayer()) {
      character.setInsideZone(ZoneId.NO_RESTART, false);
    }
  }

  @Override
  public void onPlayerLoginInside(L2PcInstance player) {
    if (!enabled) {
      return;
    }

    var now = LocalDateTime.now();
    boolean reEnterIsPossible =
        now.isAfter(
            LocalDateTime.ofEpochSecond(player.getLastAccess() + restartTime, 0, ZoneOffset.UTC));
    boolean serverIsRunning =
        now.isAfter(GameServer.dateTimeServerStarted.plusSeconds(restartAllowedTime));
    if (reEnterIsPossible && serverIsRunning) {
      player.teleToLocation(TeleportWhereType.TOWN);
    }
  }

  public int getRestartTime() {
    return restartTime;
  }

  public void setRestartTime(int time) {
    restartTime = time;
  }
}
