package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.RaidBossSpawnManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

/** This class handles following admin commands: - delete = deletes target */
@Service
public class AdminDelete implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {"admin_delete"};

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    if (command.equals("admin_delete")) {
      handleDelete(activeChar);
    }
    return true;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }

  // TODO: add possibility to delete any L2Object (except L2PcInstance)
  private void handleDelete(L2PcInstance activeChar) {
    L2Object obj = activeChar.getTarget();
    if (obj instanceof L2Npc) {
      L2Npc target = (L2Npc) obj;
      target.deleteMe();

      L2Spawn spawn = target.getSpawn();
      if (spawn != null) {
        spawn.stopRespawn();

        if (RaidBossSpawnManager.getInstance().isDefined(spawn.getId())) {
          RaidBossSpawnManager.getInstance().deleteSpawn(spawn, true);
        } else {
          SpawnTable.getInstance().deleteSpawn(spawn, true);
        }
      }

      activeChar.sendMessage("Deleted " + target.getName() + " from " + target.getObjectId() + ".");
    } else {
      activeChar.sendMessage("Incorrect target.");
    }
  }
}
