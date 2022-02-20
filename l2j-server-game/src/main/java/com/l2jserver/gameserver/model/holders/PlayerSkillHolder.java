package com.l2jserver.gameserver.model.holders;

import com.l2jserver.gameserver.data.xml.impl.SkillTreesData;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.interfaces.ISkillsHolder;
import com.l2jserver.gameserver.model.skills.Skill;

import java.util.HashMap;
import java.util.Map;

public class PlayerSkillHolder implements ISkillsHolder {
  private final Map<Integer, Skill> _skills = new HashMap<>();

  public PlayerSkillHolder(L2PcInstance player) {
    for (Skill skill : player.getSkills().values()) {
      // Adding only skills that can be learned by the player.
      if (SkillTreesData.getInstance().isSkillAllowed(player, skill)) {
        addSkill(skill);
      }
    }
  }

  /**
   * @return the map containing this character skills.
   */
  @Override
  public Map<Integer, Skill> getSkills() {
    return _skills;
  }

  /**
   * Add a skill to the skills map.<br>
   */
  @Override
  public Skill addSkill(Skill skill) {
    return _skills.put(skill.getId(), skill);
  }

  /**
   * @param skillId The identifier of the L2Skill to check the knowledge
   * @return the skill from the known skill.
   */
  @Override
  public Skill getKnownSkill(int skillId) {
    return _skills.get(skillId);
  }

  /**
   * Return the level of a skill owned by the L2Character.
   * @param skillId The identifier of the L2Skill whose level must be returned
   * @return The level of the L2Skill identified by skillId
   */
  @Override
  public int getSkillLevel(int skillId) {
    final Skill skill = getKnownSkill(skillId);
    return (skill == null) ? -1 : skill.getLevel();
  }
}
