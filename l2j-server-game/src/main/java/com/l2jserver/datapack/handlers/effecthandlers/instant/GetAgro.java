
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Get Agro effect implementation.
 * @author Adry_85
 */
public final class GetAgro extends AbstractEffect {
	public GetAgro(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.AGGRESSION;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if ((info.getEffected() instanceof L2Attackable) && info.getEffected().hasAI() && (((L2Attackable) info.getEffected()).getMostHated() != info.getEffector())) {
			info.getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, info.getEffector());
		}
	}
}
