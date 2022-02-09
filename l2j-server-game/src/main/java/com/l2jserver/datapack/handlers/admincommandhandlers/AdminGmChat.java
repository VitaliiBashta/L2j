package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import org.springframework.stereotype.Service;

/**
 * This class handles following admin commands: - gmchat text = sends text to all online GM's -
 * gmchat_menu text = same as gmchat, displays the admin panel after chat
 */
@Service
public class AdminGmChat implements IAdminCommandHandler {

  private static final String[] ADMIN_COMMANDS = {
    "admin_gmchat", "admin_snoop", "admin_gmchat_menu"
  };

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    if (command.startsWith("admin_gmchat")) {
      handleGmChat(command, activeChar);
    } else if (command.startsWith("admin_snoop")) {
      snoop(command, activeChar);
    }
    if (command.startsWith("admin_gmchat_menu")) {
      AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
    }
    return true;
  }

  /**
   * @param command
   * @param activeChar
   */
  private void snoop(String command, L2PcInstance activeChar) {
    L2Object target = null;
    if (command.length() > 12) {
      target = L2World.getInstance().getPlayer(command.substring(12));
    }
    if (target == null) {
      target = activeChar.getTarget();
    }

    if (target == null) {
      activeChar.sendPacket(SystemMessageId.SELECT_TARGET);
      return;
    }
    if (!(target instanceof L2PcInstance)) {
      activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
      return;
    }
    L2PcInstance player = (L2PcInstance) target;
    player.addSnooper(activeChar);
    activeChar.addSnooped(player);
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }

  /**
   * @param command
   * @param activeChar
   */
  private void handleGmChat(String command, L2PcInstance activeChar) {
    try {
      int offset = 0;
      String text;
      if (command.startsWith("admin_gmchat_menu")) {
        offset = 18;
      } else {
        offset = 13;
      }
      text = command.substring(offset);
      CreatureSay cs = new CreatureSay(0, Say2.ALLIANCE, activeChar.getName(), text);
      AdminData.getInstance().broadcastToGMs(cs);
    } catch (StringIndexOutOfBoundsException e) {
      // Who cares?
    }
  }
}
