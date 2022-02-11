
package com.l2jserver.datapack.handlers.usercommandhandlers;

import com.l2jserver.gameserver.handler.IUserCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class Mount implements IUserCommandHandler {
	private static final int[] COMMAND_IDS = {
		61
	};
	
	@Override
	public synchronized boolean useUserCommand(int id, L2PcInstance activeChar) {
		if (id != COMMAND_IDS[0]) {
			return false;
		}
		return activeChar.mountPlayer(activeChar.getSummon());
	}
	
	@Override
	public int[] getUserCommandList() {
		return COMMAND_IDS;
	}
}
