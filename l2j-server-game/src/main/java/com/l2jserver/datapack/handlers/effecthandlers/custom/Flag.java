
package com.l2jserver.datapack.handlers.effecthandlers.custom;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Flag effect implementation.
 * @author BiggBoss
 */
public final class Flag extends AbstractEffect {
	public Flag(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean canStart(BuffInfo info) {
		return (info.getEffected() != null) && info.getEffected().isPlayer();
	}
	
	@Override
	public void onExit(BuffInfo info) {
		info.getEffected().getActingPlayer().updatePvPFlag(0);
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().updatePvPFlag(1);
	}
}
