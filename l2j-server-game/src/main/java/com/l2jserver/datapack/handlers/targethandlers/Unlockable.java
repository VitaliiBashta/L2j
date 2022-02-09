package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2ChestInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;

import java.util.List;

public class Unlockable implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    if ((target == null) || (!target.isDoor() && !(target instanceof L2ChestInstance))) {
      return EMPTY_TARGET_LIST;
    }

    return List.of(target);
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.UNLOCKABLE;
  }
}
