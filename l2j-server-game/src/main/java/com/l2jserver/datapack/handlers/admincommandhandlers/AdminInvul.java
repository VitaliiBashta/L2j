
package com.l2jserver.datapack.handlers.admincommandhandlers;

import static com.l2jserver.gameserver.config.Configuration.general;

import java.util.logging.Logger;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

/**
 * This class handles following admin commands: - invul = turns invulnerability on/off
 * @version $Revision: 1.2.4.4 $ $Date: 2007/07/31 10:06:02 $
 */
@Service
public class AdminInvul implements IAdminCommandHandler {
	private static Logger _log = Logger.getLogger(AdminInvul.class.getName());
	private static final String[] ADMIN_COMMANDS = {
		"admin_invul",
		"admin_setinvul"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		
		if (command.equals("admin_invul")) {
			handleInvul(activeChar);
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		if (command.equals("admin_setinvul")) {
			L2Object target = activeChar.getTarget();
			if (target instanceof L2PcInstance) {
				handleInvul((L2PcInstance) target);
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
	
	private void handleInvul(L2PcInstance activeChar) {
		String text;
		if (activeChar.isInvul()) {
			activeChar.setIsInvul(false);
			text = activeChar.getName() + " is now mortal";
			if (general().debug()) {
				_log.fine("GM: Gm removed invul mode from character " + activeChar.getName() + "(" + activeChar.getObjectId() + ")");
			}
		} else {
			activeChar.setIsInvul(true);
			text = activeChar.getName() + " is now invulnerable";
			if (general().debug()) {
				_log.fine("GM: Gm activated invul mode for character " + activeChar.getName() + "(" + activeChar.getObjectId() + ")");
			}
		}
		activeChar.sendMessage(text);
	}
}
