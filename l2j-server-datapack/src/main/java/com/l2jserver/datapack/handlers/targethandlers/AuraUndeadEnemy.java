package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;

import java.util.ArrayList;
import java.util.List;

/**
 * Aura Undead Enemy target handler implementation.
 *
 * @author Adry_85
 * @since 2.6.0.0
 */
public class AuraUndeadEnemy implements ITargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    List<L2Object> targetList = new ArrayList<>();
    int maxTargets = skill.getAffectLimit();
    for (L2Character obj :
        activeChar.getKnownList().getKnownCharactersInRadius(skill.getAffectRange())) {
      if (obj.isAttackable() && obj.isUndead()) {
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
    return TargetType.AURA_UNDEAD_ENEMY;
  }
}
