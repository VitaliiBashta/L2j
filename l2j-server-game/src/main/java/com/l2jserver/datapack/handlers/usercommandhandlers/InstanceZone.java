
package com.l2jserver.datapack.handlers.usercommandhandlers;

import com.l2jserver.gameserver.handler.IUserCommandHandler;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.instancezone.InstanceWorld;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InstanceZone implements IUserCommandHandler {
	private static final int[] COMMAND_IDS = {
		114
	};
	
	@Override
	public int[] getUserCommandList() {
		return COMMAND_IDS;
	}
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar) {
		if (id != COMMAND_IDS[0]) {
			return false;
		}
		
		final InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(activeChar);
		if ((world != null) && (world.getTemplateId() >= 0)) {
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.INSTANT_ZONE_CURRENTLY_INUSE_S1);
			sm.addInstanceName(world.getTemplateId());
			activeChar.sendPacket(sm);
		}
		
		final Map<Integer, Long> instanceTimes = InstanceManager.getInstance().getAllInstanceTimes(activeChar.getObjectId());
		boolean firstMessage = true;
		if (instanceTimes != null) {
			for (int instanceId : instanceTimes.keySet()) {
				long remainingTime = (instanceTimes.get(instanceId) - System.currentTimeMillis()) / 1000;
				if (remainingTime > 60) {
					if (firstMessage) {
						firstMessage = false;
						activeChar.sendPacket(SystemMessageId.INSTANCE_ZONE_TIME_LIMIT);
					}
					int hours = (int) (remainingTime / 3600);
					int minutes = (int) ((remainingTime % 3600) / 60);
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.AVAILABLE_AFTER_S1_S2_HOURS_S3_MINUTES);
					sm.addInstanceName(instanceId);
					sm.addInt(hours);
					sm.addInt(minutes);
					activeChar.sendPacket(sm);
				} else {
					InstanceManager.getInstance().deleteInstanceTime(activeChar.getObjectId(), instanceId);
				}
			}
		}
		if (firstMessage) {
			activeChar.sendPacket(SystemMessageId.NO_INSTANCEZONE_TIME_LIMIT);
		}
		return true;
	}
}
