package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.model.zone.ZoneId;

import java.util.List;

public class EnemySummon implements ITargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    if ((target != null) && target.isSummon()) {
      final L2Summon targetSummon = (L2Summon) target;
      if ((activeChar.isPlayer()
              && (activeChar.getSummon() != targetSummon)
              && //
              !targetSummon.isDead()
              && ((targetSummon.getOwner().getPvpFlag() != 0)
                  || (targetSummon.getOwner().getKarma() > 0)))
          || //
          (targetSummon.getOwner().isInsideZone(ZoneId.PVP)
              && activeChar.getActingPlayer().isInsideZone(ZoneId.PVP))
          || //
          (targetSummon.getOwner().isInDuel()
              && activeChar.getActingPlayer().isInDuel()
              && (targetSummon.getOwner().getDuelId()
                  == activeChar.getActingPlayer().getDuelId()))) {
        return List.of(targetSummon);
      }
    }
    return EMPTY_TARGET_LIST;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.ENEMY_SUMMON;
  }
}
