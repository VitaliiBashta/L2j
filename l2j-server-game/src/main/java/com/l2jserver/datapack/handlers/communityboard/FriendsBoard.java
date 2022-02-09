
package com.l2jserver.datapack.handlers.communityboard;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IParseBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Friends board.
 * @author Zoey76
 */
public class FriendsBoard implements IParseBoardHandler {
	private static final String[] COMMANDS = {
		"_friendlist",
		"_friendblocklist"
	};
	
	@Override
	public String[] getCommunityBoardCommands() {
		return COMMANDS;
	}
	
	@Override
	public boolean parseCommunityBoardCommand(String command, L2PcInstance activeChar) {
		if (command.equals("_friendlist")) {
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Friends List", command);
			
			final String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/friends_list.html");
			
			CommunityBoardHandler.separateAndSend(html, activeChar);
		} else if (command.equals("_friendblocklist")) {
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Ignore list", command);
			
			final String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/friends_block_list.html");
			
			CommunityBoardHandler.separateAndSend(html, activeChar);
		} else {
			
		}
		return true;
	}
}
