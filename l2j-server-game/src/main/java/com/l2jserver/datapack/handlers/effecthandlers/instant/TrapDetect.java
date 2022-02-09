
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2TrapInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Trap Detect effect implementation.
 * @author UnAfraid
 */
public final class TrapDetect extends AbstractEffect {
	private final int _power;
	
	public TrapDetect(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		if (params.isEmpty()) {
			throw new IllegalArgumentException(getClass().getSimpleName() + ": effect without power!");
		}
		
		_power = params.getInt("power");
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (!info.getEffected().isTrap() || info.getEffected().isAlikeDead()) {
			return;
		}
		
		final L2TrapInstance trap = (L2TrapInstance) info.getEffected();
		if (trap.getLevel() <= _power) {
			trap.setDetected(info.getEffector());
		}
	}
}
