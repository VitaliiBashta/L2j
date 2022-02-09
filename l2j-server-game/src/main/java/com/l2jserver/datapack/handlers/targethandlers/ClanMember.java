package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClanMember implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    List<L2Object> targetList = new ArrayList<>();
    if (activeChar.isNpc()) {
      // for buff purposes, returns friendly mobs nearby and mob itself
      final L2Npc npc = (L2Npc) activeChar;
      if ((npc.getTemplate().getClans() == null) || npc.getTemplate().getClans().isEmpty()) {
        return List.of(activeChar);
      }
      final Collection<L2Object> objs = activeChar.getKnownList().getKnownObjects().values();
      for (L2Object newTarget : objs) {
        if (newTarget.isNpc() && npc.isInMyClan((L2Npc) newTarget)) {
          if (!Util.checkIfInRange(skill.getCastRange(), activeChar, newTarget, true)) {
            continue;
          }
          if (((L2Npc) newTarget).isAffectedBySkill(skill.getId())) {
            continue;
          }
          targetList.add((L2Npc) newTarget);
          break;
        }
      }
      if (targetList.isEmpty()) {
        targetList.add(npc);
      }
    } else {
      return EMPTY_TARGET_LIST;
    }
    return targetList;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.CLAN_MEMBER;
  }
}
