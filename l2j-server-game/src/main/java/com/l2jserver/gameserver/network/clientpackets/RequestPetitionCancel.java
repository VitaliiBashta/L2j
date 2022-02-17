package com.l2jserver.gameserver.network.clientpackets;

import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.instancemanager.PetitionManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

import static com.l2jserver.gameserver.config.Configuration.character;

public final class RequestPetitionCancel extends L2GameClientPacket {
	private static final String _C__8A_REQUEST_PETITIONCANCEL = "[C] 8A RequestPetitionCancel";

  // private int _unknown;

  private final PetitionManager petitionManager;
  private final AdminData adminData;

  public RequestPetitionCancel(PetitionManager petitionManager, AdminData adminData) {
    this.petitionManager = petitionManager;
    this.adminData = adminData;
  }

	@Override
	protected void readImpl() {
		// _unknown = readD(); This is pretty much a trigger packet.
	}
	
	@Override
	protected void runImpl() {
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null) {
			return;
		}

    if (petitionManager.isPlayerInConsultation(activeChar)) {
			if (activeChar.isGM()) {
        petitionManager.endActivePetition(activeChar);
			} else {
				activeChar.sendPacket(SystemMessageId.PETITION_UNDER_PROCESS);
			}
		} else {
      if (petitionManager.isPlayerPetitionPending(activeChar)) {
        if (petitionManager.cancelActivePetition(activeChar)) {
          int numRemaining =
              character().getMaxPetitionsPerPlayer()
                  - petitionManager.getPlayerTotalPetitionCount(activeChar);

					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PETITION_CANCELED_SUBMIT_S1_MORE_TODAY);
					sm.addString(String.valueOf(numRemaining));
					activeChar.sendPacket(sm);
					
					// Notify all GMs that the player's pending petition has been cancelled.
					String msgContent = activeChar.getName() + " has canceled a pending petition.";
          adminData.broadcastToGMs(
              new CreatureSay(
                  activeChar.getObjectId(), Say2.HERO_VOICE, "Petition System", msgContent));
				} else {
					activeChar.sendPacket(SystemMessageId.FAILED_CANCEL_PETITION_TRY_LATER);
				}
			} else {
				activeChar.sendPacket(SystemMessageId.PETITION_NOT_SUBMITTED);
			}
		}
	}
	
	@Override
	public String getType() {
		return _C__8A_REQUEST_PETITIONCANCEL;
	}
}
