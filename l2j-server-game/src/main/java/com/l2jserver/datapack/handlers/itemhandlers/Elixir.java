
package com.l2jserver.datapack.handlers.itemhandlers;

import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import org.springframework.stereotype.Service;

@Service
public class Elixir extends ItemSkills {
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
		if (!playable.isPlayer()) {
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		return super.useItem(playable, item, forceUse);
	}
}
