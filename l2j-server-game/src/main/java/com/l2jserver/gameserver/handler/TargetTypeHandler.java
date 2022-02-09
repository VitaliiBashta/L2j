package com.l2jserver.gameserver.handler;

import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;

import java.util.List;

public interface TargetTypeHandler {
  List<L2Object> EMPTY_TARGET_LIST = List.of();

  List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target);

  default List<L2Object> getFirstTarget(Skill skill, L2Character activeChar, L2Character target) {
    return getTargetList(skill, activeChar, true, target);
  }

  Enum<TargetType> getTargetType();
}
