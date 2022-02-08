
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import java.util.List;

import com.l2jserver.gameserver.enums.DispelCategory;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.EffectScope;
import com.l2jserver.gameserver.model.stats.Formulas;

/**
 * Steal Abnormal effect implementation.
 * @author Adry_85, Zoey76
 */
public final class StealAbnormal extends AbstractEffect {
	private final DispelCategory _slot;
	private final int _rate;
	private final int _max;
	
	public StealAbnormal(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_slot = params.getEnum("slot", DispelCategory.class, DispelCategory.BUFF);
		_rate = params.getInt("rate", 0);
		_max = params.getInt("max", 0);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.STEAL_ABNORMAL;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if ((info.getEffected() != null) && info.getEffected().isPlayer() && (info.getEffector() != info.getEffected())) {
			final List<BuffInfo> toSteal = Formulas.calcStealEffects(info.getEffector(), info.getEffected(), info.getSkill(), _slot, _rate, _max);
			if (toSteal.isEmpty()) {
				return;
			}
			
			for (BuffInfo infoToSteal : toSteal) {
				// Invert effected and effector.
				final BuffInfo stolen = new BuffInfo(info.getEffected(), info.getEffector(), infoToSteal.getSkill());
				stolen.setAbnormalTime(infoToSteal.getTime()); // Copy the remaining time.
				// To include all the effects, it's required to go through the template rather the buff info.
				infoToSteal.getSkill().applyEffectScope(EffectScope.GENERAL, stolen, true, true);
				info.getEffected().getEffectList().remove(true, infoToSteal);
				info.getEffector().getEffectList().add(stolen);
			}
		}
	}
}