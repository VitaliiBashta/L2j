
package com.l2jserver.datapack.handlers.effecthandlers.custom;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.EffectFlag;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Stun effect implementation.
 * @author mkizub
 */
public final class Stun extends AbstractEffect {
	public Stun(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public int getEffectFlags() {
		return EffectFlag.STUNNED.getMask();
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.STUN;
	}
	
	@Override
	public void onExit(BuffInfo info) {
		info.getEffected().stopStunning(false);
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().startStunning();
	}
}
