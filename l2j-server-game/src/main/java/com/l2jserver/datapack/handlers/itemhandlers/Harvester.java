package com.l2jserver.datapack.handlers.itemhandlers;

import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import org.springframework.stereotype.Service;

import static com.l2jserver.gameserver.config.Configuration.general;

@Service
public class Harvester implements IItemHandler {
  @Override
  public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
    if (!general().allowManor()) {
      return false;
    } else if (!playable.isPlayer()) {
      playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
      return false;
    }

    final SkillHolder[] skills = item.getItem().getSkills();
    if (skills == null) {
      _log.warning(getClass().getSimpleName() + ": is missing skills!");
      return false;
    }

    final L2PcInstance activeChar = playable.getActingPlayer();
    final L2Object target = activeChar.getTarget();
    if ((target == null) || !target.isMonster() || !((L2Character) target).isDead()) {
      activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
      activeChar.sendPacket(ActionFailed.STATIC_PACKET);
      return false;
    }

    for (SkillHolder sk : skills) {
      activeChar.useMagic(sk.getSkill(), false, false);
    }
    return true;
  }
}
