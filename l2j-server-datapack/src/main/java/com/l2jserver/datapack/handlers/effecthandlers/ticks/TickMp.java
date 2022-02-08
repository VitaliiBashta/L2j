
package com.l2jserver.datapack.handlers.effecthandlers.ticks;

import com.l2jserver.gameserver.enums.EffectCalculationType;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Tick Mp effect implementation.
 * @author Adry_85
 * @since 2.6.0.0
 */
public final class TickMp extends AbstractEffect {
	private final double _power;
	private final EffectCalculationType _mode;
	
	public TickMp(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getDouble("power", 0);
		_mode = params.getEnum("mode", EffectCalculationType.class, EffectCalculationType.DIFF);
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public boolean onActionTime(BuffInfo info) {
		if (info.getEffected().isDead()) {
			return false;
		}
		
		final L2Character target = info.getEffected();
		double power = 0;
		double mp = target.getCurrentMp();
		switch (_mode) {
			case DIFF: {
				power = _power * getTicksMultiplier();
				break;
			}
			case PER: {
				power = mp * _power * getTicksMultiplier();
				break;
			}
		}
		
		if (power < 0) {
			target.reduceCurrentMp(Math.abs(power));
		} else {
			double maxMp = target.getMaxRecoverableMp();
			
			// Not needed to set the MP and send update packet if player is already at max MP
			if (mp >= maxMp) {
				return true;
			}
			
			target.setCurrentMp(Math.min(mp + power, maxMp));
		}
		return false;
	}
}
