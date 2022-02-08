
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.stats.Formulas;

/**
 * Target Cancel effect implementation.
 * @author -Nemesiss-, Adry_85
 */
public final class TargetCancel extends AbstractEffect {
	private final int _chance;
	
	public TargetCancel(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_chance = params.getInt("chance", 100);
	}
	
	@Override
	public boolean calcSuccess(BuffInfo info) {
		return Formulas.calcProbability(_chance, info.getEffector(), info.getEffected(), info.getSkill());
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().setTarget(null);
		info.getEffected().abortAttack();
		info.getEffected().abortCast();
		info.getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, info.getEffector());
	}
}
