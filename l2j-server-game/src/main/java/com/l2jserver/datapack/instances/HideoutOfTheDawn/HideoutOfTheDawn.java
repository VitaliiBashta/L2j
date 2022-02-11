
package com.l2jserver.datapack.instances.HideoutOfTheDawn;

import com.l2jserver.datapack.instances.AbstractInstance;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.instancezone.InstanceWorld;
import org.springframework.stereotype.Service;

@Service
public class HideoutOfTheDawn extends AbstractInstance {
	protected class HotDWorld extends InstanceWorld {
		
	}
	
	// NPCs
	private static final int WOOD = 32593;
	private static final int JAINA = 32617;
	// Location
	private static final Location WOOD_LOC = new Location(-23758, -8959, -5384);
	private static final Location JAINA_LOC = new Location(147072, 23743, -1984);
	// Misc
	private static final int TEMPLATE_ID = 113;
	
	public HideoutOfTheDawn() {
		super(HideoutOfTheDawn.class.getSimpleName());
		addFirstTalkId(JAINA);
		addStartNpc(WOOD);
		addTalkId(WOOD, JAINA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		switch (event) {
			case "32617-01.html":
			case "32617-02a.html": {
				htmltext = event;
				break;
			}
			case "32617-02.html": {
				player.setInstanceId(0);
				player.teleToLocation(JAINA_LOC, true);
				htmltext = event;
				break;
			}
			case "32593-01.html": {
				enterInstance(player, new HotDWorld(), "HideoutOfTheDawn.xml", TEMPLATE_ID);
				htmltext = event;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onEnterInstance(L2PcInstance player, InstanceWorld world, boolean firstEntrance) {
		if (firstEntrance) {
			world.addAllowed(player.getObjectId());
		}
		teleportPlayer(player, WOOD_LOC, world.getInstanceId(), false);
	}
}
