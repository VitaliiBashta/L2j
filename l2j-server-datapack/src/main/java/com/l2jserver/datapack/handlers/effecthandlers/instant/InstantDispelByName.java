
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import static com.l2jserver.gameserver.model.effects.L2EffectType.DISPEL;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Dispel By Name instant effect.
 * @author Zoey76
 * @version 2.6.2.0
 */
public class InstantDispelByName extends AbstractEffect {
	
	private final int id;
	
	public InstantDispelByName(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		this.id = params.getInt("id");
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
		effected.getEffectList().stopSkillEffects(true, id);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return DISPEL;
	}
}
