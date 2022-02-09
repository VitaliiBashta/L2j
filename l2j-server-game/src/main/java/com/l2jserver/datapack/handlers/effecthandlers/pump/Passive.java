
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Passive effect implementation.
 * @author Adry_85
 */
public final class Passive extends AbstractEffect {
	public Passive(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public void onExit(BuffInfo info) {
		info.getEffected().enableAllSkills();
		info.getEffected().setIsImmobilized(false);
	}
	
	@Override
	public boolean canStart(BuffInfo info) {
		return info.getEffected().isAttackable();
	}
	
	@Override
	public void onStart(BuffInfo info) {
		L2Attackable target = (L2Attackable) info.getEffected();
		target.abortAttack();
		target.abortCast();
		target.disableAllSkills();
		target.setIsImmobilized(true);
	}
}
