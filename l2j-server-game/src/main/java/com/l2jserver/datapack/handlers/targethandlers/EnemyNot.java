package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.l2jserver.gameserver.model.skills.targets.TargetType.ENEMY_NOT;
import static com.l2jserver.gameserver.network.SystemMessageId.INCORRECT_TARGET;

@Service
public class EnemyNot implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    if (target == null) {
      return EMPTY_TARGET_LIST;
    }

    if (target.isDead()) {
      activeChar.sendPacket(INCORRECT_TARGET);
      return EMPTY_TARGET_LIST;
    }

    if (target.isAutoAttackable(activeChar)) {
      activeChar.sendPacket(INCORRECT_TARGET);
      return EMPTY_TARGET_LIST;
    }

    return skill.getAffectScope().affectTargets(activeChar, target, skill);
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return ENEMY_NOT;
  }
}
