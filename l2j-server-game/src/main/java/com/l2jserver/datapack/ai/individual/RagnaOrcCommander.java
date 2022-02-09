
package com.l2jserver.datapack.ai.individual;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;

/**
 * Ragna Orc Commander AI.
 * @author Zealar
 */
public final class RagnaOrcCommander extends AbstractNpcAI {
	private static final int RAGNA_ORC_COMMANDER = 22694;
	
	public RagnaOrcCommander() {
		super(RagnaOrcCommander.class.getSimpleName(), "ai/individual");
		addSpawnId(RAGNA_ORC_COMMANDER);
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		spawnMinions(npc, "Privates1");
		spawnMinions(npc, getRandomBoolean() ? "Privates2" : "Privates3");
		return super.onSpawn(npc);
	}
}
