
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Give SP effect implementation.
 * @author Adry_85
 */
public final class GiveSp extends AbstractEffect {
	private final int _sp;
	
	public GiveSp(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_sp = params.getInt("sp", 0);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if ((info.getEffector() == null) || (info.getEffected() == null) || !info.getEffector().isPlayer() || !info.getEffected().isPlayer() || info.getEffected().isAlikeDead()) {
			return;
		}
		
		info.getEffector().getActingPlayer().addExpAndSp(0, _sp);
	}
}