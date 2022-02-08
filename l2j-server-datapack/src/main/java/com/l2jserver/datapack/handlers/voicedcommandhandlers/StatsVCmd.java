
package com.l2jserver.datapack.handlers.voicedcommandhandlers;

import static com.l2jserver.gameserver.network.SystemMessageId.S1_OFFLINE;
import static com.l2jserver.gameserver.network.SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME;

import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.L2Event;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.StringUtil;

/**
 * @author Zoey76.
 */
public class StatsVCmd implements IVoicedCommandHandler {
	private static final String[] VOICED_COMMANDS = {
		"stats"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params) {
		if (!command.equals("stats") || (params == null) || params.isEmpty()) {
			activeChar.sendMessage("Usage: .stats <player name>");
			return false;
		}
		
		final L2PcInstance pc = L2World.getInstance().getPlayer(params);
		if ((pc == null)) {
			activeChar.sendPacket(TARGET_IS_NOT_FOUND_IN_THE_GAME);
			return false;
		}
		
		if (pc.getClient().isDetached()) {
			final SystemMessage sm = SystemMessage.getSystemMessage(S1_OFFLINE);
			sm.addPcName(pc);
			activeChar.sendPacket(sm);
			return false;
		}
		
		if (!L2Event.isParticipant(pc) || (pc.getEventStatus() == null)) {
			activeChar.sendMessage("That player is not an event participant.");
			return false;
		}
		
		final StringBuilder replyMSG = StringUtil.startAppend(300 + (pc.getEventStatus().getKills().size() * 50), "<html><body>"
			+ "<center><font color=\"LEVEL\">[ L2J EVENT ENGINE ]</font></center><br><br>Statistics for player <font color=\"LEVEL\">", pc.getName(), "</font><br>Total kills <font color=\"FF0000\">", String.valueOf(pc.getEventStatus().getKills().size()), "</font><br><br>Detailed list: <br>");
		for (L2PcInstance plr : pc.getEventStatus().getKills()) {
			StringUtil.append(replyMSG, "<font color=\"FF0000\">", plr.getName(), "</font><br>");
		}
		replyMSG.append("</body></html>");
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList() {
		return VOICED_COMMANDS;
	}
}
