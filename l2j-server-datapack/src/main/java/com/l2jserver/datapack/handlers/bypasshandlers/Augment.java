
package com.l2jserver.datapack.handlers.bypasshandlers;

import java.util.logging.Level;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import com.l2jserver.gameserver.network.serverpackets.ExShowVariationMakeWindow;

public class Augment implements IBypassHandler {
	private static final String[] COMMANDS = {
		"Augment"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		if (!target.isNpc()) {
			return false;
		}
		
		try {
			switch (Integer.parseInt(command.substring(8, 9).trim())) {
				case 1:
					activeChar.sendPacket(ExShowVariationMakeWindow.STATIC_PACKET);
					return true;
				case 2:
					activeChar.sendPacket(ExShowVariationCancelWindow.STATIC_PACKET);
					return true;
			}
		} catch (Exception e) {
			_log.log(Level.WARNING, "Exception in " + getClass().getSimpleName(), e);
		}
		return false;
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
