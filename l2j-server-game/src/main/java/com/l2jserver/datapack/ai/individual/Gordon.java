
package com.l2jserver.datapack.ai.individual;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Gordon AI
 * @author TOFIZ, malyelfik
 */
public final class Gordon extends AbstractNpcAI {
	private static final int GORDON = 29095;
	
	public Gordon() {
		super(Gordon.class.getSimpleName(), "ai/individual");
		addSpawnId(GORDON);
		addSeeCreatureId(GORDON);
	}
	
	@Override
	public String onSeeCreature(L2Npc npc, L2Character creature, boolean isSummon) {
		if (creature.isPlayer() && ((L2PcInstance) creature).isCursedWeaponEquipped()) {
			addAttackDesire(npc, creature);
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		((L2Attackable) npc).setCanReturnToSpawnPoint(false);
		return super.onSpawn(npc);
	}
}