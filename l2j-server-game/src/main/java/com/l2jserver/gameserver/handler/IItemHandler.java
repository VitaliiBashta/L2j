package com.l2jserver.gameserver.handler;

import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

import java.util.logging.Logger;

public interface IItemHandler {
  Logger _log = Logger.getLogger(IItemHandler.class.getName());

  /**
   * Launch task associated to the item.
   *
   * @param playable the non-NPC character using the item
   * @param item L2ItemInstance designating the item to use
   * @param forceUse ctrl hold on item use
   * @return {@code true} if the item all conditions are met and the item is used, {@code false}
   *     otherwise.
   */
  boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse);
}
