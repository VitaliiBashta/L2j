
package com.l2jserver.datapack.quests.Q00452_FindingtheLostSoldiers;

import com.l2jserver.gameserver.enums.QuestType;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Finding the Lost Soldiers (452)
 * @author Gigiikun
 * @version 2012-08-10
 */
public class Q00452_FindingtheLostSoldiers extends Quest {
	private static final int JAKAN = 32773;
	private static final int TAG_ID = 15513;
	private static final int[] SOLDIER_CORPSES = {
		32769,
		32770,
		32771,
		32772
	};
	
	public Q00452_FindingtheLostSoldiers() {
		super(452, Q00452_FindingtheLostSoldiers.class.getSimpleName(), "Finding the Lost Soldiers");
		addStartNpc(JAKAN);
		addTalkId(JAKAN);
		addTalkId(SOLDIER_CORPSES);
		registerQuestItems(TAG_ID);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		
		String htmltext = event;
		
		if (npc.getId() == JAKAN) {
			if (event.equals("32773-3.htm")) {
				st.startQuest();
			}
		} else {
			if (st.isCond(1)) {
				if (getRandom(10) < 5) {
					st.giveItems(TAG_ID, 1);
				} else {
					htmltext = "corpse-3.html";
				}
				st.setCond(2, true);
				npc.deleteMe();
			} else {
				htmltext = "corpse-3.html";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		if (npc.getId() == JAKAN) {
			switch (st.getState()) {
				case State.CREATED:
					htmltext = (player.getLevel() < 84) ? "32773-0.html" : "32773-1.htm";
					break;
				case State.STARTED:
					if (st.isCond(1)) {
						htmltext = "32773-4.html";
					} else if (st.isCond(2)) {
						htmltext = "32773-5.html";
						st.takeItems(TAG_ID, -1);
						st.giveAdena(95200, true);
						st.addExpAndSp(435024, 50366);
						st.exitQuest(QuestType.DAILY, true);
					}
					break;
				case State.COMPLETED:
					if (st.isNowAvailable()) {
						st.setState(State.CREATED);
						htmltext = (player.getLevel() < 84) ? "32773-0.html" : "32773-1.htm";
					} else {
						htmltext = "32773-6.html";
					}
					break;
			}
		} else {
			if (st.isCond(1)) {
				htmltext = "corpse-1.html";
			}
		}
		return htmltext;
	}
}
