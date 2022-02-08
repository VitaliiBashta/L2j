
package com.l2jserver.datapack.handlers.admincommandhandlers;

import static com.l2jserver.gameserver.config.Configuration.geodata;

import java.util.List;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.pathfinding.AbstractNodeLoc;
import com.l2jserver.gameserver.pathfinding.PathFinding;

public class AdminPathNode implements IAdminCommandHandler {
	private static final String[] ADMIN_COMMANDS = {
		"admin_pn_info",
		"admin_show_path",
		"admin_path_debug",
		"admin_show_pn",
		"admin_find_path",
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		if (command.equals("admin_pn_info")) {
			final String[] info = PathFinding.getInstance().getStat();
			if (info == null) {
				activeChar.sendMessage("Not supported");
			} else {
				for (String msg : info) {
					activeChar.sendMessage(msg);
				}
			}
		} else if (command.equals("admin_show_path")) {
			
		} else if (command.equals("admin_path_debug")) {
			
		} else if (command.equals("admin_show_pn")) {
			
		} else if (command.equals("admin_find_path")) {
			if (geodata().getPathFinding() == 0) {
				activeChar.sendMessage("PathFinding is disabled.");
				return true;
			}
			if (activeChar.getTarget() != null) {
				List<AbstractNodeLoc> path = PathFinding.getInstance().findPath(activeChar.getX(), activeChar.getY(), (short) activeChar.getZ(), activeChar.getTarget().getX(), activeChar.getTarget().getY(), (short) activeChar.getTarget().getZ(), activeChar.getInstanceId(), true);
				if (path == null) {
					activeChar.sendMessage("No Route!");
					return true;
				}
				for (AbstractNodeLoc a : path) {
					activeChar.sendMessage("x:" + a.getX() + " y:" + a.getY() + " z:" + a.getZ());
				}
			} else {
				activeChar.sendMessage("No Target!");
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}
