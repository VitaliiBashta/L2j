
package com.l2jserver.datapack.handlers.bypasshandlers;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import org.springframework.stereotype.Service;

@Service
public class Link implements IBypassHandler {
	private static final String[] COMMANDS = {
		"Link"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		String htmlPath = command.substring(4).trim();
		if (htmlPath.isEmpty()) {
			_log.warning("Player " + activeChar.getName() + " sent empty link html!");
			return false;
		}
		
		if (htmlPath.contains("..")) {
			_log.warning("Player " + activeChar.getName() + " sent invalid link html: " + htmlPath);
			return false;
		}
		
		String filename = "data/html/" + htmlPath;
		final NpcHtmlMessage html = new NpcHtmlMessage(target != null ? target.getObjectId() : 0);
		html.setFile(activeChar.getHtmlPrefix(), filename);
		html.replace("%objectId%", String.valueOf(target != null ? target.getObjectId() : 0));
		activeChar.sendPacket(html);
		return true;
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
