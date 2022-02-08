package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.model.zone.ZoneId;

import java.util.ArrayList;
import java.util.List;

public class Aura implements ITargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    final List<L2Object> targetList = new ArrayList<>();
    final boolean srcInArena =
        (activeChar.isInsideZone(ZoneId.PVP) && !activeChar.isInsideZone(ZoneId.SIEGE));
    for (L2Character obj :
        activeChar.getKnownList().getKnownCharactersInRadius(skill.getAffectRange())) {
      if (obj.isDoor() || obj.isAttackable() || obj.isPlayable() || obj.isTrap()) {
        // Stealth door targeting.
        if (obj.isDoor()) {
          final L2DoorInstance door = (L2DoorInstance) obj;
          if (!door.getTemplate().isStealth()) {
            continue;
          }
        }

        if (!Skill.checkForAreaOffensiveSkills(activeChar, obj, skill, srcInArena)) {
          continue;
        }

        if (activeChar.isPlayable() && obj.isAttackable() && !skill.isBad()) {
          continue;
        }

        if (onlyFirst) {
          return List.of(obj);
        }

        targetList.add(obj);
      }
    }
    return targetList;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.AURA;
  }
}
