package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.network.SystemMessageId;

import java.util.List;

public class PartyMember implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    if (target == null) {
      activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
      return EMPTY_TARGET_LIST;
    }
    if (!target.isDead()) {
      if ((target == activeChar)
          || (activeChar.isInParty()
              && target.isInParty()
              && (activeChar.getParty().getLeaderObjectId()
                  == target.getParty().getLeaderObjectId()))
          || (activeChar.isPlayer() && target.isSummon() && (activeChar.getSummon() == target))
          || (activeChar.isSummon() && target.isPlayer() && (activeChar == target.getSummon()))) {
        return List.of(target);
      }
    }
    return EMPTY_TARGET_LIST;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.PARTY_MEMBER;
  }
}
