package com.l2jserver.gameserver.network.clientpackets;

import com.l2jserver.gameserver.handler.IUserCommandHandler;
import com.l2jserver.gameserver.handler.UserCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class BypassUserCmd extends L2GameClientPacket {
	private static final String _C__B3_BYPASSUSERCMD = "[C] B3 BypassUserCmd";
	
	private int _command;
	
	@Override
	protected void readImpl() {
		_command = readD();
	}
	
	@Override
	protected void runImpl() {
		L2PcInstance player = getClient().getActiveChar();
		if (player == null) {
			return;
		}
		
		IUserCommandHandler handler = UserCommandHandler.getInstance().getHandler(_command);
		
		if (handler == null) {
			if (player.isGM()) {
				player.sendMessage("User commandID " + _command + " not implemented yet.");
			}
		} else {
			handler.useUserCommand(_command, getClient().getActiveChar());
		}
	}
	
	@Override
	public String getType() {
		return _C__B3_BYPASSUSERCMD;
	}
}
