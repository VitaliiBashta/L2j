package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2SiegeFlagInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.SystemMessageId;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
public class AreaFriendly implements TargetTypeHandler {
  private static final CharComparator CHAR_COMPARATOR = new CharComparator();

  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    L2PcInstance player = activeChar.getActingPlayer();

    if (!checkTarget(player, target) && (skill.getCastRange() >= 0)) {
      player.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
      return EMPTY_TARGET_LIST;
    }

    if (onlyFirst) {
      return List.of(target);
    }

    if (player.getActingPlayer().isInOlympiadMode()) {
      return List.of(player);
    }

    final List<L2Character> targetList = new LinkedList<>();
    if (target != null) {
      // Add target to target list.
      targetList.add(target);

      final int maxTargets = skill.getAffectLimit();
      for (L2Character obj :
          target.getKnownList().getKnownCharactersInRadius(skill.getAffectRange())) {
        if ((maxTargets > 0) && (targetList.size() >= maxTargets)) {
          break;
        }

        if (!checkTarget(player, obj) || (obj == activeChar)) {
          continue;
        }

        targetList.add(obj);
      }

      // Sort creatures, the most injured first.
      targetList.sort(CHAR_COMPARATOR);
    }

    if (targetList.isEmpty()) {
      return EMPTY_TARGET_LIST;
    }
    return targetList.stream().map(L2Object.class::cast).toList();
  }

  private boolean checkTarget(L2PcInstance activeChar, L2Character target) {
    if (!GeoData.getInstance().canSeeTarget(activeChar, target)) {
      return false;
    }

    if ((target == null)
        || target.isAlikeDead()
        || target.isDoor()
        || (target instanceof L2SiegeFlagInstance)
        || target.isMonster()) {
      return false;
    }

    // GMs and hidden creatures.
    if (target.isInvisible()) {
      return false;
    }

    if (target.isPlayable()) {
      L2PcInstance targetPlayer = target.getActingPlayer();

      if (activeChar == targetPlayer) {
        return true;
      }

      if (targetPlayer.inObserverMode() || targetPlayer.isInOlympiadMode()) {
        return false;
      }

      if (activeChar.isInDuelWith(target)) {
        return false;
      }

      if (activeChar.isInPartyWith(target)) {
        return true;
      }

      // Only siege allies.
      if (activeChar.isInSiege() && !activeChar.isOnSameSiegeSideWith(targetPlayer)) {
        return false;
      }

      if (target.isInsideZone(ZoneId.PVP)) {
        return false;
      }

      if (activeChar.isInClanWith(target)
          || activeChar.isInAllyWith(target)
          || activeChar.isInCommandChannelWith(target)) {
        return true;
      }

      return (targetPlayer.getPvpFlag() <= 0) && (targetPlayer.getKarma() <= 0);
    }
    return true;
  }

  public static class CharComparator implements Comparator<L2Character> {
    @Override
    public int compare(L2Character char1, L2Character char2) {
      return Double.compare(
          (char1.getCurrentHp() / char1.getMaxHp()), (char2.getCurrentHp() / char2.getMaxHp()));
    }
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.AREA_FRIENDLY;
  }
}
