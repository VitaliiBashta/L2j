
package com.l2jserver.datapack.handlers.voicedcommandhandlers;

import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class Hellbound implements IVoicedCommandHandler {
	private static final String[] VOICED_COMMANDS = {
		"hellbound"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params) {
		if (HellboundEngine.getInstance().isLocked()) {
			activeChar.sendMessage("Hellbound is currently locked.");
			return true;
		}
		
		final int maxTrust = HellboundEngine.getInstance().getMaxTrust();
		activeChar.sendMessage("Hellbound level: " + HellboundEngine.getInstance().getLevel() + " trust: " + HellboundEngine.getInstance().getTrust() + (maxTrust > 0 ? "/" + maxTrust : ""));
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList() {
		return VOICED_COMMANDS;
	}
}
