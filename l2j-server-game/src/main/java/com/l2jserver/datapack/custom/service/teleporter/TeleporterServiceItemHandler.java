package com.l2jserver.datapack.custom.service.teleporter;

import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import org.springframework.stereotype.Service;

@Service
public class TeleporterServiceItemHandler implements IItemHandler {

  @Override
  public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
    if (!playable.isPlayer()) {
      return false;
    }

    TeleporterService.getService().executeCommand((L2PcInstance) playable, null, null);
    return true;
  }
}
