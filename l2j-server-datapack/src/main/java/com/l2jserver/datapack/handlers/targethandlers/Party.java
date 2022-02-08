package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;

import java.util.ArrayList;
import java.util.List;

public class Party implements ITargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    if (onlyFirst) {
      return List.of(activeChar);
    }

    List<L2Object> targetList = new ArrayList<>();
    targetList.add(activeChar);

    final int radius = skill.getAffectRange();
    L2PcInstance player = activeChar.getActingPlayer();
    if (activeChar.isSummon()) {
      if (Skill.addCharacter(activeChar, player, radius, false)) {
        targetList.add(player);
      }
    } else if (activeChar.isPlayer()) {
      if (Skill.addSummon(activeChar, player, radius, false)) {
        targetList.add(player.getSummon());
      }
    }

    if (activeChar.isInParty()) {
      // Get a list of Party Members
      for (L2PcInstance partyMember : activeChar.getParty().getMembers()) {
        if ((partyMember == null) || (partyMember == player)) {
          continue;
        }

        if (Skill.addCharacter(activeChar, partyMember, radius, false)) {
          targetList.add(partyMember);
        }

        if (Skill.addSummon(activeChar, partyMember, radius, false)) {
          targetList.add(partyMember.getSummon());
        }
      }
    }
    return targetList;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.PARTY;
  }
}
