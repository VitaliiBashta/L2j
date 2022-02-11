
package com.l2jserver.datapack.handlers.chathandlers;

import com.l2jserver.gameserver.handler.IChatHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import org.springframework.stereotype.Service;

import static com.l2jserver.gameserver.config.Configuration.general;

@Service
public class ChatAlliance implements IChatHandler {
	private static final int[] COMMAND_IDS = {
		9
	};
	
	@Override
	public void handleChat(int type, L2PcInstance activeChar, String target, String text) {
		if (activeChar.getClan() != null) {
			if (activeChar.isChatBanned() && general().getBanChatChannels().contains(type)) {
				activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED);
				return;
			}
			
			CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
			activeChar.getClan().broadcastToOnlineAllyMembers(cs);
		}
	}
	
	@Override
	public int[] getChatTypeList() {
		return COMMAND_IDS;
	}
}