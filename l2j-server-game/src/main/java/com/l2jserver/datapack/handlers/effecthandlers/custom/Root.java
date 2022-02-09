
package com.l2jserver.datapack.handlers.effecthandlers.custom;

import com.l2jserver.gameserver.ai.CtrlEvent;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.EffectFlag;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Root effect implementation.
 * @author mkizub
 */
public final class Root extends AbstractEffect {
	public Root(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public int getEffectFlags() {
		return EffectFlag.ROOTED.getMask();
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.ROOT;
	}
	
	@Override
	public void onExit(BuffInfo info) {
		if (!info.getEffected().isPlayer()) {
			info.getEffected().getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().stopMove(null);
		info.getEffected().getAI().notifyEvent(CtrlEvent.EVT_ROOTED);
	}
}
