
package com.l2jserver.datapack.handlers.usercommandhandlers;

import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.handler.IUserCommandHandler;
import com.l2jserver.gameserver.instancemanager.MapRegionManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.type.L2RespawnZone;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import org.springframework.stereotype.Service;

@Service
public class Loc implements IUserCommandHandler {
	private static final int[] COMMAND_IDS = {
		0
	};
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar) {
		int region;
		L2RespawnZone zone = ZoneManager.getInstance().getZone(activeChar, L2RespawnZone.class);
		if (zone != null) {
			region = MapRegionManager.getInstance().getRestartRegion(activeChar, zone.getAllRespawnPoints().get(Race.HUMAN)).getLocId();
		} else {
			region = MapRegionManager.getInstance().getMapRegionLocId(activeChar);
		}
		
		SystemMessage sm;
		if (region > 0) {
			sm = SystemMessage.getSystemMessage(region);
			if (sm.getSystemMessageId().getParamCount() == 3) {
				sm.addInt(activeChar.getX());
				sm.addInt(activeChar.getY());
				sm.addInt(activeChar.getZ());
			}
		} else {
			sm = SystemMessage.getSystemMessage(SystemMessageId.CURRENT_LOCATION_S1);
			sm.addString(activeChar.getX() + ", " + activeChar.getY() + ", " + activeChar.getZ());
		}
		activeChar.sendPacket(sm);
		return true;
	}
	
	@Override
	public int[] getUserCommandList() {
		return COMMAND_IDS;
	}
}
