
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.EffectFlag;

/**
 * Block Resurrection effect implementation.
 * @author UnAfraid
 */
public final class BlockResurrection extends AbstractEffect {
	public BlockResurrection(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public int getEffectFlags() {
		return EffectFlag.BLOCK_RESURRECTION.getMask();
	}
}