package com.l2jserver.gameserver.model.interfaces;

import com.l2jserver.gameserver.model.skills.Skill;

import java.util.Map;

public interface ISkillsHolder {
	Map<Integer, Skill> getSkills();
	
	Skill addSkill(Skill skill);
	
	Skill getKnownSkill(int skillId);
	
	int getSkillLevel(int skillId);
}
