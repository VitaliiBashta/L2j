
package com.l2jserver.datapack.ai.npc.Teleports.GhostChamberlainOfElmoreden;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Ghost Chamberlain of Elmoreden AI.
 * @author Adry_85
 * @since 2.6.0.0
 */
public class GhostChamberlainOfElmoreden extends AbstractNpcAI {
	// NPCs
	private static final int GHOST_CHAMBERLAIN_OF_ELMOREDEN_1 = 31919;
	private static final int GHOST_CHAMBERLAIN_OF_ELMOREDEN_2 = 31920;
	// Items
	private static final int USED_GRAVE_PASS = 7261;
	private static final int ANTIQUE_BROOCH = 7262;
	// Locations
	private static final Location FOUR_SEPULCHERS_LOC = new Location(178127, -84435, -7215);
	private static final Location IMPERIAL_TOMB_LOC = new Location(186699, -75915, -2826);
	
	public GhostChamberlainOfElmoreden() {
		super(GhostChamberlainOfElmoreden.class.getSimpleName(), "ai/npc/Teleports");
		addStartNpc(GHOST_CHAMBERLAIN_OF_ELMOREDEN_1, GHOST_CHAMBERLAIN_OF_ELMOREDEN_2);
		addTalkId(GHOST_CHAMBERLAIN_OF_ELMOREDEN_1, GHOST_CHAMBERLAIN_OF_ELMOREDEN_2);
		addFirstTalkId(GHOST_CHAMBERLAIN_OF_ELMOREDEN_1, GHOST_CHAMBERLAIN_OF_ELMOREDEN_2);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equals("FOUR_SEPULCHERS")) {
			if (hasQuestItems(player, USED_GRAVE_PASS)) {
				takeItems(player, USED_GRAVE_PASS, 1);
				player.teleToLocation(FOUR_SEPULCHERS_LOC);
			} else if (hasQuestItems(player, ANTIQUE_BROOCH)) {
				player.teleToLocation(FOUR_SEPULCHERS_LOC);
			} else {
				return npc.getId() + "-01.html";
			}
		} else if (event.equals("IMPERIAL_TOMB")) {
			if (hasQuestItems(player, USED_GRAVE_PASS)) {
				takeItems(player, USED_GRAVE_PASS, 1);
				player.teleToLocation(IMPERIAL_TOMB_LOC);
			} else if (hasQuestItems(player, ANTIQUE_BROOCH)) {
				player.teleToLocation(IMPERIAL_TOMB_LOC);
			} else {
				return npc.getId() + "-01.html";
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
}