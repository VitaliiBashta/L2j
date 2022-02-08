
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.EffectFlag;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Noblesse Blessing effect implementation.
 * @author earendil
 */
public final class NoblesseBless extends AbstractEffect {
	public NoblesseBless(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean canStart(BuffInfo info) {
		return (info.getEffector() != null) && (info.getEffected() != null) && info.getEffected().isPlayable();
	}
	
	@Override
	public int getEffectFlags() {
		return EffectFlag.NOBLESS_BLESSING.getMask();
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.NOBLESSE_BLESSING;
	}
}
