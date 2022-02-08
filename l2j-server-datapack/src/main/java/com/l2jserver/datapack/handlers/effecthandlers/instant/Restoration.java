
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.PetItemList;

/**
 * Restoration effect implementation.
 * @author Zoey76
 */
public final class Restoration extends AbstractEffect {
	private final int _itemId;
	private final int _itemCount;
	
	public Restoration(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_itemId = params.getInt("itemId", 0);
		_itemCount = params.getInt("itemCount", 0);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if ((info.getEffected() == null) || !info.getEffected().isPlayable()) {
			return;
		}
		
		if ((_itemId <= 0) || (_itemCount <= 0)) {
			info.getEffected().sendPacket(SystemMessageId.NOTHING_INSIDE_THAT);
			_log.warning(Restoration.class.getSimpleName() + " effect with wrong item Id/count: " + _itemId + "/" + _itemCount + "!");
			return;
		}
		
		if (info.getEffected().isPlayer()) {
			info.getEffected().getActingPlayer().addItem("Skill", _itemId, _itemCount, info.getEffector(), true);
		} else if (info.getEffected().isPet()) {
			info.getEffected().getInventory().addItem("Skill", _itemId, _itemCount, info.getEffected().getActingPlayer(), info.getEffector());
			info.getEffected().getActingPlayer().sendPacket(new PetItemList(info.getEffected().getInventory().getItems()));
		}
	}
}
