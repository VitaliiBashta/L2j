
package com.l2jserver.datapack.gracia.instances.SecretArea;

import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.instancezone.InstanceWorld;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.network.SystemMessageId;
import org.springframework.stereotype.Service;

@Service
public class SecretArea extends Quest {
	protected class SAWorld extends InstanceWorld {
		
	}
	
	private static final int TEMPLATE_ID = 118;
	private static final int GINBY = 32566;
	private static final int LELRIKIA = 32567;
	private static final int ENTER = 0;
	private static final int EXIT = 1;
	private static final Location[] TELEPORTS = {
		new Location(-23758, -8959, -5384),
		new Location(-185057, 242821, 1576)
	};
	
	public SecretArea() {
		super(-1, SecretArea.class.getSimpleName(), "gracia/instances");
		addStartNpc(GINBY);
		addTalkId(GINBY);
		addTalkId(LELRIKIA);
	}
	
	protected void enterInstance(L2PcInstance player) {
		InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
		
		if (world != null) {
			if (world instanceof SAWorld) {
				teleportPlayer(player, TELEPORTS[ENTER], world.getInstanceId());
				return;
			}
			player.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_ANOTHER_INSTANT_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON);
			return;
		}
		
		world = new SAWorld();
		world.setInstanceId(InstanceManager.getInstance().createDynamicInstance("SecretAreaInTheKeucereusFortress.xml"));
		world.setTemplateId(TEMPLATE_ID);
		world.addAllowed(player.getObjectId());
		world.setStatus(0);
		InstanceManager.getInstance().addWorld(world);
		teleportPlayer(player, TELEPORTS[ENTER], world.getInstanceId());
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		if ((npc.getId() == GINBY) && event.equalsIgnoreCase("enter")) {
			enterInstance(player);
			return "32566-01.html";
		} else if ((npc.getId() == LELRIKIA) && event.equalsIgnoreCase("exit")) {
			teleportPlayer(player, TELEPORTS[EXIT], 0);
			return "32567-01.html";
		}
		return htmltext;
	}
}
