
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.Skill;

/**
 * Set Skill effect implementation.
 * @author Zoey76
 */
public final class SetSkill extends AbstractEffect {
	private final int _skillId;
	private final int _skillLvl;
	
	public SetSkill(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_skillId = params.getInt("skillId", 0);
		_skillLvl = params.getInt("skillLvl", 1);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if ((info.getEffected() == null) || !info.getEffected().isPlayer()) {
			return;
		}
		
		final Skill skill = SkillData.getInstance().getSkill(_skillId, _skillLvl);
		if (skill == null) {
			return;
		}
		
		info.getEffected().getActingPlayer().addSkill(skill, true);
		info.getEffected().getActingPlayer().sendSkillList();
	}
}
