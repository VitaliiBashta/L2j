
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Call Skill effect implementation.
 * @author NosBit
 */
public final class CallSkill extends AbstractEffect {
	private final SkillHolder _skill;
	
	public CallSkill(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_skill = new SkillHolder(params.getInt("skillId"), params.getInt("skillLevel", 1));
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffector().makeTriggerCast(_skill.getSkill(), info.getEffected(), true);
	}
}
