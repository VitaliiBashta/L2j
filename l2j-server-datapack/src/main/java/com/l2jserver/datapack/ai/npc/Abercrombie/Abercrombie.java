
package com.l2jserver.datapack.ai.npc.Abercrombie;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Mercenary Supplier Abercrombie AI.
 * @author Zoey76
 */
public final class Abercrombie extends AbstractNpcAI {
	// NPC
	private static final int ABERCROMBIE = 31555;
	// Items
	private static final int GOLDEN_RAM_BADGE_RECRUIT = 7246;
	private static final int GOLDEN_RAM_BADGE_SOLDIER = 7247;
	
	public Abercrombie() {
		super(Abercrombie.class.getSimpleName(), "ai/npc");
		addFirstTalkId(ABERCROMBIE);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		final String htmltext;
		if (hasQuestItems(player, GOLDEN_RAM_BADGE_SOLDIER)) {
			htmltext = "31555-07.html";
		} else if (hasQuestItems(player, GOLDEN_RAM_BADGE_RECRUIT)) {
			htmltext = "31555-01.html";
		} else {
			htmltext = "31555-09.html";
		}
		return htmltext;
	}
}
