package com.l2jserver.gameserver.model.holders;

import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.model.skills.Skill;

public class SkillHolder {
  private final int skillId;
  private final int skillLvl;

  public SkillHolder(int skillId) {
    this.skillId = skillId;
    skillLvl = 1;
  }

  public SkillHolder(int skillId, int skillLvl) {
    this.skillId = skillId;
    this.skillLvl = skillLvl;
  }

  public SkillHolder(Skill skill) {
    skillId = skill.getId();
    skillLvl = skill.getLevel();
  }

  public final int getSkillId() {
    return skillId;
  }

  public final int getSkillLvl() {
    return skillLvl;
  }

  public final Skill getSkill() {
    return SkillData.getInstance().getSkill(skillId, Math.max(skillLvl, 1));
  }

  public final Skill getSkill(int levelOverride) {
    return SkillData.getInstance().getSkill(skillId, Math.max(levelOverride, 1));
  }

  @Override
  public String toString() {
    return "[SkillId: " + skillId + " Level: " + skillLvl + "]";
  }
}
