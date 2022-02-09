package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;

import java.util.List;

public class Servitor implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    if (activeChar.hasServitor()) {
      return List.of(activeChar.getSummon());
    }
    return EMPTY_TARGET_LIST;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.SERVITOR;
  }
}
