package com.l2jserver.gameserver.handler;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public interface IWriteBoardHandler extends IParseBoardHandler {
  /**
   * Writes a community board command into the client.
   *
   * @param player the player
   * @param arg1 the first argument
   * @param arg2 the second argument
   * @param arg3 the third argument
   * @param arg4 the fourth argument
   * @param arg5 the fifth argument
   * @return
   */
  boolean writeCommunityBoardCommand(
      L2PcInstance player, String arg1, String arg2, String arg3, String arg4, String arg5);
}
