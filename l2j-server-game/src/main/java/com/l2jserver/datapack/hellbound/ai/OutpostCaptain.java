
package com.l2jserver.datapack.hellbound.ai;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.gameserver.data.xml.impl.DoorData;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class OutpostCaptain extends AbstractNpcAI {
	// NPCs
	private static final int CAPTAIN = 18466;
	private static final int[] DEFENDERS = {
		22357, // Enceinte Defender
		22358, // Enceinte Defender
	};
	private static final int DOORKEEPER = 32351;
	
	public OutpostCaptain() {
		super(OutpostCaptain.class.getSimpleName(), "hellbound/AI");
		addKillId(CAPTAIN);
		addSpawnId(CAPTAIN, DOORKEEPER);
		addSpawnId(DEFENDERS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equalsIgnoreCase("LEVEL_UP")) {
			npc.deleteMe();
			HellboundEngine.getInstance().setLevel(9);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		if (HellboundEngine.getInstance().getLevel() == 8) {
			addSpawn(DOORKEEPER, npc.getSpawn().getLocation(), false, 0, false);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		npc.setIsNoRndWalk(true);
		
		if (npc.getId() == CAPTAIN) {
			final L2DoorInstance door = DoorData.getInstance().getDoor(20250001);
			if (door != null) {
				door.closeMe();
			}
		} else if (npc.getId() == DOORKEEPER) {
			startQuestTimer("LEVEL_UP", 3000, npc, null);
		}
		return super.onSpawn(npc);
	}
}