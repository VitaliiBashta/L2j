
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.RecipeController;
import com.l2jserver.gameserver.enums.PrivateStoreType;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Open Common Recipe Book effect implementation.
 * @author Adry_85
 */
public final class OpenCommonRecipeBook extends AbstractEffect {
	public OpenCommonRecipeBook(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (!info.getEffector().isPlayer()) {
			return;
		}
		
		L2PcInstance player = info.getEffector().getActingPlayer();
		if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
			player.sendPacket(SystemMessageId.CANNOT_CREATED_WHILE_ENGAGED_IN_TRADING);
			return;
		}
		
		RecipeController.getInstance().requestBookOpen(player, false);
	}
}
