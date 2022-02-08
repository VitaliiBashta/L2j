
package com.l2jserver.datapack.quests.Q10269_ToTheSeedOfDestruction;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * To the Seed of Destruction (10269)<br>
 * Original Jython script by Kerberos.
 * @author nonom
 */
public class Q10269_ToTheSeedOfDestruction extends Quest {
	// NPCs
	private static final int KEUCEREUS = 32548;
	private static final int ALLENOS = 32526;
	// Item
	private static final int INTRODUCTION = 13812;
	
	public Q10269_ToTheSeedOfDestruction() {
		super(10269, Q10269_ToTheSeedOfDestruction.class.getSimpleName(), "To the Seed of Destruction");
		addStartNpc(KEUCEREUS);
		addTalkId(KEUCEREUS, ALLENOS);
		registerQuestItems(INTRODUCTION);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		
		if (event.equals("32548-05.html")) {
			st.startQuest();
			st.giveItems(INTRODUCTION, 1);
		}
		return event;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (npc.getId()) {
			case KEUCEREUS:
				switch (st.getState()) {
					case State.CREATED:
						htmltext = (player.getLevel() < 75) ? "32548-00.html" : "32548-01.htm";
						break;
					case State.STARTED:
						htmltext = "32548-06.html";
						break;
					case State.COMPLETED:
						htmltext = "32548-0a.html";
						break;
				}
				break;
			case ALLENOS:
				switch (st.getState()) {
					case State.STARTED:
						htmltext = "32526-01.html";
						st.giveAdena(29174, true);
						st.addExpAndSp(176121, 7671);
						st.exitQuest(false, true);
						break;
					case State.COMPLETED:
						htmltext = "32526-02.html";
						break;
					default:
						break;
				}
				break;
		}
		return htmltext;
	}
}
