
package com.l2jserver.datapack.hellbound.ai;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;

/**
 * Manages Naia's cast on the Hellbound Core
 * @author GKR
 */
public final class HellboundCore extends AbstractNpcAI {
	// NPCs
	private static final int NAIA = 18484;
	private static final int HELLBOUND_CORE = 32331;
	// Skills
	private static final SkillHolder BEAM = new SkillHolder(5493);
	
	public HellboundCore() {
		super(HellboundCore.class.getSimpleName(), "hellbound/AI");
		addSpawnId(HELLBOUND_CORE, NAIA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equalsIgnoreCase("cast") && (HellboundEngine.getInstance().getLevel() <= 6)) {
			for (L2Character naia : npc.getKnownList().getKnownCharactersInRadius(900)) {
				if ((naia != null) && naia.isMonster() && (naia.getId() == NAIA) && !naia.isDead() && !naia.isChanneling()) {
					naia.setTarget(npc);
					naia.doSimultaneousCast(BEAM);
				}
			}
			startQuestTimer("cast", 10000, npc, null);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		if (npc.getId() == NAIA) {
			npc.setIsNoRndWalk(true);
		} else {
			startQuestTimer("cast", 10000, npc, null);
		}
		
		return super.onSpawn(npc);
	}
}