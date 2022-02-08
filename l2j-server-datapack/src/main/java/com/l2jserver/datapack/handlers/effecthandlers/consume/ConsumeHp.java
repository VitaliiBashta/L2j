
package com.l2jserver.datapack.handlers.effecthandlers.consume;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Consume Hp effect implementation.
 * @author Adry_85
 * @since 2.6.0.0
 */
public final class ConsumeHp extends AbstractEffect {
	private final double _power;
	
	public ConsumeHp(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
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
		double hp = target.getCurrentHp();
		if ((consume < 0) && ((hp + consume) <= 0)) {
			target.sendPacket(SystemMessageId.SKILL_REMOVED_DUE_LACK_HP);
			return false;
		}
		
		target.setCurrentHp(Math.min(hp + consume, target.getMaxRecoverableHp()));
		return true;
	}
}
