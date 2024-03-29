
package com.l2jserver.datapack.handlers.communityboard;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IWriteBoardHandler;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.util.Util;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionBoard implements IWriteBoardHandler {
	// Region data
	// @formatter:off
	private static final int[] REGIONS = { 1049, 1052, 1053, 1057, 1060, 1059, 1248, 1247, 1056 };
	// @formatter:on
	private static final String[] COMMANDS = {
		"_bbsloc"
	};
	
	@Override
	public String[] getCommunityBoardCommands() {
		return COMMANDS;
	}
	
	@Override
	public boolean parseCommunityBoardCommand(String command, L2PcInstance activeChar) {
		if (command.equals("_bbsloc")) {
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Region", command);
			
			final String list = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/region_list.html");
			final StringBuilder sb = new StringBuilder();
			final List<Castle> castles = CastleManager.getInstance().getCastles();
			for (int i = 0; i < REGIONS.length; i++) {
				final Castle castle = castles.get(i);
				final L2Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
				String link = list.replaceAll("%region_id%", String.valueOf(i));
				link = link.replace("%region_name%", String.valueOf(REGIONS[i]));
				link = link.replace("%region_owning_clan%", (clan != null ? clan.getName() : "NPC"));
				link = link.replace("%region_owning_clan_alliance%", ((clan != null) && (clan.getAllyName() != null) ? clan.getAllyName() : ""));
				link = link.replace("%region_tax_rate%", String.valueOf(castle.getTaxRate() * 100) + "%");
				sb.append(link);
			}
			
			String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/region.html");
			html = html.replace("%region_list%", sb.toString());
			CommunityBoardHandler.separateAndSend(html, activeChar);
		} else if (command.startsWith("_bbsloc;")) {
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Region>", command);
			
			final String id = command.replace("_bbsloc;", "");
			if (!Util.isDigit(id)) {
				LOG.warning(RegionBoard.class.getSimpleName() + ": Player " + activeChar + " sent and invalid region bypass " + command + "!");
				return false;
			}
			
			// TODO: Implement.
		}
		return true;
	}
	
	@Override
	public boolean writeCommunityBoardCommand(L2PcInstance activeChar, String arg1, String arg2, String arg3, String arg4, String arg5) {
		// TODO: Implement.
		return false;
	}
}
