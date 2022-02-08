
package com.l2jserver.datapack.handlers.bypasshandlers;

import java.util.logging.Level;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2OlympiadManagerInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.olympiad.Olympiad;
import com.l2jserver.gameserver.model.olympiad.OlympiadGameManager;
import com.l2jserver.gameserver.model.olympiad.OlympiadGameTask;
import com.l2jserver.gameserver.model.olympiad.OlympiadManager;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExOlympiadMatchList;

/**
 * @author DS
 */
public class OlympiadObservation implements IBypassHandler {
	private static final String[] COMMANDS = {
		"watchmatch",
		"arenachange"
	};
	
	@Override
	public final boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		try {
			final L2Npc olymanager = activeChar.getLastFolkNPC();
			
			if (command.startsWith(COMMANDS[0])) // list
			{
				if (!Olympiad.getInstance().inCompPeriod()) {
					activeChar.sendPacket(SystemMessageId.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
					return false;
				}
				
				activeChar.sendPacket(new ExOlympiadMatchList());
			} else {
				if ((olymanager == null) || !(olymanager instanceof L2OlympiadManagerInstance)) {
					return false;
				}
				
				if (!activeChar.inObserverMode() && !activeChar.isInsideRadius(olymanager, 300, false, false)) {
					return false;
				}
				
				if (OlympiadManager.getInstance().isRegisteredInComp(activeChar)) {
					activeChar.sendPacket(SystemMessageId.WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME);
					return false;
				}
				
				if (!Olympiad.getInstance().inCompPeriod()) {
					activeChar.sendPacket(SystemMessageId.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
					return false;
				}
				
				if (activeChar.isOnEvent()) {
					activeChar.sendMessage("You can not observe games while registered on an event");
					return false;
				}
				
				final int arenaId = Integer.parseInt(command.substring(12).trim());
				final OlympiadGameTask nextArena = OlympiadGameManager.getInstance().getOlympiadTask(arenaId);
				if (nextArena != null) {
					activeChar.enterOlympiadObserverMode(nextArena.getZone().getSpectatorSpawns().get(0), arenaId);
					activeChar.setInstanceId(OlympiadGameManager.getInstance().getOlympiadTask(arenaId).getZone().getInstanceId());
				}
			}
			return true;
			
		} catch (Exception e) {
			_log.log(Level.WARNING, "Exception in " + getClass().getSimpleName(), e);
		}
		return false;
	}
	
	@Override
	public final String[] getBypassList() {
		return COMMANDS;
	}
}
