package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.network.SystemMessageId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TargetParty implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    List<L2Object> targetList = new ArrayList<>();

    // Check for null target or any other invalid target
    if ((target == null) || target.isDead() || (target == activeChar)) {
      activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
      return EMPTY_TARGET_LIST;
    }

    final int radius = skill.getAffectRange();
    final L2PcInstance player = (L2PcInstance) activeChar.getTarget();

    if (player.isInParty()) {
      for (L2PcInstance partyMember : player.getParty().getMembers()) {
        if ((partyMember == null)) {
          continue;
        }

        if (Skill.addCharacter(player, partyMember, radius, false)) {
          targetList.add(partyMember);
        }

        if (Skill.addSummon(player, partyMember, radius, false)) {
          targetList.add(partyMember.getSummon());
        }
      }
    } else {
      targetList.add(target);
    }
    return targetList;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.TARGET_PARTY;
  }
}
