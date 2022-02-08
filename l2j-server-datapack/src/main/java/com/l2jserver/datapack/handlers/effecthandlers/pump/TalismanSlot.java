
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Talisman Slot effect implementation.
 * @author Adry_85
 */
public final class TalismanSlot extends AbstractEffect {
	private final int _slots;
	
	public TalismanSlot(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_slots = params.getInt("slots", 0);
	}
	
	@Override
	public boolean canStart(BuffInfo info) {
		return (info.getEffector() != null) && (info.getEffected() != null) && info.getEffected().isPlayer();
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().getActingPlayer().getStat().addTalismanSlots(_slots);
	}
	
	@Override
	public boolean onActionTime(BuffInfo info) {
		return info.getSkill().isPassive();
	}
	
	@Override
	public void onExit(BuffInfo info) {
		info.getEffected().getActingPlayer().getStat().addTalismanSlots(-_slots);
	}
}
