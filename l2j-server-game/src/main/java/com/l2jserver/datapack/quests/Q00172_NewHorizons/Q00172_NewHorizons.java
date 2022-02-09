
package com.l2jserver.datapack.quests.Q00172_NewHorizons;

import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * New Horizons (172)
 * @author malyelfik
 */
public class Q00172_NewHorizons extends Quest {
	// NPCs
	private static final int ZENYA = 32140;
	private static final int RAGARA = 32163;
	
	// Items
	private static final int SCROLL_OF_ESCAPE_GIRAN = 7559;
	private static final int MARK_OF_TRAVELER = 7570;
	
	// Misc
	private static final int MIN_LEVEL = 3;
	
	public Q00172_NewHorizons() {
		super(172, Q00172_NewHorizons.class.getSimpleName(), "New Horizons");
		addStartNpc(ZENYA);
		addTalkId(ZENYA, RAGARA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = event;
		switch (event) {
			case "32140-04.htm":
				st.startQuest();
				break;
			case "32163-02.html":
				st.giveItems(SCROLL_OF_ESCAPE_GIRAN, 1);
				st.giveItems(MARK_OF_TRAVELER, 1);
				st.exitQuest(false, true);
				break;
			default:
				htmltext = null;
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (npc.getId()) {
			case ZENYA:
				switch (st.getState()) {
					case State.CREATED:
						htmltext = (player.getRace() == Race.KAMAEL) ? (player.getLevel() >= MIN_LEVEL) ? "32140-01.htm" : "32140-02.htm" : "32140-03.htm";
						break;
					case State.STARTED:
						htmltext = "32140-05.html";
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
				}
				break;
			case RAGARA:
				if (st.isStarted()) {
					htmltext = "32163-01.html";
				}
				break;
		}
		return htmltext;
	}
}