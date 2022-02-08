
package com.l2jserver.datapack.handlers.chathandlers;

import static com.l2jserver.gameserver.config.Configuration.general;

import com.l2jserver.gameserver.handler.IChatHandler;
import com.l2jserver.gameserver.instancemanager.MapRegionManager;
import com.l2jserver.gameserver.model.BlockList;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;

/**
 * Trade chat handler.
 * @author durgus
 */
public class ChatTrade implements IChatHandler {
	private static final int[] COMMAND_IDS = {
		8
	};
	
	/**
	 * Handle chat type 'trade'
	 */
	@Override
	public void handleChat(int type, L2PcInstance activeChar, String target, String text) {
		if (activeChar.isChatBanned() && general().getBanChatChannels().contains(type)) {
			activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED);
			return;
		}
		
		final CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
		if (general().getTradeChat().equalsIgnoreCase("on") || (general().getTradeChat().equalsIgnoreCase("gm") && activeChar.canOverrideCond(PcCondOverride.CHAT_CONDITIONS))) {
			int region = MapRegionManager.getInstance().getMapRegionLocId(activeChar);
			for (L2PcInstance player : L2World.getInstance().getPlayers()) {
				if ((region == MapRegionManager.getInstance().getMapRegionLocId(player)) && !BlockList.isBlocked(player, activeChar) && (player.getInstanceId() == activeChar.getInstanceId())) {
					player.sendPacket(cs);
				}
			}
		} else if (general().getTradeChat().equalsIgnoreCase("global")) {
			if (!activeChar.canOverrideCond(PcCondOverride.CHAT_CONDITIONS) && !activeChar.getFloodProtectors().getGlobalChat().tryPerformAction("global chat")) {
				activeChar.sendMessage("Do not spam trade channel.");
				return;
			}
			
			for (L2PcInstance player : L2World.getInstance().getPlayers()) {
				if (!BlockList.isBlocked(player, activeChar)) {
					player.sendPacket(cs);
				}
			}
		}
	}
	
	/**
	 * Returns the chat types registered to this handler.
	 */
	@Override
	public int[] getChatTypeList() {
		return COMMAND_IDS;
	}
}