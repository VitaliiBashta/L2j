
package com.l2jserver.datapack.handlers.usercommandhandlers;

import com.l2jserver.gameserver.handler.IUserCommandHandler;
import com.l2jserver.gameserver.model.L2CommandChannel;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExMultiPartyCommandChannelInfo;
import org.springframework.stereotype.Service;

@Service
public class ChannelInfo implements IUserCommandHandler {
	private static final int[] COMMAND_IDS = {
		97
	};
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar) {
		if (id != COMMAND_IDS[0]) {
			return false;
		}
		
		if ((activeChar.getParty() == null) || (activeChar.getParty().getCommandChannel() == null)) {
			return false;
		}
		
		final L2CommandChannel channel = activeChar.getParty().getCommandChannel();
		activeChar.sendPacket(new ExMultiPartyCommandChannelInfo(channel));
		return true;
	}
	
	@Override
	public int[] getUserCommandList() {
		return COMMAND_IDS;
	}
}
