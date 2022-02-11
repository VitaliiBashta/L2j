
package com.l2jserver.datapack.handlers.itemhandlers;

import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.EtcStatusUpdate;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import org.springframework.stereotype.Service;

@Service
public class CharmOfCourage implements IItemHandler {
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
		
		if (!playable.isPlayer()) {
			return false;
		}
		
		final L2PcInstance activeChar = playable.getActingPlayer();
		
		int level = activeChar.getLevel();
		final int itemLevel = item.getItem().getItemGrade().getId();
		
		if (level < 20) {
			level = 0;
		} else if (level < 40) {
			level = 1;
		} else if (level < 52) {
			level = 2;
		} else if (level < 61) {
			level = 3;
		} else if (level < 76) {
			level = 4;
		} else {
			level = 5;
		}
		
		if (itemLevel < level) {
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED);
			sm.addItemName(item.getId());
			activeChar.sendPacket(sm);
			return false;
		}
		
		if (activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), 1, null, false)) {
			activeChar.setCharmOfCourage(true);
			activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			return true;
		}
		return false;
	}
}