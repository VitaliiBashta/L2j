
package com.l2jserver.datapack.quests.Q00651_RunawayYouth;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Runaway Youth (651)
 * @author malyelfik
 */
public class Q00651_RunawayYouth extends Quest {
	// NPCs
	private static final int BATIDAE = 31989;
	private static final int IVAN = 32014;
	// Item
	private static final int SOE = 736;
	// Misc
	private static final int MIN_LEVEL = 26;
	
	public Q00651_RunawayYouth() {
		super(651, Q00651_RunawayYouth.class.getSimpleName(), "Runaway Youth");
		addStartNpc(IVAN);
		addTalkId(BATIDAE, IVAN);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = null;
		if (event.equals("32014-03.html")) {
			htmltext = event;
		} else if (event.equals("32014-04.htm")) {
			if (!st.hasQuestItems(SOE)) {
				return "32014-05.htm";
			}
			st.startQuest();
			st.takeItems(SOE, 1);
			npc.deleteMe();
			htmltext = event;
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (npc.getId()) {
			case IVAN:
				switch (st.getState()) {
					case State.CREATED:
						htmltext = (player.getLevel() >= MIN_LEVEL) ? "32014-01.htm" : "32014-01a.htm";
						break;
					case State.STARTED:
						htmltext = "32014-02.html";
						break;
				}
				break;
			case BATIDAE:
				if (st.isStarted()) {
					st.giveAdena(2883, true);
					st.exitQuest(true, true);
					htmltext = "31989-01.html";
				}
				break;
		}
		return htmltext;
	}
}