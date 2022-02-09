
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import java.util.ArrayList;
import java.util.List;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Resist Skill effect implementaion.
 * @author UnAfraid
 */
public final class ResistSkill extends AbstractEffect {
	private final List<SkillHolder> _skills = new ArrayList<>();
	
	public ResistSkill(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		for (int i = 1;; i++) {
			int skillId = params.getInt("skillId" + i, 0);
			int skillLvl = params.getInt("skillLvl" + i, 0);
			if (skillId == 0) {
				break;
			}
			_skills.add(new SkillHolder(skillId, skillLvl));
		}
		
		if (_skills.isEmpty()) {
			throw new IllegalArgumentException(getClass().getSimpleName() + ": Without parameters!");
		}
	}
	
	@Override
	public void onStart(BuffInfo info) {
		final L2Character effected = info.getEffected();
		for (SkillHolder holder : _skills) {
			effected.addInvulAgainst(holder);
			effected.sendDebugMessage("Applying invul against " + holder.getSkill());
		}
	}
	
	@Override
	public void onExit(BuffInfo info) {
		final L2Character effected = info.getEffected();
		for (SkillHolder holder : _skills) {
			info.getEffected().removeInvulAgainst(holder);
			effected.sendDebugMessage("Removing invul against " + holder.getSkill());
		}
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.BUFF;
	}
}
