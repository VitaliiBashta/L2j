
package com.l2jserver.datapack.handlers.actionshifthandlers;

import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.handler.IActionShiftHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.StringUtil;

public class L2ItemInstanceActionShift implements IActionShiftHandler {
	@Override
	public boolean action(L2PcInstance activeChar, L2Object target, boolean interact) {
		if (activeChar.getAccessLevel().isGm()) {
			final NpcHtmlMessage html = new NpcHtmlMessage(StringUtil.concat("<html><body><center><font color=\"LEVEL\">Item Info</font></center><br><table border=0>", "<tr><td>Object ID: </td><td>", String.valueOf(target.getObjectId()), "</td></tr><tr><td>Item ID: </td><td>", String.valueOf(target.getId()), "</td></tr><tr><td>Owner ID: </td><td>", String.valueOf(((L2ItemInstance) target).getOwnerId()), "</td></tr><tr><td>Location: </td><td>", String.valueOf(((L2ItemInstance) target).getLocation()), "</td></tr><tr><td><br></td></tr><tr><td>Class: </td><td>", target.getClass().getSimpleName(), "</td></tr></table></body></html>"));
			activeChar.sendPacket(html);
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType() {
		return InstanceType.L2ItemInstance;
	}
}
