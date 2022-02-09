
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Take Castle effect implementation.
 * @author Adry_85
 */
public final class TakeCastle extends AbstractEffect {
	public TakeCastle(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
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
		Castle castle = CastleManager.getInstance().getCastle(info.getEffector());
		castle.engrave(info.getEffector().getActingPlayer().getClan(), info.getEffected());
	}
	
}
