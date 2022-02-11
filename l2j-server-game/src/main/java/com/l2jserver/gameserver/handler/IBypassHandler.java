package com.l2jserver.gameserver.handler;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.logging.Logger;

public interface IBypassHandler {
  Logger _log = Logger.getLogger(IBypassHandler.class.getName());

  /** This is the worker method that is called when someone uses an bypass command. */
  boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin);

  /** This method is called at initialization to register all bypasses automatically. */
  String[] getBypassList();
}
