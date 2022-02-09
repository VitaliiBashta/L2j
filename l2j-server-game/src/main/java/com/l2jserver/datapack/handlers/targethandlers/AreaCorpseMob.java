package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AreaCorpseMob implements ITargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    if ((target == null) || !target.isAttackable() || !target.isDead()) {
      activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
      return EMPTY_TARGET_LIST;
    }

    if (onlyFirst) {
      return List.of(target);
    }

    final List<L2Object> targetList = new ArrayList<>();
    targetList.add(target);

    final boolean srcInArena =
        activeChar.isInsideZone(ZoneId.PVP) && !activeChar.isInsideZone(ZoneId.SIEGE);
    final Collection<L2Character> objs = activeChar.getKnownList().getKnownCharacters();
    for (L2Character obj : objs) {
      if (!(obj.isAttackable() || obj.isPlayable())
          || !Util.checkIfInRange(skill.getAffectRange(), target, obj, true)) {
        continue;
      }

      if (!Skill.checkForAreaOffensiveSkills(activeChar, obj, skill, srcInArena)) {
        continue;
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
    return TargetType.AREA_CORPSE_MOB;
  }
}
