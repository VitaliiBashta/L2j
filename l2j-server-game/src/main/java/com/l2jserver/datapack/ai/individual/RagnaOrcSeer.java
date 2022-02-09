
package com.l2jserver.datapack.ai.individual;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;

/**
 * Ragna Orc Seer AI.
 * @author Zealar
 */
public final class RagnaOrcSeer extends AbstractNpcAI {
	private static final int RAGNA_ORC_SEER = 22697;
	
	public RagnaOrcSeer() {
		super(RagnaOrcSeer.class.getSimpleName(), "ai/individual");
		addSpawnId(RAGNA_ORC_SEER);
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		spawnMinions(npc, "Privates" + getRandom(1, 2));
		return super.onSpawn(npc);
	}
}
