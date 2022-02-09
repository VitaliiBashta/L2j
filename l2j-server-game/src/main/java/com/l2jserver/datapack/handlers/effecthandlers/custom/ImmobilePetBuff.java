
package com.l2jserver.datapack.handlers.effecthandlers.custom;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Immobile Pet Buff effect implementation.
 * @author demonia
 */
public final class ImmobilePetBuff extends AbstractEffect {
	public ImmobilePetBuff(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.BUFF;
	}
	
	@Override
	public void onExit(BuffInfo info) {
		info.getEffected().setIsImmobilized(false);
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffected().isSummon() && info.getEffector().isPlayer() && (((L2Summon) info.getEffected()).getOwner() == info.getEffector())) {
			info.getEffected().setIsImmobilized(true);
		}
	}
}