package com.l2jserver.gameserver.handler;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.logging.Logger;

public interface IVoicedCommandHandler {
	Logger _log = Logger.getLogger(IVoicedCommandHandler.class.getName());
	
	/**
	 * this is the worker method that is called when someone uses an admin command.
	 * @param activeChar
	 * @param command
	 * @param params
	 * @return command success
	 */
	boolean useVoicedCommand(String command, L2PcInstance activeChar, String params);
	
	/**
	 * this method is called at initialization to register all the item ids automatically
	 * @return all known itemIds
	 */
	String[] getVoicedCommandList();
}
