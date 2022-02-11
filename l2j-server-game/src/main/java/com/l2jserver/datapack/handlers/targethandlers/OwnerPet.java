package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerPet implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    if (!activeChar.isSummon()) {
      return EMPTY_TARGET_LIST;
    }
    var owner = ((L2Summon) activeChar).getOwner();
    if ((owner != null) && !owner.isDead()) {
      return List.of(owner);
    }
    return EMPTY_TARGET_LIST;
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return TargetType.OWNER_PET;
  }
}
