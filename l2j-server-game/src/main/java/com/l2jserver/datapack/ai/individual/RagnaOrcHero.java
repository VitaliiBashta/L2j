
package com.l2jserver.datapack.ai.individual;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;

/**
 * Ragna Orc Hero AI.
 * @author Zealar
 */
public final class RagnaOrcHero extends AbstractNpcAI {
	private static final int RAGNA_ORC_HERO = 22693;
	
	public RagnaOrcHero() {
		super(RagnaOrcHero.class.getSimpleName(), "ai/individual");
		addSpawnId(RAGNA_ORC_HERO);
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		spawnMinions(npc, getRandom(100) < 70 ? "Privates1" : "Privates2");
		return super.onSpawn(npc);
	}
}
