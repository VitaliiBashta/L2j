
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Static Damage effect implementation.
 * @author Adry_85
 */
public final class StaticDamage extends AbstractEffect {
	private final int _power;
	
	public StaticDamage(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getInt("power", 0);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffector().isAlikeDead()) {
			return;
		}
		
		info.getEffected().reduceCurrentHp(_power, info.getEffector(), info.getSkill());
		info.getEffected().notifyDamageReceived(_power, info.getEffector(), info.getSkill(), false, false, false);
		
		if (info.getEffector().isPlayer()) {
			info.getEffector().sendDamageMessage(info.getEffected(), _power, false, false, false);
		}
	}
}