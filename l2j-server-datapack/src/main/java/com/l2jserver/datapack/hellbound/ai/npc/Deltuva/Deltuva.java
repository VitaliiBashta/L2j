
package com.l2jserver.datapack.hellbound.ai.npc.Deltuva;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.quests.Q00132_MatrasCuriosity.Q00132_MatrasCuriosity;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Deltuva AI.
 * @author GKR
 */
public final class Deltuva extends AbstractNpcAI {
	// NPCs
	private static final int DELTUVA = 32313;
	// Location
	private static final Location TELEPORT = new Location(17934, 283189, -9701);
	
	public Deltuva() {
		super(Deltuva.class.getSimpleName(), "hellbound/AI/NPC");
		addStartNpc(DELTUVA);
		addTalkId(DELTUVA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equalsIgnoreCase("teleport")) {
			final QuestState hostQuest = player.getQuestState(Q00132_MatrasCuriosity.class.getSimpleName());
			if ((hostQuest == null) || !hostQuest.isCompleted()) {
				return "32313-02.htm";
			}
			player.teleToLocation(TELEPORT);
		}
		return super.onAdvEvent(event, npc, player);
	}
}