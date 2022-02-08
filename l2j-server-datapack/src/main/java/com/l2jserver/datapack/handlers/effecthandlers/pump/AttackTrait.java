
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.stat.CharStat;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.stats.TraitType;

/**
 * Attack Trait effect implementation.
 * @author NosBit
 */
public final class AttackTrait extends AbstractEffect {
	private final Map<TraitType, Float> _attackTraits = new HashMap<>();
	
	public AttackTrait(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		if (params.isEmpty()) {
			_log.warning(getClass().getSimpleName() + ": this effect must have parameters!");
			return;
		}
		
		for (Entry<String, Object> param : params.getSet().entrySet()) {
			_attackTraits.put(TraitType.valueOf(param.getKey()), (Float.parseFloat((String) param.getValue()) + 100) / 100);
		}
	}
	
	@Override
	public void onExit(BuffInfo info) {
		final CharStat charStat = info.getEffected().getStat();
		synchronized (charStat.getAttackTraits()) {
			for (Entry<TraitType, Float> trait : _attackTraits.entrySet()) {
				charStat.getAttackTraits()[trait.getKey().getId()] /= trait.getValue();
				charStat.getAttackTraitsCount()[trait.getKey().getId()]--;
			}
		}
	}
	
	@Override
	public void onStart(BuffInfo info) {
		final CharStat charStat = info.getEffected().getStat();
		synchronized (charStat.getAttackTraits()) {
			for (Entry<TraitType, Float> trait : _attackTraits.entrySet()) {
				charStat.getAttackTraits()[trait.getKey().getId()] *= trait.getValue();
				charStat.getAttackTraitsCount()[trait.getKey().getId()]++;
			}
		}
	}
}
