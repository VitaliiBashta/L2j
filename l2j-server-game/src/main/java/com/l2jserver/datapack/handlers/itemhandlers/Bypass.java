
package com.l2jserver.datapack.handlers.itemhandlers;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author JIV
 */
public class Bypass implements IItemHandler {
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
		if (!(playable instanceof L2PcInstance)) {
			return false;
		}
		L2PcInstance activeChar = (L2PcInstance) playable;
		final int itemId = item.getId();
		
		String filename = "data/html/item/" + itemId + ".htm";
		String content = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), filename);
		final NpcHtmlMessage html = new NpcHtmlMessage(0, item.getId());
		if (content == null) {
			html.setHtml("<html><body>My Text is missing:<br>" + filename + "</body></html>");
			activeChar.sendPacket(html);
		} else {
			html.setHtml(content);
			html.replace("%itemId%", String.valueOf(item.getObjectId()));
			activeChar.sendPacket(html);
		}
		return true;
	}
	
}
