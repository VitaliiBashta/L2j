
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.stats.Formulas;
import com.l2jserver.gameserver.util.Util;

/**
 * Transfer Hate effect implementation.
 * @author Adry_85
 */
public final class TransferHate extends AbstractEffect {
	private final int _chance;
	
	public TransferHate(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_chance = params.getInt("chance", 100);
	}
	
	@Override
	public boolean calcSuccess(BuffInfo info) {
		return Formulas.calcProbability(_chance, info.getEffector(), info.getEffected(), info.getSkill());
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (!Util.checkIfInRange(info.getSkill().getEffectRange(), info.getEffector(), info.getEffected(), true)) {
			return;
		}
		
		for (L2Character obj : info.getEffector().getKnownList().getKnownCharactersInRadius(info.getSkill().getAffectRange())) {
			if ((obj == null) || !obj.isAttackable() || obj.isDead()) {
				continue;
			}
			
			final L2Attackable hater = ((L2Attackable) obj);
			final long hate = hater.getHating(info.getEffector());
			if (hate <= 0) {
				continue;
			}
			
			hater.reduceHate(info.getEffector(), -hate);
			hater.addDamageHate(info.getEffected(), 0, hate);
		}
	}
}
