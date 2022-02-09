
package com.l2jserver.datapack.handlers.usercommandhandlers;

import com.l2jserver.gameserver.handler.IUserCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Dismount user command.
 * @author Micht
 */
public class Dismount implements IUserCommandHandler {
	private static final int[] COMMAND_IDS = {
		62
	};
	
	@Override
	public synchronized boolean useUserCommand(int id, L2PcInstance activeChar) {
		if (id != COMMAND_IDS[0]) {
			return false;
		}
		
		if (activeChar.isRentedPet()) {
			activeChar.stopRentPet();
		} else if (activeChar.isMounted()) {
			activeChar.dismount();
		}
		return true;
	}
	
	@Override
	public int[] getUserCommandList() {
		return COMMAND_IDS;
	}
}
