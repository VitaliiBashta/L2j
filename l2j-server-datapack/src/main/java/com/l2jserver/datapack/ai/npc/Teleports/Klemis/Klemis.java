
package com.l2jserver.datapack.ai.npc.Teleports.Klemis;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Klemis AI.
 * @author St3eT
 */
public class Klemis extends AbstractNpcAI {
	// NPC
	private static final int KLEMIS = 32734; // Klemis
	// Location
	private static final Location LOCATION = new Location(-180218, 185923, -10576);
	// Misc
	private static final int MIN_LV = 80;
	
	public Klemis() {
		super(Klemis.class.getSimpleName(), "ai/npc/Teleports");
		addStartNpc(KLEMIS);
		addTalkId(KLEMIS);
		addFirstTalkId(KLEMIS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equals("portInside")) {
			if (player.getLevel() >= MIN_LV) {
				player.teleToLocation(LOCATION);
			} else {
				return "32734-01.html";
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
}