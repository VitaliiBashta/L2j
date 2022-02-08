
package com.l2jserver.datapack.handlers.itemhandlers;

import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.instancemanager.TerritoryWarManager;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * This class provides handling for items that should display a map when double clicked.
 */
public class Disguise implements IItemHandler {
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
		if (!playable.isPlayer()) {
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		
		L2PcInstance activeChar = playable.getActingPlayer();
		
		int regId = TerritoryWarManager.getInstance().getRegisteredTerritoryId(activeChar);
		if ((regId > 0) && (regId == (item.getId() - 13596))) {
			if ((activeChar.getClan() != null) && (activeChar.getClan().getCastleId() > 0)) {
				activeChar.sendPacket(SystemMessageId.TERRITORY_OWNING_CLAN_CANNOT_USE_DISGUISE_SCROLL);
				return false;
			}
			TerritoryWarManager.getInstance().addDisguisedPlayer(activeChar.getObjectId());
			activeChar.broadcastUserInfo();
			playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
			return true;
		} else if (regId > 0) {
			activeChar.sendPacket(SystemMessageId.THE_DISGUISE_SCROLL_MEANT_FOR_DIFFERENT_TERRITORY);
			return false;
		} else {
			activeChar.sendPacket(SystemMessageId.TERRITORY_WAR_SCROLL_CAN_NOT_USED_NOW);
			return false;
		}
	}
}
