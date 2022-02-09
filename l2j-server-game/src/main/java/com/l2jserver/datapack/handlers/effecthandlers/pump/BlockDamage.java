
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.EffectFlag;

/**
 * @author Zealar
 */
public final class BlockDamage extends AbstractEffect {
	public enum BlockType {
		HP,
		MP
	}
	
	private final BlockType _type;
	
	public BlockDamage(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		_type = params.getEnum("type", BlockType.class, BlockType.HP);
	}
	
	@Override
	public int getEffectFlags() {
		return _type == BlockType.HP ? EffectFlag.BLOCK_HP.getMask() : EffectFlag.BLOCK_MP.getMask();
	}
}
