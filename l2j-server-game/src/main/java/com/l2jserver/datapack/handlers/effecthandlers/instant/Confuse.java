
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import java.util.ArrayList;
import java.util.List;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.ai.CtrlEvent;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.EffectFlag;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.stats.Formulas;

/**
 * Confuse effect implementation.
 * @author littlecrow
 */
public final class Confuse extends AbstractEffect {
	private final int _chance;
	
	public Confuse(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_chance = params.getInt("chance", 100);
	}
	
	@Override
	public boolean calcSuccess(BuffInfo info) {
		return Formulas.calcProbability(_chance, info.getEffector(), info.getEffected(), info.getSkill());
	}
	
	@Override
	public int getEffectFlags() {
		return EffectFlag.CONFUSED.getMask();
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onExit(BuffInfo info) {
		if (!info.getEffected().isPlayer()) {
			info.getEffected().getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().getAI().notifyEvent(CtrlEvent.EVT_CONFUSED);
		
		final List<L2Character> targetList = new ArrayList<>();
		// Getting the possible targets
		for (L2Object obj : info.getEffected().getKnownList().getKnownObjects().values()) {
			if (((info.getEffected().isMonster() && obj.isAttackable()) || (obj instanceof L2Character)) && (obj != info.getEffected())) {
				targetList.add((L2Character) obj);
			}
		}
		
		// if there is no target, exit function
		if (!targetList.isEmpty()) {
			// Choosing randomly a new target
			final L2Character target = targetList.get(Rnd.nextInt(targetList.size()));
			// Attacking the target
			info.getEffected().setTarget(target);
			info.getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		}
	}
}
