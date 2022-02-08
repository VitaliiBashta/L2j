
package com.l2jserver.datapack.handlers.admincommandhandlers;

import static com.l2jserver.gameserver.config.Configuration.tvt;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.TvTEvent;
import com.l2jserver.gameserver.model.entity.TvTEventTeleporter;
import com.l2jserver.gameserver.model.entity.TvTManager;

/**
 * @author HorridoJoho
 */
public class AdminTvTEvent implements IAdminCommandHandler {
	private static final String[] ADMIN_COMMANDS = {
		"admin_tvt_add",
		"admin_tvt_remove",
		"admin_tvt_advance"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		if (command.equals("admin_tvt_add")) {
			L2Object target = activeChar.getTarget();
			
			if (!(target instanceof L2PcInstance)) {
				activeChar.sendMessage("You should select a player!");
				return true;
			}
			
			add(activeChar, (L2PcInstance) target);
		} else if (command.equals("admin_tvt_remove")) {
			L2Object target = activeChar.getTarget();
			
			if (!(target instanceof L2PcInstance)) {
				activeChar.sendMessage("You should select a player!");
				return true;
			}
			
			remove(activeChar, (L2PcInstance) target);
		} else if (command.equals("admin_tvt_advance")) {
			TvTManager.getInstance().skipDelay();
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
	
	private void add(L2PcInstance activeChar, L2PcInstance playerInstance) {
		if (playerInstance.isOnEvent()) {
			activeChar.sendMessage("Player already participated in the event!");
			return;
		}
		
		if (!TvTEvent.addParticipant(playerInstance)) {
			activeChar.sendMessage("Player instance could not be added, it seems to be null!");
			return;
		}
		
		if (TvTEvent.isStarted()) {
			new TvTEventTeleporter(playerInstance, TvTEvent.getParticipantTeamCoordinates(playerInstance.getObjectId()), true, false);
		}
	}
	
	private void remove(L2PcInstance activeChar, L2PcInstance playerInstance) {
		if (!TvTEvent.removeParticipant(playerInstance.getObjectId())) {
			activeChar.sendMessage("Player is not part of the event!");
			return;
		}
		
		new TvTEventTeleporter(playerInstance, tvt().getParticipationNpcLoc(), true, true);
	}
}
