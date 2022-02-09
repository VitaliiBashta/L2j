
package com.l2jserver.datapack.handlers.effecthandlers.custom;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;

/**
 * Debuff effect implementation.
 */
public final class Debuff extends AbstractEffect {
	public Debuff(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.DEBUFF;
	}
}
