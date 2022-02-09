
package com.l2jserver.datapack.ai.npc.TownPets;

import static com.l2jserver.gameserver.config.Configuration.general;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Town Pets AI
 * @author malyelfik
 */
public final class TownPets extends AbstractNpcAI {
	// Pet IDs
	private static final int[] PETS = {
		31202, // Maximus
		31203, // Moon Dancer
		31204, // Georgio
		31205, // Katz
		31206, // Ten Ten
		31207, // Sardinia
		31208, // La Grange
		31209, // Misty Rain
		31266, // Kaiser
		31593, // Dorothy
		31758, // Rafi
		31955, // Ruby
	};
	
	public TownPets() {
		super(TownPets.class.getSimpleName(), "ai/npc");
		
		if (general().allowPetWalkers()) {
			addSpawnId(PETS);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equalsIgnoreCase("move")) {
			final int locX = (npc.getSpawn().getX() - 50) + getRandom(100);
			final int locY = (npc.getSpawn().getY() - 50) + getRandom(100);
			npc.setRunning();
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(locX, locY, npc.getZ(), 0));
			startQuestTimer("move", 5000, npc, null);
		}
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		startQuestTimer("move", 3000, npc, null);
		return super.onSpawn(npc);
	}
}