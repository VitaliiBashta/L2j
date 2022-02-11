
package com.l2jserver.datapack.handlers.communityboard;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IParseBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Service
public class HomeBoard implements IParseBoardHandler {
	// SQL Queries
	private static final String COUNT_FAVORITES = "SELECT COUNT(*) AS favorites FROM `bbs_favorites` WHERE `playerId`=?";
	
	private static final String[] COMMANDS = {
		"_bbshome",
		"_bbstop"
	};
	
	@Override
	public String[] getCommunityBoardCommands() {
		return COMMANDS;
	}
	
	@Override
	public boolean parseCommunityBoardCommand(String command, L2PcInstance activeChar) {
		if (command.equals("_bbshome") || command.equals("_bbstop")) {
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Home", command);
			
			String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/home.html");
			html = html.replaceAll("%fav_count%", Integer.toString(getFavoriteCount(activeChar)));
			html = html.replaceAll("%region_count%", Integer.toString(getRegionCount(activeChar)));
			html = html.replaceAll("%clan_count%", Integer.toString(ClanTable.getInstance().getClanCount()));
			CommunityBoardHandler.separateAndSend(html, activeChar);
		} else if (command.startsWith("_bbstop;")) {
			final String path = command.replace("_bbstop;", "");
			if ((path.length() > 0) && path.endsWith(".html")) {
				final String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/" + path);
				CommunityBoardHandler.separateAndSend(html, activeChar);
			}
		}
		return true;
	}
	
	/**
	 * Gets the Favorite links for the given player.
	 * @param player the player
	 * @return the favorite links count
	 */
	private static int getFavoriteCount(L2PcInstance player) {
		int count = 0;
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(COUNT_FAVORITES)) {
			ps.setInt(1, player.getObjectId());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					count = rs.getInt("favorites");
				}
			}
		} catch (Exception e) {
			LOG.warning(FavoriteBoard.class.getSimpleName() + ": Coudn't load favorites count for player " + player.getName());
		}
		return count;
	}
	
	/**
	 * Gets the registered regions count for the given player.
	 * @param player the player
	 * @return the registered regions count
	 */
	private static int getRegionCount(L2PcInstance player) {
		return 0; // TODO: Implement.
	}
}
