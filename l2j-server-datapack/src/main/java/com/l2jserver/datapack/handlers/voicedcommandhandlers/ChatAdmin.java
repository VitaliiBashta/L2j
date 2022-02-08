
package com.l2jserver.datapack.handlers.voicedcommandhandlers;

import java.util.StringTokenizer;

import com.l2jserver.gameserver.data.sql.impl.CharNameTable;
import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.instancemanager.PunishmentManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.punishment.PunishmentAffect;
import com.l2jserver.gameserver.model.punishment.PunishmentTask;
import com.l2jserver.gameserver.model.punishment.PunishmentType;
import com.l2jserver.gameserver.util.Util;

public class ChatAdmin implements IVoicedCommandHandler {
	private static final String[] VOICED_COMMANDS = {
		"banchat",
		"unbanchat"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params) {
		if (!AdminData.getInstance().hasAccess(command, activeChar.getAccessLevel())) {
			return false;
		}
		
		if (command.equals(VOICED_COMMANDS[0])) // banchat
		{
			if (params == null) {
				activeChar.sendMessage("Usage: .banchat name [minutes]");
				return true;
			}
			StringTokenizer st = new StringTokenizer(params);
			if (st.hasMoreTokens()) {
				String name = st.nextToken();
				long expirationTime = 0;
				if (st.hasMoreTokens()) {
					String token = st.nextToken();
					if (Util.isDigit(token)) {
						expirationTime = System.currentTimeMillis() + (Integer.parseInt(st.nextToken()) * 60 * 1000);
					}
				}
				
				int objId = CharNameTable.getInstance().getIdByName(name);
				if (objId > 0) {
					L2PcInstance player = L2World.getInstance().getPlayer(objId);
					if ((player == null) || !player.isOnline()) {
						activeChar.sendMessage("Player not online !");
						return false;
					}
					if (player.isChatBanned()) {
						activeChar.sendMessage("Player is already punished !");
						return false;
					}
					if (player == activeChar) {
						activeChar.sendMessage("You can't ban yourself !");
						return false;
					}
					if (player.isGM()) {
						activeChar.sendMessage("You can't ban GM !");
						return false;
					}
					if (AdminData.getInstance().hasAccess(command, player.getAccessLevel())) {
						activeChar.sendMessage("You can't ban moderator !");
						return false;
					}
					
					PunishmentManager.getInstance().startPunishment(new PunishmentTask(objId, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN, expirationTime, "Chat banned by moderator", activeChar.getName()));
					player.sendMessage("Chat banned by moderator " + activeChar.getName());
					
					if (expirationTime > 0) {
						activeChar.sendMessage("Player " + player.getName() + " chat banned for " + expirationTime + " minutes.");
					} else {
						activeChar.sendMessage("Player " + player.getName() + " chat banned forever.");
					}
				} else {
					activeChar.sendMessage("Player not found !");
					return false;
				}
			}
		} else if (command.equals(VOICED_COMMANDS[1])) // unbanchat
		{
			if (params == null) {
				activeChar.sendMessage("Usage: .unbanchat name");
				return true;
			}
			StringTokenizer st = new StringTokenizer(params);
			if (st.hasMoreTokens()) {
				String name = st.nextToken();
				
				int objId = CharNameTable.getInstance().getIdByName(name);
				if (objId > 0) {
					L2PcInstance player = L2World.getInstance().getPlayer(objId);
					if ((player == null) || !player.isOnline()) {
						activeChar.sendMessage("Player not online !");
						return false;
					}
					if (!player.isChatBanned()) {
						activeChar.sendMessage("Player is not chat banned !");
						return false;
					}
					
					PunishmentManager.getInstance().stopPunishment(objId, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN);
					
					activeChar.sendMessage("Player " + player.getName() + " chat unbanned.");
					player.sendMessage("Chat unbanned by moderator " + activeChar.getName());
				} else {
					activeChar.sendMessage("Player not found !");
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList() {
		return VOICED_COMMANDS;
	}
}
