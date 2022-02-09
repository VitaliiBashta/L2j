
package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class AdminBBS implements IAdminCommandHandler {
	private static final String[] ADMIN_COMMANDS = {
		"admin_bbs"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}