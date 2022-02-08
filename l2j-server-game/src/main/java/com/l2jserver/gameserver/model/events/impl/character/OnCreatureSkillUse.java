package com.l2jserver.gameserver.model.events.impl.character;

import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.skills.Skill;

import java.util.List;

/** An instantly executed event when L2Character is attacked by L2Character. */
public class OnCreatureSkillUse implements IBaseEvent {
  private final L2Character _caster;
  private final Skill _skill;
  private final boolean _simultaneously;
  private final L2Character _target;
  private final List<L2Object> _targets;

  public OnCreatureSkillUse(
      L2Character caster,
      Skill skill,
      boolean simultaneously,
      L2Character target,
      List<L2Object> targets) {
    _caster = caster;
    _skill = skill;
    _simultaneously = simultaneously;
    _target = target;
    _targets = targets;
  }

  public final L2Character getCaster() {
    return _caster;
  }

  public Skill getSkill() {
    return _skill;
  }

  public boolean isSimultaneously() {
    return _simultaneously;
  }

  public final L2Character getTarget() {
    return _target;
  }

  public List<L2Object> getTargets() {
    return _targets;
  }

  @Override
  public EventType getType() {
    return EventType.ON_CREATURE_SKILL_USE;
  }
}
