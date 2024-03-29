package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.logging.Logger;

import static com.l2jserver.gameserver.config.Configuration.general;

/**
 * This class handles following admin commands: - heal = restores HP/MP/CP on target, name or radius
 */
@Service
public class AdminHeal implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {"admin_heal"};
  private static Logger _log = Logger.getLogger(AdminRes.class.getName());

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    if (command.equals("admin_heal")) {
      handleHeal(activeChar);
    } else if (command.startsWith("admin_heal")) {
      try {
        String healTarget = command.substring(11);
        handleHeal(activeChar, healTarget);
      } catch (StringIndexOutOfBoundsException e) {
        if (general().developer()) {
          _log.warning("Heal error: " + e);
        }
        activeChar.sendMessage("Incorrect target/radius specified.");
      }
    }
    return true;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }

  private void handleHeal(L2PcInstance activeChar) {
    handleHeal(activeChar, null);
  }

  private void handleHeal(L2PcInstance activeChar, String player) {

    L2Object obj = activeChar.getTarget();
    if (player != null) {
      L2PcInstance plyr = L2World.getInstance().getPlayer(player);

      if (plyr != null) {
        obj = plyr;
      } else {
        try {
          int radius = Integer.parseInt(player);
          Collection<L2Object> objs = activeChar.getKnownList().getKnownObjects().values();
          for (L2Object object : objs) {
            if (object instanceof L2Character) {
              L2Character character = (L2Character) object;
              character.setCurrentHpMp(character.getMaxHp(), character.getMaxMp());
              if (object instanceof L2PcInstance) {
                character.setCurrentCp(character.getMaxCp());
              }
            }
          }

          activeChar.sendMessage("Healed within " + radius + " unit radius.");
          return;
        } catch (NumberFormatException nbe) {
        }
      }
    }
    if (obj == null) {
      obj = activeChar;
    }
    if (obj instanceof L2Character) {
      L2Character target = (L2Character) obj;
      target.setCurrentHpMp(target.getMaxHp(), target.getMaxMp());
      if (target instanceof L2PcInstance) {
        target.setCurrentCp(target.getMaxCp());
      }
      if (general().debug()) {
        _log.fine(
            "GM: "
                + activeChar.getName()
                + "("
                + activeChar.getObjectId()
                + ") healed character "
                + target.getName());
      }
    } else {
      activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
    }
  }
}
