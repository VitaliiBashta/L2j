
package com.l2jserver.datapack.handlers.admincommandhandlers;

import static com.l2jserver.gameserver.config.Configuration.server;

import java.io.File;
import java.util.StringTokenizer;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author NosBit
 */
public class AdminHtml implements IAdminCommandHandler {
	private static final String[] ADMIN_COMMANDS = {
		"admin_html",
		"admin_loadhtml"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		switch (actualCommand.toLowerCase()) {
			case "admin_html": {
				if (!st.hasMoreTokens()) {
					activeChar.sendMessage("Usage: //html path");
					return false;
				}
				
				final String path = st.nextToken();
				showAdminHtml(activeChar, path);
				break;
			}
			case "admin_loadhtml": {
				if (!st.hasMoreTokens()) {
					activeChar.sendMessage("Usage: //loadhtml path");
					return false;
				}
				
				final String path = st.nextToken();
				showHtml(activeChar, path, true);
				break;
			}
		}
		return true;
	}
	
	/**
	 * Shows a html message to activeChar
	 * @param activeChar activeChar where html is shown
	 * @param path relative path from directory data/html/admin/ to html
	 */
	public static void showAdminHtml(L2PcInstance activeChar, String path) {
		showHtml(activeChar, "data/html/admin/" + path, false);
	}
	
	/**
	 * Shows a html message to activeChar.
	 * @param activeChar activeChar where html message is shown.
	 * @param path relative path from Configuration.getInstance().server().getDatapackRoot() to html.
	 * @param reload {@code true} will reload html and show it {@code false} will show it from cache.
	 */
	public static void showHtml(L2PcInstance activeChar, String path, boolean reload) {
		String content = null;
		if (!reload) {
			content = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), path);
		} else {
			File file = new File(server().getDatapackRoot(), path);
			content = HtmCache.getInstance().loadFile(file);
		}
		final NpcHtmlMessage html = new NpcHtmlMessage();
		if (content != null) {
			html.setHtml(content);
		} else {
			html.setHtml("<html><body>My text is missing:<br>" + path + "</body></html>");
		}
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList() {
		
		return ADMIN_COMMANDS;
	}
	
}
