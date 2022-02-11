
package com.l2jserver.datapack.handlers.actionshifthandlers;

import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.handler.AdminCommandHandler;
import com.l2jserver.gameserver.handler.IActionShiftHandler;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class L2SummonActionShift implements IActionShiftHandler {
	@Override
	public boolean action(L2PcInstance activeChar, L2Object target, boolean interact) {
		if (activeChar.isGM()) {
			if (activeChar.getTarget() != target) {
				// Set the target of the L2PcInstance activeChar
				activeChar.setTarget(target);
			}
			
			final IAdminCommandHandler ach = AdminCommandHandler.getInstance().getHandler("admin_summon_info");
			if (ach != null) {
				ach.useAdminCommand("admin_summon_info", activeChar);
			}
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType() {
		return InstanceType.L2Summon;
	}
}