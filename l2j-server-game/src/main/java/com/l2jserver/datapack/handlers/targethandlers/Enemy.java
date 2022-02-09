package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;

import java.util.List;

import static com.l2jserver.gameserver.model.skills.targets.TargetType.ENEMY;
import static com.l2jserver.gameserver.network.SystemMessageId.INCORRECT_TARGET;

public class Enemy implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    switch (skill.getAffectScope()) {
      case SINGLE:
        {
          if (target == null) {
            return EMPTY_TARGET_LIST;
          }

          if (target.isDead()) {
            activeChar.sendPacket(INCORRECT_TARGET);
            return EMPTY_TARGET_LIST;
          }

          if (target.isAttackable()) {
            return List.of(target);
          }

          final L2PcInstance player = activeChar.getActingPlayer();
          if (player == null) {
            return EMPTY_TARGET_LIST;
          }

          if (!player.checkIfPvP(target) && !player.getCurrentSkill().isCtrlPressed()) {
            player.sendPacket(INCORRECT_TARGET);
            return EMPTY_TARGET_LIST;
          }

          return List.of(target);
        }
    }
    return EMPTY_TARGET_LIST;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return ENEMY;
  }
}
