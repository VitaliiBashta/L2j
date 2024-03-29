
package com.l2jserver.datapack.handlers.actionshifthandlers;

import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.handler.IActionShiftHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.StaticObject;
import org.springframework.stereotype.Service;

@Service
public class L2DoorInstanceActionShift implements IActionShiftHandler {
	@Override
	public boolean action(L2PcInstance activeChar, L2Object target, boolean interact) {
		if (activeChar.getAccessLevel().isGm()) {
			activeChar.setTarget(target);
			L2DoorInstance door = (L2DoorInstance) target;
			activeChar.sendPacket(new StaticObject(door, activeChar.isGM()));
			
			final NpcHtmlMessage html = new NpcHtmlMessage();
			html.setFile(activeChar.getHtmlPrefix(), "data/html/admin/doorinfo.htm");
			html.replace("%class%", target.getClass().getSimpleName());
			html.replace("%hp%", String.valueOf((int) door.getCurrentHp()));
			html.replace("%hpmax%", String.valueOf(door.getMaxHp()));
			html.replace("%objid%", String.valueOf(target.getObjectId()));
			html.replace("%doorid%", String.valueOf(door.getId()));
			
			html.replace("%minx%", String.valueOf(door.getX(0)));
			html.replace("%miny%", String.valueOf(door.getY(0)));
			html.replace("%minz%", String.valueOf(door.getZMin()));
			
			html.replace("%maxx%", String.valueOf(door.getX(2)));
			html.replace("%maxy%", String.valueOf(door.getY(2)));
			html.replace("%maxz%", String.valueOf(door.getZMax()));
			html.replace("%unlock%", door.isOpenableBySkill() ? "<font color=00FF00>YES<font>" : "<font color=FF0000>NO</font>");
			
			activeChar.sendPacket(html);
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType() {
		return InstanceType.L2DoorInstance;
	}
}