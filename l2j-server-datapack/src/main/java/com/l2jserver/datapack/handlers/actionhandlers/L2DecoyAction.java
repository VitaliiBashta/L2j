
package com.l2jserver.datapack.handlers.actionhandlers;

import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.handler.IActionHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;

public class L2DecoyAction implements IActionHandler {
	@Override
	public boolean action(L2PcInstance activeChar, L2Object target, boolean interact) {
		// Aggression target lock effect
		if (activeChar.isLockedTarget() && (activeChar.getLockedTarget() != target)) {
			activeChar.sendPacket(SystemMessageId.FAILED_CHANGE_TARGET);
			return false;
		}
		
		activeChar.setTarget(target);
		return true;
	}
	
	@Override
	public InstanceType getInstanceType() {
		return InstanceType.L2Decoy;
	}
}