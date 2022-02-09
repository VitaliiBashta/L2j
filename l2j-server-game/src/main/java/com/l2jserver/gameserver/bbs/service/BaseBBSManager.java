package com.l2jserver.gameserver.bbs.service;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ShowBoard;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseBBSManager {
  public abstract void parsecmd(String command, L2PcInstance activeChar);

  public abstract void parsewrite(
      String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar);

  protected void send1001(String html, L2PcInstance player) {
    if (html.length() < 8192) {
      player.sendPacket(new ShowBoard(html, "1001"));
    }
  }

  protected void send1002(L2PcInstance player) {
    send1002(player, " ", " ", "0");
  }

  protected void send1002(L2PcInstance activeChar, String string, String string2, String string3) {
    List<String> arg = new ArrayList<>();
    arg.add("0");
    arg.add("0");
    arg.add("0");
    arg.add("0");
    arg.add("0");
    arg.add("0");
    arg.add(activeChar.getName());
    arg.add(Integer.toString(activeChar.getObjectId()));
    arg.add(activeChar.getAccountName());
    arg.add("9");
    arg.add(string2); // subject?
    arg.add(string2); // subject?
    arg.add(string); // text
    arg.add(string3); // date?
    arg.add(string3); // date?
    arg.add("0");
    arg.add("0");
    activeChar.sendPacket(new ShowBoard(arg));
  }
}
