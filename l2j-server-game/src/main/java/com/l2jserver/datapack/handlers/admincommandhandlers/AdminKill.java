package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2ControllableMobInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import org.springframework.stereotype.Service;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import static com.l2jserver.gameserver.config.Configuration.customs;
import static com.l2jserver.gameserver.config.Configuration.general;

/**
 * This class handles following admin commands: - kill = kills target L2Character - kill_monster =
 * kills target non-player - kill <radius> = If radius is specified, then ALL players only in that
 * radius will be killed. - kill_monster <radius> = If radius is specified, then ALL non-players
 * only in that radius will be killed.
 */
@Service
public class AdminKill implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {"admin_kill", "admin_kill_monster"};
  private static final Logger _log = Logger.getLogger(AdminKill.class.getName());

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    if (command.startsWith("admin_kill")) {
      StringTokenizer st = new StringTokenizer(command, " ");
      st.nextToken(); // skip command

      if (st.hasMoreTokens()) {
        String firstParam = st.nextToken();
        L2PcInstance plyr = L2World.getInstance().getPlayer(firstParam);
        if (plyr != null) {
          if (st.hasMoreTokens()) {
            try {
              int radius = Integer.parseInt(st.nextToken());
              for (L2Character knownChar : plyr.getKnownList().getKnownCharactersInRadius(radius)) {
                if ((knownChar instanceof L2ControllableMobInstance) || (knownChar == activeChar)) {
                  continue;
                }

                kill(activeChar, knownChar);
              }

              activeChar.sendMessage("Killed all characters within a " + radius + " unit radius.");
              return true;
            } catch (NumberFormatException e) {
              activeChar.sendMessage("Invalid radius.");
              return false;
            }
          }
          kill(activeChar, plyr);
        } else {
          try {
            int radius = Integer.parseInt(firstParam);

            for (L2Character knownChar :
                activeChar.getKnownList().getKnownCharactersInRadius(radius)) {
              if ((knownChar instanceof L2ControllableMobInstance) || (knownChar == activeChar)) {
                continue;
              }
              kill(activeChar, knownChar);
            }

            activeChar.sendMessage("Killed all characters within a " + radius + " unit radius.");
            return true;
          } catch (NumberFormatException e) {
            activeChar.sendMessage("Usage: //kill <player_name | radius>");
            return false;
          }
        }
      } else {
        L2Object obj = activeChar.getTarget();
        if ((obj instanceof L2ControllableMobInstance) || !(obj instanceof L2Character)) {
          activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
        } else {
          kill(activeChar, (L2Character) obj);
        }
      }
    }
    return true;
  }

  private void kill(L2PcInstance activeChar, L2Character target) {
    if (target instanceof L2PcInstance) {
      if (!((L2PcInstance) target).isGM()) {
        target.stopAllEffects(); // e.g. invincibility effect
      }
      target.reduceCurrentHp(target.getMaxHp() + target.getMaxCp() + 1, activeChar, null);
    } else if (customs().championEnable() && target.isChampion()) {
      target.reduceCurrentHp((target.getMaxHp() * customs().getChampionHp()) + 1, activeChar, null);
    } else {
      boolean targetIsInvul = false;
      if (target.isInvul()) {
        targetIsInvul = true;
        target.setIsInvul(false);
      }

      target.reduceCurrentHp(target.getMaxHp() + 1, activeChar, null);

      if (targetIsInvul) {
        target.setIsInvul(true);
      }
    }
    if (general().debug()) {
      _log.fine(
          "GM: "
              + activeChar.getName()
              + "("
              + activeChar.getObjectId()
              + ")"
              + " killed character "
              + target.getObjectId());
    }
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }
}
