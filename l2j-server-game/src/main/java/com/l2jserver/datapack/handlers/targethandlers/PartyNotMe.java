package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.l2jserver.gameserver.config.Configuration.character;

public class PartyNotMe implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    if (activeChar.getParty() == null) {
      return List.of();
    }
    final List<L2Object> targetList = new ArrayList<>();
    final List<L2PcInstance> partyList = activeChar.getParty().getMembers();
    for (L2PcInstance partyMember : partyList) {
      if ((partyMember == null) || partyMember.isDead()) {
        continue;
      } else if (partyMember == activeChar) {
        continue;
      } else if (!Util.checkIfInRange(character().getPartyRange(), activeChar, partyMember, true)) {
        continue;
      } else if ((skill.getAffectRange() > 0)
          && !Util.checkIfInRange(skill.getAffectRange(), activeChar, partyMember, true)) {
        continue;
      } else {
        targetList.add(partyMember);

        if ((partyMember.getSummon() != null) && !partyMember.getSummon().isDead()) {
          targetList.add(partyMember.getSummon());
        }
      }
    }
    return targetList;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.PARTY_NOTME;
  }
}
