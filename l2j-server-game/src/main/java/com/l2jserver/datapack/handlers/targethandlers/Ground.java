package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.model.zone.ZoneId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Ground implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    final List<L2Object> targetList = new ArrayList<>();
    final L2PcInstance player = (L2PcInstance) activeChar;
    final int maxTargets = skill.getAffectLimit();
    final boolean srcInArena =
        (activeChar.isInsideZone(ZoneId.PVP) && !activeChar.isInsideZone(ZoneId.SIEGE));

    for (L2Character character : activeChar.getKnownList().getKnownCharacters()) {
      if ((character != null)
          && character.isInsideRadius(
              player.getCurrentSkillWorldPosition(), skill.getAffectRange(), false, false)) {
        if (!Skill.checkForAreaOffensiveSkills(activeChar, character, skill, srcInArena)) {
          continue;
        }

        if (character.isDoor()) {
          continue;
        }

        if ((maxTargets > 0) && (targetList.size() >= maxTargets)) {
          break;
        }
        targetList.add(character);
      }
    }

    if (targetList.isEmpty()) {
      if (skill.hasEffectType(L2EffectType.SUMMON_NPC)) {
        targetList.add(activeChar);
      }
    }
    return targetList;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.GROUND;
  }
}
