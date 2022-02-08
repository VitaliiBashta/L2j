
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.instancemanager.FortManager;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.entity.Fort;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Take Fort effect implementation.
 * @author Adry_85
 */
public final class TakeFort extends AbstractEffect {
	public TakeFort(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
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
		
		final Fort fort = FortManager.getInstance().getFort(info.getEffector().getActingPlayer());
		if (fort != null) {
			fort.endOfSiege(info.getEffector().getActingPlayer().getClan());
		}
	}
}
