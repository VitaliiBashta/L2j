package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.network.SystemMessageId;

import java.util.List;

public class PartyOther implements ITargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    if ((target != null)
        && (target != activeChar)
        && activeChar.isInParty()
        && target.isInParty()
        && (activeChar.getParty().getLeaderObjectId() == target.getParty().getLeaderObjectId())) {
      if (!target.isDead()) {
        if (target.isPlayer()) {
          switch (skill.getId()) {
              // FORCE BUFFS may cancel here but there should be a proper condition
            case 426:
              if (!target.getActingPlayer().isMageClass()) {
                return List.of(target);
              }
              return EMPTY_TARGET_LIST;
            case 427:
              if (target.getActingPlayer().isMageClass()) {
                return List.of(target);
              }
              return EMPTY_TARGET_LIST;
          }
        }
        return List.of(target);
      }
      return EMPTY_TARGET_LIST;
    }
    activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
    return EMPTY_TARGET_LIST;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.PARTY_OTHER;
  }
}
