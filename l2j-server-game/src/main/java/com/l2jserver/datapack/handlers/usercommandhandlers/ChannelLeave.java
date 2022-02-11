
package com.l2jserver.datapack.handlers.usercommandhandlers;

import com.l2jserver.gameserver.handler.IUserCommandHandler;
import com.l2jserver.gameserver.model.L2CommandChannel;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import org.springframework.stereotype.Service;

@Service
public class ChannelLeave implements IUserCommandHandler {
	private static final int[] COMMAND_IDS = {
		96
	};
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar) {
		if (id != COMMAND_IDS[0]) {
			return false;
		}
		
		if (!activeChar.isInParty() || !activeChar.getParty().isLeader(activeChar)) {
			activeChar.sendPacket(SystemMessageId.ONLY_PARTY_LEADER_CAN_LEAVE_CHANNEL);
			return false;
		}
		
		if (activeChar.getParty().isInCommandChannel()) {
			final L2CommandChannel channel = activeChar.getParty().getCommandChannel();
			final L2Party party = activeChar.getParty();
			channel.removeParty(party);
			party.getLeader().sendPacket(SystemMessageId.LEFT_COMMAND_CHANNEL);
			
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_PARTY_LEFT_COMMAND_CHANNEL);
			sm.addPcName(party.getLeader());
			channel.broadcastPacket(sm);
			return true;
		}
		return false;
		
	}
	
	@Override
	public int[] getUserCommandList() {
		return COMMAND_IDS;
	}
}
