
package com.l2jserver.datapack.handlers.voicedcommandhandlers;

import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.util.Util;

/**
 * @author Zoey76
 */
public class SetVCmd implements IVoicedCommandHandler {
	private static final String[] VOICED_COMMANDS = {
		"set name",
		"set home",
		"set group"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params) {
		if (command.equals("set")) {
			final L2Object target = activeChar.getTarget();
			if ((target == null) || !target.isPlayer()) {
				return false;
			}
			
			final L2PcInstance player = activeChar.getTarget().getActingPlayer();
			if ((activeChar.getClan() == null) || (player.getClan() == null) || (activeChar.getClan().getId() != player.getClan().getId())) {
				return false;
			}
			
			if (params.startsWith("privileges")) {
				final String val = params.substring(11);
				if (!Util.isDigit(val)) {
					return false;
				}
				
				final int n = Integer.parseInt(val);
				if ((activeChar.getClanPrivileges().getBitmask() <= n) || !activeChar.isClanLeader()) {
					return false;
				}
				
				player.getClanPrivileges().setBitmask(n);
				activeChar.sendMessage("Your clan privileges have been set to " + n + " by " + activeChar.getName() + ".");
			} else if (params.startsWith("title")) {
				// TODO why is this empty?
			}
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList() {
		return VOICED_COMMANDS;
	}
}
