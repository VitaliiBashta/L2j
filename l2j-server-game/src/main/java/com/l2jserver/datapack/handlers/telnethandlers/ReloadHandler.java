package com.l2jserver.datapack.handlers.telnethandlers;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.sql.impl.TeleportLocationTable;
import com.l2jserver.gameserver.data.xml.impl.MultisellData;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.handler.ITelnetHandler;
import com.l2jserver.gameserver.instancemanager.DayNightSpawnManager;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.instancemanager.RaidBossSpawnManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.L2World;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class ReloadHandler implements ITelnetHandler {
  private final String[] _commands = {"reload"};

  @Override
  public boolean useCommand(String command, PrintWriter _print, Socket _cSocket, int _uptime) {
    if (command.startsWith("reload")) {
      StringTokenizer st = new StringTokenizer(command.substring(7));
      try {
        String type = st.nextToken();

        if (type.equals("multisell")) {
          _print.print("Reloading multisell... ");
          MultisellData.getInstance().load();
          _print.println("done");
        } else if (type.equals("skill")) {
          _print.print("Reloading skills... ");
          SkillData.getInstance().reload();
          _print.println("done");
        } else if (type.equals("npc")) {
          _print.print("Reloading npc templates... ");
          NpcData.getInstance().load();
          QuestManager.getInstance().reloadAllScripts();
          _print.println("done");
        } else if (type.equals("html")) {
          _print.print("Reloading html cache... ");
          HtmCache.getInstance().reload();
          _print.println("done");
        } else if (type.equals("item")) {
          _print.print("Reloading item templates... ");
          ItemTable.getInstance().reload();
          _print.println("done");
        } else if (type.equals("zone")) {
          _print.print("Reloading zone tables... ");
          ZoneManager.getInstance().reload();
          _print.println("done");
        } else if (type.equals("teleports")) {
          _print.print("Reloading telport location table... ");
          TeleportLocationTable.getInstance().reloadAll();
          _print.println("done");
        } else if (type.equals("spawns")) {
          _print.print("Reloading spawns... ");
          RaidBossSpawnManager.getInstance().cleanUp();
          DayNightSpawnManager.getInstance().cleanUp();
          L2World.getInstance().deleteVisibleNpcSpawns();
          NpcData.getInstance().load();
          SpawnTable.getInstance().load();
          RaidBossSpawnManager.getInstance().load();
          _print.println("done\n");
        }
      } catch (Exception e) {
      }
    }
    return false;
  }

  @Override
  public String[] getCommandList() {
    return _commands;
  }
}