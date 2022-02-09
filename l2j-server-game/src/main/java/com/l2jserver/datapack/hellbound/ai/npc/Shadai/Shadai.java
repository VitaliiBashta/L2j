
package com.l2jserver.datapack.hellbound.ai.npc.Shadai;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Shadai AI.
 * @author GKR
 */
public final class Shadai extends AbstractNpcAI {
	// NPCs
	private static final int SHADAI = 32347;
	// Locations
	private static final Location DAY_COORDS = new Location(16882, 238952, 9776);
	private static final Location NIGHT_COORDS = new Location(9064, 253037, -1928);
	
	public Shadai() {
		super(Shadai.class.getSimpleName(), "hellbound/AI/NPC");
		addSpawnId(SHADAI);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equals("VALIDATE_POS") && (npc != null)) {
			Location coords = DAY_COORDS;
			boolean mustRevalidate = false;
			if ((npc.getX() != NIGHT_COORDS.getX()) && GameTimeController.getInstance().isNight()) {
				coords = NIGHT_COORDS;
				mustRevalidate = true;
			} else if ((npc.getX() != DAY_COORDS.getX()) && !GameTimeController.getInstance().isNight()) {
				mustRevalidate = true;
			}
			
			if (mustRevalidate) {
				npc.getSpawn().setLocation(coords);
				npc.teleToLocation(coords);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		startQuestTimer("VALIDATE_POS", 60000, npc, null, true);
		return super.onSpawn(npc);
	}
}