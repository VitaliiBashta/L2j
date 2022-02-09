
package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;

public class AdminDebug implements IAdminCommandHandler {
	private static final String[] ADMIN_COMMANDS = {
		"admin_debug"
	};
	
	@Override
	public final boolean useAdminCommand(String command, L2PcInstance activeChar) {
		String[] commandSplit = command.split(" ");
		if (ADMIN_COMMANDS[0].equalsIgnoreCase(commandSplit[0])) {
			L2Object target;
			if (commandSplit.length > 1) {
				target = L2World.getInstance().getPlayer(commandSplit[1].trim());
				if (target == null) {
					activeChar.sendPacket(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
					return true;
				}
			} else {
				target = activeChar.getTarget();
			}
			
			if (target instanceof L2Character) {
				setDebug(activeChar, (L2Character) target);
			} else {
				setDebug(activeChar, activeChar);
			}
		}
		return true;
	}
	
	@Override
	public final String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
	
	private final void setDebug(L2PcInstance activeChar, L2Character target) {
		if (target.isDebug()) {
			target.setDebug(null);
			activeChar.sendMessage("Stop debugging " + target.getName());
		} else {
			target.setDebug(activeChar);
			activeChar.sendMessage("Start debugging " + target.getName());
		}
	}
}