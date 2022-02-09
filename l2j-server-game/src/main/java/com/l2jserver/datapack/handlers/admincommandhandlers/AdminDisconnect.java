
package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

/**
 * This class handles following admin commands: - character_disconnect = disconnects target player
 * @version $Revision: 1.2.4.4 $ $Date: 2005/04/11 10:06:00 $
 */
@Service
public class AdminDisconnect implements IAdminCommandHandler {
	private static final String[] ADMIN_COMMANDS = {
		"admin_character_disconnect"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		if (command.equals("admin_character_disconnect")) {
			disconnectCharacter(activeChar);
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
	
	private void disconnectCharacter(L2PcInstance activeChar) {
		L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance) {
			player = (L2PcInstance) target;
		} else {
			return;
		}
		
		if (player == activeChar) {
			activeChar.sendMessage("You cannot logout your own character.");
		} else {
			activeChar.sendMessage("Character " + player.getName() + " disconnected from server.");
			
			player.logout();
		}
	}
}
