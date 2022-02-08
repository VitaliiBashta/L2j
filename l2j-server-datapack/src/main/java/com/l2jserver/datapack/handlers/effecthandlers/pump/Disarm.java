
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.EffectFlag;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Disarm effect implementation.
 * @author nBd
 */
public final class Disarm extends AbstractEffect {
	public Disarm(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean canStart(BuffInfo info) {
		return info.getEffected().isPlayer();
	}
	
	@Override
	public int getEffectFlags() {
		return EffectFlag.DISARMED.getMask();
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().getActingPlayer().disarmWeapons();
	}
}
