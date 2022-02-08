
package com.l2jserver.datapack.handlers.communityboard;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IWriteBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Memo board.
 * @author Zoey76
 */
public class MemoBoard implements IWriteBoardHandler {
	private static final String[] COMMANDS = {
		"_bbsmemo",
		"_bbstopics"
	};
	
	@Override
	public String[] getCommunityBoardCommands() {
		return COMMANDS;
	}
	
	@Override
	public boolean parseCommunityBoardCommand(String command, L2PcInstance activeChar) {
		CommunityBoardHandler.getInstance().addBypass(activeChar, "Memo Command", command);
		
		final String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/memo.html");
		CommunityBoardHandler.separateAndSend(html, activeChar);
		return true;
	}
	
	@Override
	public boolean writeCommunityBoardCommand(L2PcInstance activeChar, String arg1, String arg2, String arg3, String arg4, String arg5) {
		// TODO: Implement.
		return false;
	}
}
