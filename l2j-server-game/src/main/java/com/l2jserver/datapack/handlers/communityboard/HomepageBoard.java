package com.l2jserver.datapack.handlers.communityboard;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IParseBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class HomepageBoard implements IParseBoardHandler {
  private static final String[] COMMANDS = {"_bbslink"};

  @Override
  public boolean parseCommunityBoardCommand(String command, L2PcInstance activeChar) {
    CommunityBoardHandler.separateAndSend(
        HtmCache.getInstance()
            .getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/homepage.html"),
        activeChar);
    return true;
  }

  @Override
  public String[] getCommunityBoardCommands() {
    return COMMANDS;
  }
}
