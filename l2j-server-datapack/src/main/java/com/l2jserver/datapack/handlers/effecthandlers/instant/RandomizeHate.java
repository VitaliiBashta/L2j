
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import java.util.List;
import java.util.stream.Collectors;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.stats.Formulas;

/**
 * Randomize Hate effect implementation.
 */
public final class RandomizeHate extends AbstractEffect {
	private final int _chance;
	
	public RandomizeHate(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
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
		if ((info.getEffected() == null) || (info.getEffected() == info.getEffector()) || !info.getEffected().isAttackable()) {
			return;
		}
		
		final L2Attackable effectedMob = (L2Attackable) info.getEffected();
		final List<L2Character> aggroList = effectedMob.getAggroList().keySet().stream().filter(c -> c != info.getEffector()).collect(Collectors.toList());
		if (aggroList.isEmpty()) {
			return;
		}
		
		// Choosing randomly a new target
		final L2Character target = aggroList.get(Rnd.get(aggroList.size()));
		final long hate = effectedMob.getHating(info.getEffector());
		effectedMob.stopHating(info.getEffector());
		effectedMob.addDamageHate(target, 0, hate);
	}
}