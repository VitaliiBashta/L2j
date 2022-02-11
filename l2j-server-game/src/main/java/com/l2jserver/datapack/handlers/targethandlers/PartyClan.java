package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.TvTEvent;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class PartyClan implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    if (onlyFirst) {
      return List.of(activeChar);
    }

    final L2PcInstance player = activeChar.getActingPlayer();

    if (player == null) {
      return EMPTY_TARGET_LIST;
    }

    List<L2Object> targetList = new ArrayList<>();
    targetList.add(player);

    final int radius = skill.getAffectRange();
    final boolean hasClan = player.getClan() != null;
    final boolean hasParty = player.isInParty();

    if (Skill.addSummon(activeChar, player, radius, false)) {
      targetList.add(player.getSummon());
    }

    // if player in clan and not in party
    if (!(hasClan || hasParty)) {
      return targetList;
    }

    // Get all visible objects in a spherical area near the L2Character
    final Collection<L2PcInstance> objs = activeChar.getKnownList().getKnownPlayersInRadius(radius);
    int maxTargets = skill.getAffectLimit();
    for (L2PcInstance obj : objs) {
      if (obj == null) {
        continue;
      }

      // olympiad mode - adding only own side
      if (player.isInOlympiadMode()) {
        if (!obj.isInOlympiadMode()) {
          continue;
        }
        if (player.getOlympiadGameId() != obj.getOlympiadGameId()) {
          continue;
        }
        if (player.getOlympiadSide() != obj.getOlympiadSide()) {
          continue;
        }
      }

      if (player.isInDuel()) {
        if (player.getDuelId() != obj.getDuelId()) {
          continue;
        }

        if (hasParty
            && obj.isInParty()
            && (player.getParty().getLeaderObjectId() != obj.getParty().getLeaderObjectId())) {
          continue;
        }
      }

      if (!((hasClan && (obj.getClanId() == player.getClanId()))
          || (hasParty
              && obj.isInParty()
              && (player.getParty().getLeaderObjectId() == obj.getParty().getLeaderObjectId())))) {
        continue;
      }

      // Don't add this target if this is a Pc->Pc pvp
      // casting and pvp condition not met
      if (!player.checkPvpSkill(obj, skill)) {
        continue;
      }

      if (!TvTEvent.checkForTvTSkill(player, obj, skill)) {
        continue;
      }

      if (!onlyFirst && Skill.addSummon(activeChar, obj, radius, false)) {
        targetList.add(obj.getSummon());
      }

      if (!Skill.addCharacter(activeChar, obj, radius, false)) {
        continue;
      }

      if (onlyFirst) {
        return List.of(obj);
      }

      if ((maxTargets > 0) && (targetList.size() >= maxTargets)) {
        break;
      }

      targetList.add(obj);
    }
    return targetList;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.PARTY_CLAN;
  }
}
