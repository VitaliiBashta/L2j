
package com.l2jserver.datapack.hellbound.ai;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import org.springframework.stereotype.Service;

@Service
public class Typhoon extends AbstractNpcAI {
	// NPCs
	private static final int TYPHOON = 25539;
	// Skills
	private static final SkillHolder STORM = new SkillHolder(5434); // Gust
	
	public Typhoon() {
		super(Typhoon.class.getSimpleName(), "hellbound/AI");
		addAggroRangeEnterId(TYPHOON);
		addSpawnId(TYPHOON);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equalsIgnoreCase("CAST") && (npc != null) && !npc.isDead()) {
			npc.doSimultaneousCast(STORM);
			startQuestTimer("CAST", 5000, npc, null);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon) {
		npc.doSimultaneousCast(STORM);
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		startQuestTimer("CAST", 5000, npc, null);
		return super.onSpawn(npc);
	}
}