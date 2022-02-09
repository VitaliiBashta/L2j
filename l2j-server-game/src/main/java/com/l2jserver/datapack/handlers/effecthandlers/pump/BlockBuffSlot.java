
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.AbnormalType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Block Buff Slot effect implementation.
 * @author Zoey76
 */
public final class BlockBuffSlot extends AbstractEffect {
	private final Set<AbnormalType> _blockBuffSlots;
	
	public BlockBuffSlot(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		String blockBuffSlots = params.getString("slot", null);
		if ((blockBuffSlots != null) && !blockBuffSlots.isEmpty()) {
			_blockBuffSlots = new HashSet<>();
			for (String slot : blockBuffSlots.split(";")) {
				_blockBuffSlots.add(AbnormalType.valueOf(slot));
			}
		} else {
			_blockBuffSlots = Collections.<AbnormalType> emptySet();
		}
	}
	
	@Override
	public void onExit(BuffInfo info) {
		info.getEffected().getEffectList().removeBlockedBuffSlots(_blockBuffSlots);
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().getEffectList().addBlockedBuffSlots(_blockBuffSlots);
	}
}