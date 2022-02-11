
package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.instancemanager.CastleManorManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.StringUtil;
import com.l2jserver.gameserver.util.Util;
import org.springframework.stereotype.Service;

@Service
public class AdminManor implements IAdminCommandHandler {
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		final CastleManorManager manor = CastleManorManager.getInstance();
		final NpcHtmlMessage msg = new NpcHtmlMessage();
		msg.setFile(activeChar.getHtmlPrefix(), "data/html/admin/manor.htm");
		msg.replace("%status%", manor.getCurrentModeName());
		msg.replace("%change%", manor.getNextModeChange());
		
		final StringBuilder sb = new StringBuilder(3400);
		for (Castle c : CastleManager.getInstance().getCastles()) {
			StringUtil.append(sb, "<tr><td>Name:</td><td><font color=008000>" + c.getName() + "</font></td></tr>");
			StringUtil.append(sb, "<tr><td>Current period cost:</td><td><font color=FF9900>", Util.formatAdena(manor.getManorCost(c.getResidenceId(), false)), " Adena</font></td></tr>");
			StringUtil.append(sb, "<tr><td>Next period cost:</td><td><font color=FF9900>", Util.formatAdena(manor.getManorCost(c.getResidenceId(), true)), " Adena</font></td></tr>");
			StringUtil.append(sb, "<tr><td><font color=808080>--------------------------</font></td><td><font color=808080>--------------------------</font></td></tr>");
		}
		msg.replace("%castleInfo%", sb.toString());
		activeChar.sendPacket(msg);
		
		sb.setLength(0);
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return new String[] {
			"admin_manor"
		};
	}
}