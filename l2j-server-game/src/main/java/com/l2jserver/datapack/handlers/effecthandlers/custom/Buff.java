
package com.l2jserver.datapack.handlers.effecthandlers.custom;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Buff effect implementation.
 * @author mkizub
 */
public class Buff extends AbstractEffect {
	public Buff(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.BUFF;
	}
	
	@Override
	public boolean onActionTime(BuffInfo info) {
		return info.getSkill().isPassive() || info.getSkill().isToggle();
	}
}
