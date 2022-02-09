
package com.l2jserver.datapack.handlers.actionshifthandlers;

import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.handler.AdminCommandHandler;
import com.l2jserver.gameserver.handler.IActionShiftHandler;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class L2PcInstanceActionShift implements IActionShiftHandler {
	@Override
	public boolean action(L2PcInstance activeChar, L2Object target, boolean interact) {
		if (activeChar.isGM()) {
			// Check if the gm already target this l2pcinstance
			if (activeChar.getTarget() != target) {
				// Set the target of the L2PcInstance activeChar
				activeChar.setTarget(target);
			}
			
			IAdminCommandHandler ach = AdminCommandHandler.getInstance().getHandler("admin_character_info");
			if (ach != null) {
				ach.useAdminCommand("admin_character_info " + target.getName(), activeChar);
			}
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType() {
		return InstanceType.L2PcInstance;
	}
}
