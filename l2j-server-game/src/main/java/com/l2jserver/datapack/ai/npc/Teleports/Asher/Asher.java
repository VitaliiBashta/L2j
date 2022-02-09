
package com.l2jserver.datapack.ai.npc.Teleports.Asher;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;

/**
 * Asher AI.
 * @author Adry_85
 * @since 2.6.0.0
 */
public class Asher extends AbstractNpcAI {
	// NPC
	private static final int ASHER = 32714;
	// Location
	private static final Location LOCATION = new Location(43835, -47749, -792);
	// Misc
	private static final int ADENA = 50000;
	
	public Asher() {
		super(Asher.class.getSimpleName(), "ai/npc/Teleports");
		addFirstTalkId(ASHER);
		addStartNpc(ASHER);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equals("teleport")) {
			if (player.getAdena() >= ADENA) {
				player.teleToLocation(LOCATION);
				takeItems(player, Inventory.ADENA_ID, ADENA);
			} else {
				return "32714-02.html";
			}
		} else if (event.equals("32714-01.html")) {
			return event;
		}
		return super.onAdvEvent(event, npc, player);
	}
}