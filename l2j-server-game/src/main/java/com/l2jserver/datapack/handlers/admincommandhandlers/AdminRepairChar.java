package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.data.sql.impl.CharNameTable;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

/** This class handles following admin commands: - delete = deletes target */
@Service
public class AdminRepairChar implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {"admin_restore", "admin_repair"};
  private static Logger _log = Logger.getLogger(AdminRepairChar.class.getName());

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    handleRepair(command);
    return true;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }

  private void handleRepair(String command) {
    String[] parts = command.split(" ");
    if (parts.length != 2) {
      return;
    }

    final String playerName = parts[1];
    String cmd = "UPDATE characters SET x=-84318, y=244579, z=-3730 WHERE char_name=?";
    try (Connection con = ConnectionFactory.getInstance().getConnection()) {
      try (PreparedStatement ps = con.prepareStatement(cmd)) {
        ps.setString(1, playerName);
        ps.execute();
      }

      final int objId = CharNameTable.getInstance().getIdByName(playerName);
      if (objId != 0) {
        // Delete player's shortcuts.
        try (PreparedStatement ps =
            con.prepareStatement("DELETE FROM character_shortcuts WHERE charId=?")) {
          ps.setInt(1, objId);
          ps.execute();
        }
        // Move all items to the inventory.
        try (PreparedStatement ps =
            con.prepareStatement("UPDATE items SET loc=\"INVENTORY\" WHERE owner_id=?")) {
          ps.setInt(1, objId);
          ps.execute();
        }
      }
    } catch (Exception e) {
      _log.log(Level.WARNING, "Could not repair char:", e);
    }
  }
}
