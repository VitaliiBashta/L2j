
package com.l2jserver.datapack.handlers.effecthandlers.consume;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Consume Mp effect implementation.
 * @author Adry_85
 * @since 2.6.0.0
 */
public final class ConsumeMp extends AbstractEffect {
	private final double _power;
	
	public ConsumeMp(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getDouble("power", 0);
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public boolean onActionTime(BuffInfo info) {
		if (info.getEffected().isDead()) {
			return false;
		}
		
		final L2Character target = info.getEffected();
		final double consume = _power * getTicksMultiplier();
		double mp = target.getCurrentMp();
		if ((consume < 0) && ((mp + consume) <= 0)) {
			target.sendPacket(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
			return false;
		}
		
		target.setCurrentMp(Math.min(mp + consume, target.getMaxRecoverableMp()));
		return true;
	}
}
