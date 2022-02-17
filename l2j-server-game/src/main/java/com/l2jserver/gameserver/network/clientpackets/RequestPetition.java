package com.l2jserver.gameserver.network.clientpackets;

import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.instancemanager.PetitionManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

import static com.l2jserver.gameserver.config.Configuration.character;

public final class RequestPetition extends L2GameClientPacket {
	private static final String _C__89_RequestPetition = "[C] 89 RequestPetition";
	
	private String _content;
	private int _type; // 1 = on : 0 = off;
  private final AdminData adminData;
  private final PetitionManager petitionManager;

  public RequestPetition(AdminData adminData, PetitionManager petitionManager) {
    this.adminData = adminData;
    this.petitionManager = petitionManager;
  }

	@Override
	protected void readImpl() {
		_content = readS();
		_type = readD();
	}
	
	@Override
	protected void runImpl() {
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null) {
			return;
		}

    if (!adminData.isGmOnline(false)) {
			activeChar.sendPacket(SystemMessageId.NO_GM_PROVIDING_SERVICE_NOW);
			return;
		}

    if (!petitionManager.isPetitioningAllowed()) {
			activeChar.sendPacket(SystemMessageId.GAME_CLIENT_UNABLE_TO_CONNECT_TO_PETITION_SERVER);
			return;
		}

    if (petitionManager.isPlayerPetitionPending(activeChar)) {
			activeChar.sendPacket(SystemMessageId.ONLY_ONE_ACTIVE_PETITION_AT_TIME);
			return;
		}

    if (petitionManager.getPendingPetitionCount() == character().getMaxPetitionsPending()) {
			activeChar.sendPacket(SystemMessageId.PETITION_SYSTEM_CURRENT_UNAVAILABLE);
			return;
		}

    int totalPetitions = petitionManager.getPlayerTotalPetitionCount(activeChar) + 1;

		if (totalPetitions > character().getMaxPetitionsPerPlayer()) {
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.WE_HAVE_RECEIVED_S1_PETITIONS_TODAY);
			sm.addInt(totalPetitions);
			activeChar.sendPacket(sm);
			return;
		}
		
		if (_content.length() > 255) {
			activeChar.sendPacket(SystemMessageId.PETITION_MAX_CHARS_255);
			return;
		}

    int petitionId = petitionManager.submitPetition(activeChar, _content, _type);

		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PETITION_ACCEPTED_RECENT_NO_S1);
		sm.addInt(petitionId);
		activeChar.sendPacket(sm);
		
		sm = SystemMessage.getSystemMessage(SystemMessageId.SUBMITTED_YOU_S1_TH_PETITION_S2_LEFT);
		sm.addInt(totalPetitions);
		sm.addInt(character().getMaxPetitionsPerPlayer() - totalPetitions);
		activeChar.sendPacket(sm);
		
		sm = SystemMessage.getSystemMessage(SystemMessageId.S1_PETITION_ON_WAITING_LIST);
    sm.addInt(petitionManager.getPendingPetitionCount());
		activeChar.sendPacket(sm);
	}
	
	@Override
	public String getType() {
		return _C__89_RequestPetition;
	}
}
