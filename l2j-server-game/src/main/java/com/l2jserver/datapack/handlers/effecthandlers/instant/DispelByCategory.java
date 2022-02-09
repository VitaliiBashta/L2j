
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import java.util.List;

import com.l2jserver.gameserver.enums.DispelCategory;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.stats.Formulas;

/**
 * Dispel By Category effect implementation.
 * @author DS, Adry_85
 */
public final class DispelByCategory extends AbstractEffect {
	private final DispelCategory _slot;
	private final int _rate;
	private final int _max;
	
	public DispelByCategory(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_slot = params.getEnum("slot", DispelCategory.class, DispelCategory.BUFF);
		_rate = params.getInt("rate", 0);
		_max = params.getInt("max", 0);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.DISPEL;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffected().isDead()) {
			return;
		}
		
		final List<BuffInfo> canceled = Formulas.calcCancelEffects(info.getEffector(), info.getEffected(), info.getSkill(), _slot, _rate, _max);
		for (BuffInfo can : canceled) {
			info.getEffected().getEffectList().stopSkillEffects(true, can.getSkill());
		}
	}
}