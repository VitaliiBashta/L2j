package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.L2ClanMember;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.TvTEvent;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.util.Util;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CorpseClan implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    List<L2Object> targetList = new ArrayList<>();
    if (activeChar.isPlayable()) {
      final L2PcInstance player = activeChar.getActingPlayer();
      if (player == null) {
        return EMPTY_TARGET_LIST;
      }

      if (player.isInOlympiadMode()) {
        return List.of(player);
      }

      final L2Clan clan = player.getClan();
      if (clan != null) {
        final int radius = skill.getAffectRange();
        final int maxTargets = skill.getAffectLimit();
        for (L2ClanMember member : clan.getMembers()) {
          final L2PcInstance obj = member.getPlayerInstance();
          if ((obj == null) || (obj == player)) {
            continue;
          }

          if (player.isInDuel()) {
            if (player.getDuelId() != obj.getDuelId()) {
              continue;
            }
            if (player.isInParty()
                && obj.isInParty()
                && (player.getParty().getLeaderObjectId() != obj.getParty().getLeaderObjectId())) {
              continue;
            }
          }

          // Don't add this target if this is a Pc->Pc pvp casting and pvp condition not met
          if (!player.checkPvpSkill(obj, skill)) {
            continue;
          }

          if (!TvTEvent.checkForTvTSkill(player, obj, skill)) {
            continue;
          }

          if (!Skill.addCharacter(activeChar, obj, radius, true)) {
            continue;
          }

          // check target is not in a active siege zone
          if (obj.isInsideZone(ZoneId.SIEGE) && !obj.isInSiege()) {
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
      }
    } else if (activeChar.isNpc()) {
      // for buff purposes, returns friendly mobs nearby and mob itself
      final L2Npc npc = (L2Npc) activeChar;
      if ((npc.getTemplate().getClans() == null) || npc.getTemplate().getClans().isEmpty()) {
        return List.of(activeChar);
      }

      targetList.add(activeChar);

      final Collection<L2Object> objs = activeChar.getKnownList().getKnownObjects().values();
      int maxTargets = skill.getAffectLimit();
      for (L2Object newTarget : objs) {
        if (newTarget.isNpc() && npc.isInMyClan((L2Npc) newTarget)) {
          if (!Util.checkIfInRange(skill.getCastRange(), activeChar, newTarget, true)) {
            continue;
          }

          if (targetList.size() >= maxTargets) {
            break;
          }

          targetList.add(newTarget);
        }
      }
    }

    return targetList;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.CORPSE_CLAN;
  }
}
