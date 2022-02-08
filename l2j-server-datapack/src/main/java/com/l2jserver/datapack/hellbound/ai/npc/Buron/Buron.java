
package com.l2jserver.datapack.hellbound.ai.npc.Buron;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Buron AI.
 * @author DS
 */
public final class Buron extends AbstractNpcAI {
	private static final int BURON = 32345;
	private static final int HELMET = 9669;
	private static final int TUNIC = 9670;
	private static final int PANTS = 9671;
	private static final int DARION_BADGE = 9674;
	
	public Buron() {
		super(Buron.class.getSimpleName(), "hellbound/AI/NPC");
		addFirstTalkId(BURON);
		addStartNpc(BURON);
		addTalkId(BURON);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = event;
		if ("Rumor".equalsIgnoreCase(event)) {
			htmltext = "32345-" + HellboundEngine.getInstance().getLevel() + "r.htm";
		} else {
			if (HellboundEngine.getInstance().getLevel() < 2) {
				htmltext = "32345-lowlvl.htm";
			} else {
				if (getQuestItemsCount(player, DARION_BADGE) >= 10) {
					takeItems(player, DARION_BADGE, 10);
					if (event.equalsIgnoreCase("Tunic")) {
						player.addItem("Quest", TUNIC, 1, npc, true);
					} else if (event.equalsIgnoreCase("Helmet")) {
						player.addItem("Quest", HELMET, 1, npc, true);
					} else if (event.equalsIgnoreCase("Pants")) {
						player.addItem("Quest", PANTS, 1, npc, true);
					}
					htmltext = null;
				} else {
					htmltext = "32345-noitems.htm";
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		getQuestState(player, true);
		switch (HellboundEngine.getInstance().getLevel()) {
			case 1:
				return "32345-01.htm";
			case 2:
			case 3:
			case 4:
				return "32345-02.htm";
			default:
				return "32345-01a.htm";
		}
	}
}