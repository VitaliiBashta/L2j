
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import static com.l2jserver.gameserver.model.effects.L2EffectType.BUFF;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Despawn instant effect implementation.
 * @author Zoey76
 * @version 2.6.2.0
 */
public final class InstantDespawn extends AbstractEffect {
	
	private final int chance;
	
	public InstantDespawn(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		this.chance = params.getInt("chance", 0);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		final var effected = info.getEffected();
		if (effected == null) {
			return;
		}
		
		final var player = effected.getActingPlayer();
		if (player == null) {
			return;
		}
		
		final var summon = player.getSummon();
		if (summon == null) {
			return;
		}
		
		if (Rnd.get(100) < chance) {
			return;
		}
		
		summon.unSummon(player);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return BUFF;
	}
}
