package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.MonsterRace;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.enums.audio.Music;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.DeleteObject;
import com.l2jserver.gameserver.network.serverpackets.MonRaceInfo;
import com.l2jserver.gameserver.network.serverpackets.PlaySound;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import org.springframework.stereotype.Service;

/** This class handles following admin commands: - invul = turns invulnerability on/off */
@Service
public class AdminMonsterRace implements IAdminCommandHandler {

  private static final String[] ADMIN_COMMANDS = {"admin_mons"};

  protected static int state = -1;

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    if (command.equalsIgnoreCase("admin_mons")) {
      handleSendPacket(activeChar);
    }
    return true;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }

  private void handleSendPacket(L2PcInstance activeChar) {
    /*
     * -1 0 to initialize the race 0 15322 to start race 13765 -1 in middle of race -1 0 to end the race 8003 to 8027
     */

    int[][] codes = {
      {-1, 0},
      {0, 15322},
      {13765, -1},
      {-1, 0}
    };
    MonsterRace race = MonsterRace.getInstance();

    if (state == -1) {
      state++;
      race.newRace();
      race.newSpeeds();
      MonRaceInfo spk =
          new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds());
      activeChar.sendPacket(spk);
      activeChar.broadcastPacket(spk);
    } else if (state == 0) {
      state++;
      SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_RACE_START);
      sm.addInt(0);
      activeChar.sendPacket(sm);
      PlaySound SRace = Music.S_RACE.getPacket();
      activeChar.sendPacket(SRace);
      activeChar.broadcastPacket(SRace);
      PlaySound SRace2 = Sound.ITEMSOUND2_RACE_START.getPacket();
      activeChar.sendPacket(SRace2);
      activeChar.broadcastPacket(SRace2);
      MonRaceInfo spk =
          new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds());
      activeChar.sendPacket(spk);
      activeChar.broadcastPacket(spk);

      ThreadPoolManager.getInstance().scheduleGeneral(new RunRace(codes, activeChar), 5000);
    }
  }

  private static class RunEnd implements Runnable {
    private final L2PcInstance activeChar;

    public RunEnd(L2PcInstance pActiveChar) {
      activeChar = pActiveChar;
    }

    @Override
    public void run() {
      DeleteObject obj = null;
      for (int i = 0; i < 8; i++) {
        obj = new DeleteObject(MonsterRace.getInstance().getMonsters()[i]);
        activeChar.sendPacket(obj);
        activeChar.broadcastPacket(obj);
      }
      state = -1;
    }
  }

  class RunRace implements Runnable {

    private final int[][] codes;
    private final L2PcInstance activeChar;

    public RunRace(int[][] pCodes, L2PcInstance pActiveChar) {
      codes = pCodes;
      activeChar = pActiveChar;
    }

    @Override
    public void run() {
      // int[][] speeds1 = MonsterRace.getInstance().getSpeeds();
      // MonsterRace.getInstance().newSpeeds();
      // int[][] speeds2 = MonsterRace.getInstance().getSpeeds();
      /*
       * int[] speed = new int[8]; for (int i=0; i<8; i++) { for (int j=0; j<20; j++) { //_log.info("Adding "+speeds1[i][j] +" and "+ speeds2[i][j]); speed[i] += (speeds1[i][j]*1);// + (speeds2[i][j]*1); } _log.info("Total speed for "+(i+1)+" = "+speed[i]); }
       */

      MonRaceInfo spk =
          new MonRaceInfo(
              codes[2][0],
              codes[2][1],
              MonsterRace.getInstance().getMonsters(),
              MonsterRace.getInstance().getSpeeds());
      activeChar.sendPacket(spk);
      activeChar.broadcastPacket(spk);
      ThreadPoolManager.getInstance().scheduleGeneral(new RunEnd(activeChar), 30000);
    }
  }
}
