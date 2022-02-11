
package com.l2jserver.datapack.handlers.voicedcommandhandlers;

import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class Debug implements IVoicedCommandHandler {
	private static final String[] VOICED_COMMANDS = {
		"debug"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params) {
		if (!AdminData.getInstance().hasAccess(command, activeChar.getAccessLevel())) {
			return false;
		}
		
		if (VOICED_COMMANDS[0].equalsIgnoreCase(command)) {
			if (activeChar.isDebug()) {
				activeChar.setDebug(null);
				activeChar.sendMessage("Debugging disabled.");
			} else {
				activeChar.setDebug(activeChar);
				activeChar.sendMessage("Debugging enabled.");
			}
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList() {
		return VOICED_COMMANDS;
	}
}