
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.EffectFlag;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Betray effect implementation.
 * @author decad
 */
public final class Betray extends AbstractEffect {
	public Betray(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean canStart(BuffInfo info) {
		return info.getEffector().isPlayer() && info.getEffected().isSummon();
	}
	
	@Override
	public int getEffectFlags() {
		return EffectFlag.BETRAYED.getMask();
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.DEBUFF;
	}
	
	@Override
	public void onExit(BuffInfo info) {
		info.getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, info.getEffected().getActingPlayer());
	}
}
