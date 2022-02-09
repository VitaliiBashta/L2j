package com.l2jserver.gameserver.handler;

import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public interface IActionHandler {

  boolean action(L2PcInstance activeChar, L2Object target, boolean interact);

  InstanceType getInstanceType();
}
