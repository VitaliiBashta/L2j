package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.PetitionManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import org.springframework.stereotype.Service;

/** This class handles commands for GMs to respond to petitions. */
@Service
public class AdminPetition implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {
    "admin_view_petitions",
    "admin_view_petition",
    "admin_accept_petition",
    "admin_reject_petition",
    "admin_reset_petitions",
    "admin_force_peti"
  };

  private final PetitionManager petitionManager;

  public AdminPetition(PetitionManager petitionManager) {
    this.petitionManager = petitionManager;
  }

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    int petitionId = -1;

    petitionId = Integer.parseInt(command.split(" ")[1]);

    if (command.equals("admin_view_petitions")) {
      petitionManager.sendPendingPetitionList(activeChar);
    } else if (command.startsWith("admin_view_petition")) {
      petitionManager.viewPetition(activeChar, petitionId);
    } else if (command.startsWith("admin_accept_petition")) {
      if (petitionManager.isPlayerInConsultation(activeChar)) {
        activeChar.sendPacket(SystemMessageId.ONLY_ONE_ACTIVE_PETITION_AT_TIME);
        return true;
      }

      if (petitionManager.isPetitionInProcess(petitionId)) {
        activeChar.sendPacket(SystemMessageId.PETITION_UNDER_PROCESS);
        return true;
      }

      if (!petitionManager.acceptPetition(activeChar, petitionId)) {
        activeChar.sendPacket(SystemMessageId.NOT_UNDER_PETITION_CONSULTATION);
      }
    } else if (command.startsWith("admin_reject_petition")) {
      if (!petitionManager.rejectPetition(activeChar, petitionId)) {
        activeChar.sendPacket(SystemMessageId.FAILED_CANCEL_PETITION_TRY_LATER);
      }
      petitionManager.sendPendingPetitionList(activeChar);
    } else if (command.equals("admin_reset_petitions")) {
      if (petitionManager.isPetitionInProcess()) {
        activeChar.sendPacket(SystemMessageId.PETITION_UNDER_PROCESS);
        return false;
      }
      petitionManager.clearPendingPetitions();
      petitionManager.sendPendingPetitionList(activeChar);
    } else if (command.startsWith("admin_force_peti")) {
      try {
        L2Object targetChar = activeChar.getTarget();
        if (!(targetChar instanceof L2PcInstance targetPlayer)) {
          activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
          return false;
        }

        String val = command.substring(15);

        petitionId = petitionManager.submitPetition(targetPlayer, val, 9);
        petitionManager.acceptPetition(activeChar, petitionId);
      } catch (StringIndexOutOfBoundsException e) {
        activeChar.sendMessage("Usage: //force_peti text");
        return false;
      }
    }
    return true;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }
}
