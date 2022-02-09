
package com.l2jserver.datapack.handlers.admincommandhandlers;

import java.util.StringTokenizer;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class AdminKick implements IAdminCommandHandler {
	private static final String[] ADMIN_COMMANDS = {
		"admin_kick",
		"admin_kick_non_gm"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		if (command.startsWith("admin_kick")) {
			StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1) {
				st.nextToken();
				String player = st.nextToken();
				L2PcInstance plyr = L2World.getInstance().getPlayer(player);
				if (plyr != null) {
					plyr.logout();
					activeChar.sendMessage("You kicked " + plyr.getName() + " from the game.");
				}
			}
		}
		if (command.startsWith("admin_kick_non_gm")) {
			int counter = 0;
			for (L2PcInstance player : L2World.getInstance().getPlayers()) {
				if (!player.isGM()) {
					counter++;
					player.logout();
				}
			}
			activeChar.sendMessage("Kicked " + counter + " players");
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}
