package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AreaSummon implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    List<L2Object> targetList = new ArrayList<>();
    target = activeChar.getSummon();
    if ((target == null) || !target.isServitor() || target.isDead()) {
      return EMPTY_TARGET_LIST;
    }

    if (onlyFirst) {
      return List.of(target);
    }

    final boolean srcInArena =
        (activeChar.isInsideZone(ZoneId.PVP) && !activeChar.isInsideZone(ZoneId.SIEGE));
    final Collection<L2Character> objs = target.getKnownList().getKnownCharacters();
    int maxTargets = skill.getAffectLimit();

    for (L2Character obj : objs) {
      if ((obj == null) || (obj == target) || (obj == activeChar)) {
        continue;
      }

      if (!Util.checkIfInRange(skill.getAffectRange(), target, obj, true)) {
        continue;
      }

      if (!(obj.isAttackable() || obj.isPlayable())) {
        continue;
      }

      if (!Skill.checkForAreaOffensiveSkills(activeChar, obj, skill, srcInArena)) {
        continue;
      }

      if ((maxTargets > 0) && (targetList.size() >= maxTargets)) {
        break;
      }

      targetList.add(obj);
    }

    if (targetList.isEmpty()) {
      return EMPTY_TARGET_LIST;
    }

    return targetList;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.AREA_SUMMON;
  }
}
