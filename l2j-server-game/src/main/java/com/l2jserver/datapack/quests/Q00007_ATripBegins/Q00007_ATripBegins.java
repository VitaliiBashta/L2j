
package com.l2jserver.datapack.quests.Q00007_ATripBegins;

import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import org.springframework.stereotype.Service;

@Service
public class Q00007_ATripBegins extends Quest {
	// NPCs
	private static final int MIRABEL = 30146;
	private static final int ARIEL = 30148;
	private static final int ASTERIOS = 30154;
	// Items
	private static final int ARIELS_RECOMMENDATION = 7572;
	private static final int SCROLL_OF_ESCAPE_GIRAN = 7559;
	private static final int MARK_OF_TRAVELER = 7570;
	// Misc
	private static final int MIN_LEVEL = 3;
	
	public Q00007_ATripBegins() {
		super(7, Q00007_ATripBegins.class.getSimpleName(), "A Trip Begins");
		addStartNpc(MIRABEL);
		addTalkId(MIRABEL, ARIEL, ASTERIOS);
		registerQuestItems(ARIELS_RECOMMENDATION);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = event;
		switch (event) {
			case "30146-03.htm":
				st.startQuest();
				break;
			case "30146-06.html":
				st.giveItems(SCROLL_OF_ESCAPE_GIRAN, 1);
				st.giveItems(MARK_OF_TRAVELER, 1);
				st.exitQuest(false, true);
				break;
			case "30148-02.html":
				st.setCond(2, true);
				st.giveItems(ARIELS_RECOMMENDATION, 1);
				break;
			case "30154-02.html":
				if (!st.hasQuestItems(ARIELS_RECOMMENDATION)) {
					return "30154-03.html";
				}
				st.takeItems(ARIELS_RECOMMENDATION, -1);
				st.setCond(3, true);
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
			case MIRABEL:
				switch (st.getState()) {
					case State.CREATED:
						htmltext = ((player.getRace() == Race.ELF) && (player.getLevel() >= MIN_LEVEL)) ? "30146-01.htm" : "30146-02.html";
						break;
					case State.STARTED:
						if (st.isCond(1)) {
							htmltext = "30146-04.html";
						} else if (st.isCond(3)) {
							htmltext = "30146-05.html";
						}
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
				}
				break;
			case ARIEL:
				if (st.isStarted()) {
					if (st.isCond(1)) {
						htmltext = "30148-01.html";
					} else if (st.isCond(2)) {
						htmltext = "30148-03.html";
					}
				}
				break;
			case ASTERIOS:
				if (st.isStarted()) {
					if (st.isCond(2)) {
						htmltext = "30154-01.html";
					} else if (st.isCond(3)) {
						htmltext = "30154-04.html";
					}
				}
				break;
		}
		return htmltext;
	}
}