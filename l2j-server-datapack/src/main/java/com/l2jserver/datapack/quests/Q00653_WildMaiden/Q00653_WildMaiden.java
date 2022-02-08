
package com.l2jserver.datapack.quests.Q00653_WildMaiden;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Wild Maiden (653)
 * @author malyelfik
 */
public class Q00653_WildMaiden extends Quest {
	// NPCs
	private static final int GALIBREDO = 30181;
	private static final int SUKI = 32013;
	// Item
	private static final int SOE = 736;
	// Misc
	private static final int MIN_LEVEL = 36;
	
	public Q00653_WildMaiden() {
		super(653, Q00653_WildMaiden.class.getSimpleName(), "Wild Maiden");
		addStartNpc(SUKI);
		addTalkId(GALIBREDO, SUKI);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = null;
		if (event.equals("32013-03.html")) {
			htmltext = event;
		} else if (event.equals("32013-04.htm")) {
			if (!st.hasQuestItems(SOE)) {
				return "32013-05.htm";
			}
			st.startQuest();
			st.takeItems(SOE, 1);
			npc.deleteMe();
			htmltext = (getRandom(2) == 0) ? event : "32013-04a.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (npc.getId()) {
			case SUKI:
				switch (st.getState()) {
					case State.CREATED:
						htmltext = (player.getLevel() >= MIN_LEVEL) ? "32013-01.htm" : "32013-01a.htm";
						break;
					case State.STARTED:
						htmltext = "32013-02.htm";
						break;
				}
				break;
			case GALIBREDO:
				if (st.isStarted()) {
					st.giveAdena(2553, true);
					st.exitQuest(true, true);
					htmltext = "30181-01.html";
				}
				break;
		}
		return htmltext;
	}
}