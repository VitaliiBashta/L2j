
package com.l2jserver.datapack.hellbound.ai.npc.Kanaf;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Kanaf AI.
 * @author GKR
 */
public final class Kanaf extends AbstractNpcAI {
	// NPCs
	private static final int KANAF = 32346;
	
	public Kanaf() {
		super(Kanaf.class.getSimpleName(), "hellbound/AI/NPC");
		addStartNpc(KANAF);
		addTalkId(KANAF);
		addFirstTalkId(KANAF);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equals("info")) {
			return "32346-0" + getRandom(1, 3) + ".htm";
		}
		return super.onAdvEvent(event, npc, player);
	}
}