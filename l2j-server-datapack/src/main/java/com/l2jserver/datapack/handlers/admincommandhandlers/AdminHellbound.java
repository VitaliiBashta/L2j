
package com.l2jserver.datapack.handlers.admincommandhandlers;

import java.util.StringTokenizer;

import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * Hellbound admin command.
 * @author DS, Gladicek
 */
public class AdminHellbound implements IAdminCommandHandler {
	private static final String[] ADMIN_COMMANDS = {
		"admin_hellbound_setlevel",
		"admin_hellbound"
	};
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		if (activeChar == null) {
			return false;
		}
		
		if (command.startsWith(ADMIN_COMMANDS[0])) {
			try {
				StringTokenizer st = new StringTokenizer(command, " ");
				st.nextToken();
				final int level = Integer.parseInt(st.nextToken());
				if ((level < 0) || (level > 11)) {
					throw new NumberFormatException();
				}
				
				HellboundEngine.getInstance().setLevel(level);
				activeChar.sendMessage("Hellbound level set to " + level);
				return true;
			} catch (Exception e) {
				activeChar.sendMessage("Usage: //hellbound_setlevel 0-11");
				return false;
			}
		} else if (command.startsWith(ADMIN_COMMANDS[1])) {
			showMenu(activeChar);
			return true;
		}
		return false;
	}
	
	private void showMenu(L2PcInstance activeChar) {
		final NpcHtmlMessage html = new NpcHtmlMessage();
		html.setFile(activeChar.getHtmlPrefix(), "data/html/admin/hellbound.htm");
		html.replace("%hbstage%", String.valueOf(HellboundEngine.getInstance().getLevel()));
		html.replace("%trust%", String.valueOf(HellboundEngine.getInstance().getTrust()));
		html.replace("%maxtrust%", String.valueOf(HellboundEngine.getInstance().getMaxTrust()));
		html.replace("%mintrust%", String.valueOf(HellboundEngine.getInstance().getMinTrust()));
		activeChar.sendPacket(html);
	}
}
