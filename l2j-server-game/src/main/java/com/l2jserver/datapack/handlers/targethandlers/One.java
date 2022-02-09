package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.network.SystemMessageId;

import java.util.List;

public class One implements ITargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    // Check for null target or any other invalid target
    if ((target == null) || target.isDead() || ((target == activeChar) && skill.isBad())) {
      activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
      return EMPTY_TARGET_LIST;
    }

    // If a target is found, return it in a table else send a system message TARGET_IS_INCORRECT
    return List.of(target);
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.ONE;
  }
}
