package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.model.zone.ZoneId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BehindAura implements ITargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    List<L2Object> targetList = new ArrayList<>();
    final boolean srcInArena =
        (activeChar.isInsideZone(ZoneId.PVP) && !activeChar.isInsideZone(ZoneId.SIEGE));
    final Collection<L2Character> objs =
        activeChar.getKnownList().getKnownCharactersInRadius(skill.getAffectRange());
    int maxTargets = skill.getAffectLimit();
    for (L2Character obj : objs) {
      if (obj.isAttackable() || obj.isPlayable()) {

        if (!obj.isBehind(activeChar)) {
          continue;
        }

        if (!Skill.checkForAreaOffensiveSkills(activeChar, obj, skill, srcInArena)) {
          continue;
        }

        if (onlyFirst) {
          return List.of(obj);
        }

        if ((maxTargets > 0) && (targetList.size() >= maxTargets)) {
          break;
        }

        targetList.add(obj);
      }
    }
    return targetList;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.BEHIND_AURA;
  }
}
