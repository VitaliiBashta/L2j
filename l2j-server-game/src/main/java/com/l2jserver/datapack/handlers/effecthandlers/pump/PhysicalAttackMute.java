
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.EffectFlag;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Physical Attack Mute effect implementation.
 * @author -Rnn-
 */
public final class PhysicalAttackMute extends AbstractEffect {
	public PhysicalAttackMute(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public int getEffectFlags() {
		return EffectFlag.PSYCHICAL_ATTACK_MUTED.getMask();
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().startPhysicalAttackMuted();
	}
}