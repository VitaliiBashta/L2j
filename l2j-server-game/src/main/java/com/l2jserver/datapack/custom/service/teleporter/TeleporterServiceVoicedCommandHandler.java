
package com.l2jserver.datapack.custom.service.teleporter;

import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Teleporter service voiced command handler.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public final class TeleporterServiceVoicedCommandHandler implements IVoicedCommandHandler {
	private static final String[] COMMANDS = new String[] {
		Configuration.teleporterService().getVoicedCommand()
	};
	
	private TeleporterServiceVoicedCommandHandler() {
	}
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params) {
		TeleporterService.getService().executeCommand(activeChar, null, params);
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList() {
		return COMMANDS;
	}
	
	static TeleporterServiceVoicedCommandHandler getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder {
		protected static final TeleporterServiceVoicedCommandHandler INSTANCE = new TeleporterServiceVoicedCommandHandler();
	}
}
