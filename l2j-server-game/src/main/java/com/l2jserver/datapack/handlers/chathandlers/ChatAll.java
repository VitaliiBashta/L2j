
package com.l2jserver.datapack.handlers.chathandlers;

import com.l2jserver.gameserver.handler.IChatHandler;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.handler.VoicedCommandHandler;
import com.l2jserver.gameserver.model.BlockList;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import static com.l2jserver.gameserver.config.Configuration.general;

@Service
public class ChatAll implements IChatHandler {
	private static Logger _log = Logger.getLogger(ChatAll.class.getName());
	
	private static final int[] COMMAND_IDS = {
		0
	};
	
	@Override
	public void handleChat(int type, L2PcInstance activeChar, String params, String text) {
		boolean vcd_used = false;
		if (text.startsWith(".")) {
			StringTokenizer st = new StringTokenizer(text);
			IVoicedCommandHandler vch;
			String command = "";
			
			if (st.countTokens() > 1) {
				command = st.nextToken().substring(1);
				params = text.substring(command.length() + 2);
				vch = VoicedCommandHandler.getInstance().getHandler(command);
			} else {
				command = text.substring(1);
				if (general().debug()) {
					_log.info("Command: " + command);
				}
				vch = VoicedCommandHandler.getInstance().getHandler(command);
			}
			if (vch != null) {
				vch.useVoicedCommand(command, activeChar, params);
				vcd_used = true;
			} else {
				if (general().debug()) {
					_log.warning("No handler registered for bypass '" + command + "'");
				}
				vcd_used = false;
			}
		}
		if (!vcd_used) {
			if (activeChar.isChatBanned() && general().getBanChatChannels().contains(type)) {
				activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED);
				return;
			}
			
			/**
			 * Match the character "." literally (Exactly 1 time) Match any character that is NOT a . character. Between one and unlimited times as possible, giving back as needed (greedy)
			 */
			if (text.matches("\\.{1}[^\\.]+")) {
				activeChar.sendPacket(SystemMessageId.INCORRECT_SYNTAX);
			} else {
				CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getAppearance().getVisibleName(), text);
				Collection<L2PcInstance> plrs = activeChar.getKnownList().getKnownPlayers().values();
				for (L2PcInstance player : plrs) {
					if ((player != null) && activeChar.isInsideRadius(player, 1250, false, true) && !BlockList.isBlocked(player, activeChar)) {
						player.sendPacket(cs);
					}
				}
				
				activeChar.sendPacket(cs);
			}
		}
	}
	
	@Override
	public int[] getChatTypeList() {
		return COMMAND_IDS;
	}
}