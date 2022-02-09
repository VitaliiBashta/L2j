
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Enable Cloak effect implementation.
 * @author Adry_85
 */
public final class EnableCloak extends AbstractEffect {
	public EnableCloak(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean canStart(BuffInfo info) {
		return (info.getEffector() != null) && (info.getEffected() != null) && info.getEffected().isPlayer();
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().getActingPlayer().getStat().setCloakSlotStatus(true);
	}
	
	@Override
	public boolean onActionTime(BuffInfo info) {
		return info.getSkill().isPassive();
	}
	
	@Override
	public void onExit(BuffInfo info) {
		info.getEffected().getActingPlayer().getStat().setCloakSlotStatus(false);
	}
}
