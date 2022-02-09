
package com.l2jserver.datapack.handlers.bypasshandlers;

import java.util.logging.Level;

import com.l2jserver.gameserver.data.xml.impl.MultisellData;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class Multisell implements IBypassHandler {
	private static final String[] COMMANDS = {
		"multisell",
		"exc_multisell"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		if (!target.isNpc()) {
			return false;
		}
		
		try {
			int listId;
			if (command.toLowerCase().startsWith(COMMANDS[0])) // multisell
			{
				listId = Integer.parseInt(command.substring(9).trim());
				MultisellData.getInstance().separateAndSend(listId, activeChar, (L2Npc) target, false);
				return true;
			} else if (command.toLowerCase().startsWith(COMMANDS[1])) // exc_multisell
			{
				listId = Integer.parseInt(command.substring(13).trim());
				MultisellData.getInstance().separateAndSend(listId, activeChar, (L2Npc) target, true);
				return true;
			}
			return false;
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
