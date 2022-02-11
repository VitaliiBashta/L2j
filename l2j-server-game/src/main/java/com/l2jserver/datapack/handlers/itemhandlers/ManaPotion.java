package com.l2jserver.datapack.handlers.itemhandlers;

import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import org.springframework.stereotype.Service;

import static com.l2jserver.gameserver.config.Configuration.customs;

@Service
public class ManaPotion extends ItemSkills {
  @Override
  public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
    if (!customs().enableManaPotionSupport()) {
      playable.sendPacket(SystemMessageId.NOTHING_HAPPENED);
      return false;
    }
    return super.useItem(playable, item, forceUse);
  }
}
