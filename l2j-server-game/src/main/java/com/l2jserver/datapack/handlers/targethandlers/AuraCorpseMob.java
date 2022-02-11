package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class AuraCorpseMob implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    List<L2Object> targetList = new ArrayList<>();
    // Go through the L2Character _knownList
    final Collection<L2Character> objs =
        activeChar.getKnownList().getKnownCharactersInRadius(skill.getAffectRange());
    int maxTargets = skill.getAffectLimit();
    for (L2Character obj : objs) {
      if (obj.isAttackable() && obj.isDead()) {
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
    return TargetType.AURA_CORPSE_MOB;
  }
}
