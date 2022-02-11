package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2AccessLevel;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.l2jserver.gameserver.config.Configuration.general;

/** Change access level command handler. */
@Service
public class AdminChangeAccessLevel implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {"admin_changelvl"};

  private void onlineChange(L2PcInstance activeChar, L2PcInstance player, int lvl) {
    if (lvl >= 0) {
      if (AdminData.getInstance().hasAccessLevel(lvl)) {
        final L2AccessLevel acccessLevel = AdminData.getInstance().getAccessLevel(lvl);
        player.setAccessLevel(lvl);
        player.sendMessage(
            "Your access level has been changed to "
                + acccessLevel.getName()
                + " ("
                + acccessLevel.getLevel()
                + ").");
        activeChar.sendMessage(
            player.getName()
                + "'s access level has been changed to "
                + acccessLevel.getName()
                + " ("
                + acccessLevel.getLevel()
                + ").");
      } else {
        activeChar.sendMessage(
            "You are trying to set unexisting access level: "
                + lvl
                + " please try again with a valid one!");
      }
    } else {
      player.setAccessLevel(lvl);
      player.sendMessage("Your character has been banned. Bye.");
      player.logout();
    }
  }

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    String[] parts = command.split(" ");
    if (parts.length == 2) {
      try {
        int lvl = Integer.parseInt(parts[1]);
        if (activeChar.getTarget() instanceof L2PcInstance) {
          onlineChange(activeChar, (L2PcInstance) activeChar.getTarget(), lvl);
        } else {
          activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
        }
      } catch (Exception e) {
        activeChar.sendMessage("Usage: //changelvl <target_new_level> | <player_name> <new_level>");
      }
    } else if (parts.length == 3) {
      String name = parts[1];
      int lvl = Integer.parseInt(parts[2]);
      L2PcInstance player = L2World.getInstance().getPlayer(name);
      if (player != null) {
        onlineChange(activeChar, player, lvl);
      } else {
        try (Connection con = ConnectionFactory.getInstance().getConnection();
            PreparedStatement ps =
                con.prepareStatement("UPDATE characters SET accesslevel=? WHERE char_name=?")) {
          ps.setInt(1, lvl);
          ps.setString(2, name);
          ps.execute();

          if (ps.getUpdateCount() == 0) {
            activeChar.sendMessage("Character not found or access level unaltered.");
          } else {
            activeChar.sendMessage("Character's access level is now set to " + lvl);
          }
        } catch (SQLException se) {
          activeChar.sendMessage("SQLException while changing character's access level");
          if (general().debug()) {
            se.printStackTrace();
          }
        }
      }
    }
    return true;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }
}
