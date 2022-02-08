
package com.l2jserver.datapack.ai.npc.Summons.MerchantGolem;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;

/**
 * Golem Trader AI.
 * @author Zoey76
 */
public final class GolemTrader extends AbstractNpcAI {
	// NPC
	private static final int GOLEM_TRADER = 13128;
	// Misc
	private static final long DESPAWN = 180000;
	
	public GolemTrader() {
		super(GolemTrader.class.getSimpleName(), "ai/npc/Summons");
		addSpawnId(GOLEM_TRADER);
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		npc.scheduleDespawn(DESPAWN);
		return super.onSpawn(npc);
	}
}
