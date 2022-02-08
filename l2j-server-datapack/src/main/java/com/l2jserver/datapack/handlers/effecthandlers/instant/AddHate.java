
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Add Hate effect implementation.
 * @author Adry_85
 */
public final class AddHate extends AbstractEffect {
	private final double _power;
	
	public AddHate(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getDouble("power", 0);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (!info.getEffected().isAttackable()) {
			return;
		}
		
		final double val = _power;
		if (val > 0) {
			((L2Attackable) info.getEffected()).addDamageHate(info.getEffector(), 0, (int) val);
		} else if (val < 0) {
			((L2Attackable) info.getEffected()).reduceHate(info.getEffector(), (int) -val);
		}
	}
}
