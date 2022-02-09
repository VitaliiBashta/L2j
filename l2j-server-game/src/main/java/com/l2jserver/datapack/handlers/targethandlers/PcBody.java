package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PetInstance;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.SystemMessageId;

import java.util.ArrayList;
import java.util.List;

public class PcBody implements ITargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    List<L2Object> targetList = new ArrayList<>();
    if ((target != null) && target.isDead()) {
      final L2PcInstance player;
      if (activeChar.isPlayer()) {
        player = activeChar.getActingPlayer();
      } else {
        player = null;
      }

      final L2PcInstance targetPlayer;
      if (target.isPlayer()) {
        targetPlayer = target.getActingPlayer();
      } else {
        targetPlayer = null;
      }

      final L2PetInstance targetPet;
      if (target.isPet()) {
        targetPet = (L2PetInstance) target;
      } else {
        targetPet = null;
      }

      if ((player != null) && ((targetPlayer != null) || (targetPet != null))) {
        boolean condGood = true;

        if (skill.hasEffectType(L2EffectType.RESURRECTION)) {
          if (targetPlayer != null) {
            // check target is not in a active siege zone
            if (targetPlayer.isInsideZone(ZoneId.SIEGE) && !targetPlayer.isInSiege()) {
              condGood = false;
              activeChar.sendPacket(SystemMessageId.CANNOT_BE_RESURRECTED_DURING_SIEGE);
            }

            if (targetPlayer
                .isFestivalParticipant()) // Check to see if the current player target is in a
            // festival.
            {
              condGood = false;
              activeChar.sendMessage("You may not resurrect participants in a festival.");
            }
          }
        }

        if (condGood) {
          if (!onlyFirst) {
            targetList.add(target);
            return targetList;
          }
          return List.of(target);
        }
      }
    }
    activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
    return EMPTY_TARGET_LIST;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.PC_BODY;
  }
}
