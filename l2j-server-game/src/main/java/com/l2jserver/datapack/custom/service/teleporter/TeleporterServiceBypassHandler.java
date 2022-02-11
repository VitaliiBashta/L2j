package com.l2jserver.datapack.custom.service.teleporter;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class TeleporterServiceBypassHandler implements IBypassHandler {

  public static final String BYPASS = "TeleporterService";
  private static final String[] BYPASS_LIST = new String[] {BYPASS};

  @Override
  public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
    if ((target == null) || !target.isNpc()) {
      return false;
    }

    TeleporterService.getService()
        .executeCommand(activeChar, (L2Npc) target, command.substring(BYPASS.length()).trim());
    return true;
  }

  @Override
  public String[] getBypassList() {
    return BYPASS_LIST;
  }
}
