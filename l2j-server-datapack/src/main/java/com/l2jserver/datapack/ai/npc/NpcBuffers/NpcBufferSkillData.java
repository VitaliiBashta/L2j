package com.l2jserver.datapack.ai.npc.NpcBuffers;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.AffectObject;
import com.l2jserver.gameserver.model.skills.targets.AffectScope;

public class NpcBufferSkillData {
  private final SkillHolder _skill;
  private final int _initialDelay;
  private final int _delay;
  private final AffectScope _affectScope;
  private final AffectObject _affectObject;

  public NpcBufferSkillData(StatsSet set) {
    _skill = new SkillHolder(set.getInt("id"), set.getInt("level"));
    _initialDelay = set.getInt("initialDelay", 0) * 1000;
    _delay = set.getInt("delay") * 1000;
    _affectScope = set.getEnum("affectScope", AffectScope.class);
    _affectObject = set.getEnum("affectObject", AffectObject.class);
  }

  public Skill getSkill() {
    return _skill.getSkill();
  }

  public int getInitialDelay() {
    return _initialDelay;
  }

  public int getDelay() {
    return _delay;
  }

  public AffectScope getAffectScope() {
    return _affectScope;
  }

  public AffectObject getAffectObject() {
    return _affectObject;
  }
}
