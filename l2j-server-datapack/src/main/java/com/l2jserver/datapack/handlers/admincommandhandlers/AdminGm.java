
package com.l2jserver.datapack.handlers.admincommandhandlers;

import java.util.logging.Logger;

import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class handles following admin commands: - gm = turns gm mode off
 * @version $Revision: 1.2.4.4 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminGm implements IAdminCommandHandler {
	private static Logger _log = Logger.getLogger(AdminGm.class.getName());
	private static final String[] ADMIN_COMMANDS = {
		"admin_gm"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		if (command.equals("admin_gm") && activeChar.isGM()) {
			AdminData.getInstance().deleteGm(activeChar);
			activeChar.setAccessLevel(0);
			activeChar.sendMessage("You no longer have GM status.");
			_log.info("GM: " + activeChar.getName() + "(" + activeChar.getObjectId() + ") turned his GM status off");
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}
