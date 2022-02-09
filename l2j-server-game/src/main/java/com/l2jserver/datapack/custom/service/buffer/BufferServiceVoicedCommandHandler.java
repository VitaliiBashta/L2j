
package com.l2jserver.datapack.custom.service.buffer;

import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Buffer service voiced command handler.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public final class BufferServiceVoicedCommandHandler implements IVoicedCommandHandler {
	
	private static final String[] COMMANDS = new String[] {
		Configuration.bufferService().getVoicedCommand()
	};
	
	private BufferServiceVoicedCommandHandler() {
		// Do nothing.
	}
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params) {
		BufferService.getInstance().executeCommand(activeChar, null, params);
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList() {
		return COMMANDS;
	}
	
	public static BufferServiceVoicedCommandHandler getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder {
		protected static final BufferServiceVoicedCommandHandler INSTANCE = new BufferServiceVoicedCommandHandler();
	}
}
