
package com.l2jserver.datapack.handlers.bypasshandlers;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MerchantInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import org.springframework.stereotype.Service;

@Service
public class BuyShadowItem implements IBypassHandler {
	private static final String[] COMMANDS = {
		"BuyShadowItem"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		if (!(target instanceof L2MerchantInstance)) {
			return false;
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(((L2Npc) target).getObjectId());
		if (activeChar.getLevel() < 40) {
			html.setFile(activeChar.getHtmlPrefix(), "data/html/common/shadow_item-lowlevel.htm");
		} else if ((activeChar.getLevel() >= 40) && (activeChar.getLevel() < 46)) {
			html.setFile(activeChar.getHtmlPrefix(), "data/html/common/shadow_item_d.htm");
		} else if ((activeChar.getLevel() >= 46) && (activeChar.getLevel() < 52)) {
			html.setFile(activeChar.getHtmlPrefix(), "data/html/common/shadow_item_c.htm");
		} else if (activeChar.getLevel() >= 52) {
			html.setFile(activeChar.getHtmlPrefix(), "data/html/common/shadow_item_b.htm");
		}
		html.replace("%objectId%", String.valueOf(((L2Npc) target).getObjectId()));
		activeChar.sendPacket(html);
		
		return true;
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
