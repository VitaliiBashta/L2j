package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;

import java.util.List;

import static com.l2jserver.gameserver.model.skills.targets.TargetType.TARGET;
import static com.l2jserver.gameserver.network.SystemMessageId.INCORRECT_TARGET;

public class Target implements ITargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    if (target == null) {
      return EMPTY_TARGET_LIST;
    }

    final var player = activeChar.getActingPlayer();
    if (player != null) {
      final var currentSkill = player.getCurrentSkill();
      if ((currentSkill != null)
          && !currentSkill.isCtrlPressed()
          && target.isAutoAttackable(activeChar)) {
        activeChar.sendPacket(INCORRECT_TARGET);
        return EMPTY_TARGET_LIST;
      }
    }

    return skill.getAffectScope().affectTargets(activeChar, target, skill);
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TARGET;
  }
}
