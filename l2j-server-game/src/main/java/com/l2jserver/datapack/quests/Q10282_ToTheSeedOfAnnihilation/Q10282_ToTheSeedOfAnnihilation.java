
package com.l2jserver.datapack.quests.Q10282_ToTheSeedOfAnnihilation;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * To the Seed of Destruction (10269)<br>
 * Original Jython script by Gnacik 2010-08-13 Based on Freya PTS.
 * @author nonom
 */
public class Q10282_ToTheSeedOfAnnihilation extends Quest {
	// NPCs
	private static final int KBALDIR = 32733;
	private static final int KLEMIS = 32734;
	// Item
	private static final int SOA_ORDERS = 15512;
	
	public Q10282_ToTheSeedOfAnnihilation() {
		super(10282, Q10282_ToTheSeedOfAnnihilation.class.getSimpleName(), "To the Seed of Annihilation");
		addStartNpc(KBALDIR);
		addTalkId(KBALDIR, KLEMIS);
		registerQuestItems(SOA_ORDERS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return htmltext;
		}
		
		switch (event) {
			case "32733-07.htm":
				st.startQuest();
				st.giveItems(SOA_ORDERS, 1);
				break;
			case "32734-02.htm":
				st.addExpAndSp(1148480, 99110);
				st.exitQuest(false);
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		final int npcId = npc.getId();
		switch (st.getState()) {
			case State.COMPLETED:
				if (npcId == KBALDIR) {
					htmltext = "32733-09.htm";
				} else if (npcId == KLEMIS) {
					htmltext = "32734-03.htm";
				}
				break;
			case State.CREATED:
				htmltext = (player.getLevel() < 84) ? "32733-00.htm" : "32733-01.htm";
				break;
			case State.STARTED:
				if (st.isCond(1)) {
					if (npcId == KBALDIR) {
						htmltext = "32733-08.htm";
					} else if (npcId == KLEMIS) {
						htmltext = "32734-01.htm";
					}
				}
				break;
		}
		return htmltext;
	}
}
