
package com.l2jserver.datapack.handlers.admincommandhandlers;

import java.util.StringTokenizer;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.Util;

/**
 * Handler provides ability to override server's conditions for admin.
 * @author UnAfraid
 */
public class AdminPcCondOverride implements IAdminCommandHandler {
	private static final String[] COMMANDS = {
		"admin_exceptions",
		"admin_set_exception",
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		StringTokenizer st = new StringTokenizer(command);
		if (st.hasMoreTokens()) {
			switch (st.nextToken())
			// command
			{
				case "admin_exceptions": {
					final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1);
					msg.setFile(activeChar.getHtmlPrefix(), "data/html/admin/cond_override.htm");
					StringBuilder sb = new StringBuilder();
					for (PcCondOverride ex : PcCondOverride.values()) {
						sb.append("<tr><td fixwidth=\"180\">" + ex.getDescription() + ":</td><td><a action=\"bypass -h admin_set_exception " + ex.ordinal() + "\">" + (activeChar.canOverrideCond(ex) ? "Disable" : "Enable") + "</a></td></tr>");
					}
					msg.replace("%cond_table%", sb.toString());
					activeChar.sendPacket(msg);
					break;
				}
				case "admin_set_exception": {
					if (st.hasMoreTokens()) {
						String token = st.nextToken();
						if (Util.isDigit(token)) {
							PcCondOverride ex = PcCondOverride.getCondOverride(Integer.valueOf(token));
							if (ex != null) {
								if (activeChar.canOverrideCond(ex)) {
									activeChar.removeOverridedCond(ex);
									activeChar.sendMessage("You've disabled " + ex.getDescription());
								} else {
									activeChar.addOverrideCond(ex);
									activeChar.sendMessage("You've enabled " + ex.getDescription());
								}
							}
						} else {
							switch (token) {
								case "enable_all": {
									for (PcCondOverride ex : PcCondOverride.values()) {
										if (!activeChar.canOverrideCond(ex)) {
											activeChar.addOverrideCond(ex);
										}
									}
									activeChar.sendMessage("All condition exceptions have been enabled.");
									break;
								}
								case "disable_all": {
									for (PcCondOverride ex : PcCondOverride.values()) {
										if (activeChar.canOverrideCond(ex)) {
											activeChar.removeOverridedCond(ex);
										}
									}
									activeChar.sendMessage("All condition exceptions have been disabled.");
									break;
								}
							}
						}
						useAdminCommand(COMMANDS[0], activeChar);
					}
					break;
				}
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return COMMANDS;
	}
}
