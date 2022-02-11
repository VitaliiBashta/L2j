package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.util.Util;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class Area implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    List<L2Object> targetList = new ArrayList<>();
    if ((target == null)
        || (((target == activeChar) || target.isAlikeDead()) && (skill.getCastRange() >= 0))
        || (!(target.isAttackable() || target.isPlayable()))) {
      activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
      return EMPTY_TARGET_LIST;
    }

    final L2Character origin;
    final boolean srcInArena =
        (activeChar.isInsideZone(ZoneId.PVP) && !activeChar.isInsideZone(ZoneId.SIEGE));

    if (skill.getCastRange() >= 0) {
      if (!Skill.checkForAreaOffensiveSkills(activeChar, target, skill, srcInArena)) {
        return EMPTY_TARGET_LIST;
      }

      if (onlyFirst) {
        return List.of(target);
      }

      origin = target;
      targetList.add(origin); // Add target to target list
    } else {
      origin = activeChar;
    }

    int maxTargets = skill.getAffectLimit();
    final Collection<L2Character> objs = activeChar.getKnownList().getKnownCharacters();
    for (L2Character obj : objs) {
      if (!(obj.isAttackable() || obj.isPlayable())) {
        continue;
      }

      if (obj == origin) {
        continue;
      }

      if (Util.checkIfInRange(skill.getAffectRange(), origin, obj, true)) {
        if (!Skill.checkForAreaOffensiveSkills(activeChar, obj, skill, srcInArena)) {
          continue;
        }

        if ((maxTargets > 0) && (targetList.size() >= maxTargets)) {
          break;
        }

        targetList.add(obj);
      }
    }

    if (targetList.isEmpty()) {
      return EMPTY_TARGET_LIST;
    }

    return targetList;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.AREA;
  }
}
