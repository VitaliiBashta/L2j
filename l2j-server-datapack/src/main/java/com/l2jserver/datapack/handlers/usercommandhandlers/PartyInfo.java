
package com.l2jserver.datapack.handlers.usercommandhandlers;

import com.l2jserver.gameserver.handler.IUserCommandHandler;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Party Info user command.
 * @author Tempy
 */
public class PartyInfo implements IUserCommandHandler {
	private static final int[] COMMAND_IDS = {
		81
	};
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar) {
		if (id != COMMAND_IDS[0]) {
			return false;
		}
		
		activeChar.sendPacket(SystemMessageId.PARTY_INFORMATION);
		if (activeChar.isInParty()) {
			final L2Party party = activeChar.getParty();
			switch (party.getDistributionType()) {
				case FINDERS_KEEPERS:
					activeChar.sendPacket(SystemMessageId.LOOTING_FINDERS_KEEPERS);
					break;
				case RANDOM:
					activeChar.sendPacket(SystemMessageId.LOOTING_RANDOM);
					break;
				case RANDOM_INCLUDING_SPOIL:
					activeChar.sendPacket(SystemMessageId.LOOTING_RANDOM_INCLUDE_SPOIL);
					break;
				case BY_TURN:
					activeChar.sendPacket(SystemMessageId.LOOTING_BY_TURN);
					break;
				case BY_TURN_INCLUDING_SPOIL:
					activeChar.sendPacket(SystemMessageId.LOOTING_BY_TURN_INCLUDE_SPOIL);
					break;
			}
			
			if (!party.isLeader(activeChar)) {
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PARTY_LEADER_C1);
				sm.addPcName(party.getLeader());
				activeChar.sendPacket(sm);
			}
			activeChar.sendMessage("Members: " + party.getMemberCount() + "/9"); // TODO: Custom?
		}
		activeChar.sendPacket(SystemMessageId.FRIEND_LIST_FOOTER);
		return true;
	}
	
	@Override
	public int[] getUserCommandList() {
		return COMMAND_IDS;
	}
}
