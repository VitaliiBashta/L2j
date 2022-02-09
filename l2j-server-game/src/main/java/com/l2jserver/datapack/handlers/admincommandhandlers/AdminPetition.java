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

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    int petitionId = -1;

    try {
      petitionId = Integer.parseInt(command.split(" ")[1]);
    } catch (Exception e) {
    }

    if (command.equals("admin_view_petitions")) {
      PetitionManager.getInstance().sendPendingPetitionList(activeChar);
    } else if (command.startsWith("admin_view_petition")) {
      PetitionManager.getInstance().viewPetition(activeChar, petitionId);
    } else if (command.startsWith("admin_accept_petition")) {
      if (PetitionManager.getInstance().isPlayerInConsultation(activeChar)) {
        activeChar.sendPacket(SystemMessageId.ONLY_ONE_ACTIVE_PETITION_AT_TIME);
        return true;
      }

      if (PetitionManager.getInstance().isPetitionInProcess(petitionId)) {
        activeChar.sendPacket(SystemMessageId.PETITION_UNDER_PROCESS);
        return true;
      }

      if (!PetitionManager.getInstance().acceptPetition(activeChar, petitionId)) {
        activeChar.sendPacket(SystemMessageId.NOT_UNDER_PETITION_CONSULTATION);
      }
    } else if (command.startsWith("admin_reject_petition")) {
      if (!PetitionManager.getInstance().rejectPetition(activeChar, petitionId)) {
        activeChar.sendPacket(SystemMessageId.FAILED_CANCEL_PETITION_TRY_LATER);
      }
      PetitionManager.getInstance().sendPendingPetitionList(activeChar);
    } else if (command.equals("admin_reset_petitions")) {
      if (PetitionManager.getInstance().isPetitionInProcess()) {
        activeChar.sendPacket(SystemMessageId.PETITION_UNDER_PROCESS);
        return false;
      }
      PetitionManager.getInstance().clearPendingPetitions();
      PetitionManager.getInstance().sendPendingPetitionList(activeChar);
    } else if (command.startsWith("admin_force_peti")) {
      try {
        L2Object targetChar = activeChar.getTarget();
        if ((targetChar == null) || !(targetChar instanceof L2PcInstance)) {
          activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
          return false;
        }
        L2PcInstance targetPlayer = (L2PcInstance) targetChar;

        String val = command.substring(15);

        petitionId = PetitionManager.getInstance().submitPetition(targetPlayer, val, 9);
        PetitionManager.getInstance().acceptPetition(activeChar, petitionId);
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
