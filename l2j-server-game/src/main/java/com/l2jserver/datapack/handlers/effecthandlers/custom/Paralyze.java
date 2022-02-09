
package com.l2jserver.datapack.handlers.effecthandlers.custom;

import com.l2jserver.gameserver.ai.CtrlEvent;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.EffectFlag;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Paralyze effect implementation.
 */
public final class Paralyze extends AbstractEffect {
	public Paralyze(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public int getEffectFlags() {
		return EffectFlag.PARALYZED.getMask();
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.PARALYZE;
	}
	
	@Override
	public void onExit(BuffInfo info) {
		if (!info.getEffected().isPlayer()) {
			info.getEffected().getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, info.getEffector());
		info.getEffected().startParalyze();
	}
}
