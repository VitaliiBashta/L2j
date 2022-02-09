
package com.l2jserver.datapack.ai.npc.Rafforty;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Rafforty AI.
 * @author malyelfik, Gladicek
 */
public final class Rafforty extends AbstractNpcAI {
	// NPC
	private static final int RAFFORTY = 32020;
	// Items
	private static final int NECKLACE = 16025;
	private static final int BLESSED_NECKLACE = 16026;
	private static final int BOTTLE = 16027;
	
	public Rafforty() {
		super(Rafforty.class.getSimpleName(), "ai/npc");
		addStartNpc(RAFFORTY);
		addFirstTalkId(RAFFORTY);
		addTalkId(RAFFORTY);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = event;
		switch (event) {
			case "32020-01.html":
				if (!hasQuestItems(player, NECKLACE)) {
					htmltext = "32020-02.html";
				}
				break;
			case "32020-04.html":
				if (!hasQuestItems(player, BOTTLE)) {
					htmltext = "32020-05.html";
				}
				break;
			case "32020-07.html":
				if (!hasQuestItems(player, BOTTLE, NECKLACE)) {
					return "32020-08.html";
				}
				takeItems(player, NECKLACE, 1);
				takeItems(player, BOTTLE, 1);
				giveItems(player, BLESSED_NECKLACE, 1);
				break;
		}
		return htmltext;
	}
}