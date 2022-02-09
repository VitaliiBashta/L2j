
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Focus Energy effect implementation.
 * @author DS
 */
public final class FocusEnergy extends AbstractEffect {
	private final int _charge;
	
	public FocusEnergy(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_charge = params.getInt("charge", 0);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (!info.getEffected().isPlayer()) {
			return;
		}
		
		info.getEffected().getActingPlayer().increaseCharges(1, _charge);
	}
}