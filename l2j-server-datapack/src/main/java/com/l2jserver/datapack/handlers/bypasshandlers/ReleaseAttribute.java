
package com.l2jserver.datapack.handlers.bypasshandlers;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExShowBaseAttributeCancelWindow;

public class ReleaseAttribute implements IBypassHandler {
	private static final String[] COMMANDS = {
		"ReleaseAttribute"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		if (!target.isNpc()) {
			return false;
		}
		
		activeChar.sendPacket(new ExShowBaseAttributeCancelWindow(activeChar));
		return true;
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
