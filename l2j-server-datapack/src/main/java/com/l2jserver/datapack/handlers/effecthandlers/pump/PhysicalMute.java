
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.ai.CtrlEvent;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.EffectFlag;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Physical Mute effect implementation.
 * @author -Nemesiss-
 */
public final class PhysicalMute extends AbstractEffect {
	public PhysicalMute(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public int getEffectFlags() {
		return EffectFlag.PSYCHICAL_MUTED.getMask();
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().getAI().notifyEvent(CtrlEvent.EVT_MUTED);
	}
}
