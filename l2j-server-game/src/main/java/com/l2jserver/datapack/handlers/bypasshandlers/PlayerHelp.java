
package com.l2jserver.datapack.handlers.bypasshandlers;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import org.springframework.stereotype.Service;

import java.util.StringTokenizer;
import java.util.logging.Level;

@Service
public class PlayerHelp implements IBypassHandler {
	private static final String[] COMMANDS = {
		"player_help"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		try {
			if (command.length() < 13) {
				return false;
			}
			
			final String path = command.substring(12);
			if (path.indexOf("..") != -1) {
				return false;
			}
			
			final StringTokenizer st = new StringTokenizer(path);
			final String[] cmd = st.nextToken().split("#");
			
			final NpcHtmlMessage html;
			if (cmd.length > 1) {
				final int itemId = Integer.parseInt(cmd[1]);
				html = new NpcHtmlMessage(0, itemId);
			} else {
				html = new NpcHtmlMessage();
			}
			
			html.setFile(activeChar.getHtmlPrefix(), "data/html/help/" + cmd[0]);
			activeChar.sendPacket(html);
		} catch (Exception e) {
			_log.log(Level.WARNING, "Exception in " + getClass().getSimpleName(), e);
		}
		return true;
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
