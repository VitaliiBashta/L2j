
package com.l2jserver.datapack.handlers.effecthandlers.custom;

import com.l2jserver.gameserver.ai.CtrlEvent;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.EffectFlag;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Mute effect implementation.
 */
public final class Mute extends AbstractEffect {
	public Mute(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public int getEffectFlags() {
		return EffectFlag.MUTED.getMask();
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.MUTE;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().abortCast();
		info.getEffected().getAI().notifyEvent(CtrlEvent.EVT_MUTED);
	}
}
