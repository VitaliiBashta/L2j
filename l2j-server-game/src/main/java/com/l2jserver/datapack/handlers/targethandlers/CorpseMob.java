package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.network.SystemMessageId;

import java.util.List;

import static com.l2jserver.gameserver.config.Configuration.npc;

public class CorpseMob implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    if ((target == null) || !target.isAttackable() || !target.isDead()) {
      activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
      return EMPTY_TARGET_LIST;
    }

    if (skill.hasEffectType(L2EffectType.SUMMON)
        && target.isServitor()
        && (target.getActingPlayer() != null)
        && (target.getActingPlayer().getObjectId() == activeChar.getObjectId())) {
      return EMPTY_TARGET_LIST;
    }

    if (skill.hasEffectType(L2EffectType.HP_DRAIN)
        && ((L2Attackable) target)
            .isOldCorpse(
                activeChar.getActingPlayer(),
                npc().getCorpseConsumeSkillAllowedTimeBeforeDecay(),
                true)) {
      return EMPTY_TARGET_LIST;
    }

    return List.of(target);
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.CORPSE_MOB;
  }
}
