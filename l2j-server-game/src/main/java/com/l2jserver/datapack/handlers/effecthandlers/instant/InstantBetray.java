
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import static com.l2jserver.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import static com.l2jserver.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE;
import static com.l2jserver.gameserver.model.effects.EffectFlag.BETRAYED;
import static com.l2jserver.gameserver.model.effects.L2EffectType.DEBUFF;
import static java.util.concurrent.TimeUnit.SECONDS;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.stats.Formulas;

/**
 * Betray instant effect implementation.
 * @author Zoey76
 * @version 2.6.2.0
 */
public final class InstantBetray extends AbstractEffect {
	
	private final int chance;
	
	private final int time;
	
	public InstantBetray(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		this.chance = params.getInt("chance", 0);
		this.time = params.getInt("time", 0);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		final var effected = info.getEffected();
		if (effected == null) {
			return;
		}
		
		if (effected.isRaid()) {
			return;
		}
		
		final var target = effected.isServitor() || effected.isSummon() ? effected.getActingPlayer() //
			: effected.isRaidMinion() ? ((L2Attackable) effected).getLeader() : null;
		if (target == null) {
			return;
		}
		
		if (!Formulas.calcProbability(chance, info.getEffector(), effected, info.getSkill())) {
			return;
		}
		
		final var effectedAI = effected.getAI();
		effectedAI.setIntention(AI_INTENTION_ATTACK, target);
		
		ThreadPoolManager.getInstance().scheduleAiAtFixedRate(() -> effectedAI.setIntention(AI_INTENTION_IDLE, target), 0, time, SECONDS);
	}
	
	@Override
	public int getEffectFlags() {
		return BETRAYED.getMask();
	}
	
	@Override
	public L2EffectType getEffectType() {
		return DEBUFF;
	}
}
