
package com.l2jserver.datapack.handlers.effecthandlers.custom;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Immobile Buff effect implementation.
 * @author mkizub
 */
public final class ImmobileBuff extends Buff {
	public ImmobileBuff(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.BUFF;
	}
	
	@Override
	public void onExit(BuffInfo info) {
		info.getEffected().setIsImmobilized(false);
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().setIsImmobilized(true);
	}
}
