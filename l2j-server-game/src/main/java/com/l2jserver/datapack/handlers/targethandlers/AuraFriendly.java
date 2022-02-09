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

import java.util.ArrayList;
import java.util.List;

public class AuraFriendly implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    List<L2Character> targetList = new ArrayList<>();
    L2PcInstance player = activeChar.getActingPlayer();
    int maxTargets = skill.getAffectLimit();
    for (L2Character obj :
        player.getKnownList().getKnownCharactersInRadius(skill.getAffectRange())) {
      if ((obj == activeChar) || !checkTarget(player, obj)) {
        continue;
      }

      if ((maxTargets > 0) && (targetList.size() >= maxTargets)) {
        break;
      }

      targetList.add(obj);
    }

    return EMPTY_TARGET_LIST;
  }

  private boolean checkTarget(L2PcInstance activeChar, L2Character target) {
    if ((target == null) || !GeoData.getInstance().canSeeTarget(activeChar, target)) {
      return false;
    }

    if (target.isAlikeDead()
        || target.isDoor()
        || (target instanceof L2SiegeFlagInstance)
        || target.isMonster()) {
      return false;
    }

    if (target.isPlayable()) {
      L2PcInstance targetPlayer = target.getActingPlayer();

      if (activeChar.isInDuelWith(target)) {
        return false;
      }

      if (activeChar.isInPartyWith(target)) {
        return true;
      }

      if (target.isInsideZone(ZoneId.PVP)) {
        return false;
      }

      if (activeChar.isInClanWith(target)
          || activeChar.isInAllyWith(target)
          || activeChar.isInCommandChannelWith(target)) {
        return true;
      }

      if ((targetPlayer.getPvpFlag() > 0) || (targetPlayer.getKarma() > 0)) {
        return false;
      }
    }

    return true;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.AURA_FRIENDLY;
  }
}
