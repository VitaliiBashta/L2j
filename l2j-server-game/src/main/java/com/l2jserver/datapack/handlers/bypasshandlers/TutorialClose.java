
package com.l2jserver.datapack.handlers.bypasshandlers;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.TutorialCloseHtml;

/**
 * @author UnAfraid
 */
public class TutorialClose implements IBypassHandler {
	private static final String[] COMMANDS = {
		"tutorial_close",
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		return false;
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
