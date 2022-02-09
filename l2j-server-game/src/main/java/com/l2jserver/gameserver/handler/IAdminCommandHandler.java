package com.l2jserver.gameserver.handler;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public interface IAdminCommandHandler {

  boolean useAdminCommand(String command, L2PcInstance activeChar);

  /** this method is called at initialization to register all the item ids automatically */
  String[] getAdminCommandList();
}
