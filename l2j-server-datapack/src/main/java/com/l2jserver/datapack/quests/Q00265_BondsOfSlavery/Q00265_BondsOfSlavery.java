
package com.l2jserver.datapack.quests.Q00265_BondsOfSlavery;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.datapack.quests.Q00281_HeadForTheHills.Q00281_HeadForTheHills;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Bonds of Slavery (265)
 * @author xban1x
 */
public final class Q00265_BondsOfSlavery extends Quest {
	// Item
	private static final int IMP_SHACKLES = 1368;
	// NPC
	private static final int KRISTIN = 30357;
	// Misc
	private static final int MIN_LVL = 6;
	// Monsters
	private static final Map<Integer, Integer> MONSTERS = new HashMap<>();
	static {
		MONSTERS.put(20004, 5); // Imp
		MONSTERS.put(20005, 6); // Imp Elder
	}
	
	public Q00265_BondsOfSlavery() {
		super(265, Q00265_BondsOfSlavery.class.getSimpleName(), "Bonds of Slavery");
		addStartNpc(KRISTIN);
		addTalkId(KRISTIN);
		addKillId(MONSTERS.keySet());
		registerQuestItems(IMP_SHACKLES);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		String htmltext = null;
		if (st == null) {
			return htmltext;
		}
		
		switch (event) {
			case "30357-04.htm": {
				st.startQuest();
				htmltext = event;
				break;
			}
			case "30357-07.html": {
				st.exitQuest(true, true);
				htmltext = event;
				break;
			}
			case "30357-08.html": {
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && (getRandom(10) < MONSTERS.get(npc.getId()))) {
			st.giveItems(IMP_SHACKLES, 1);
			st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = (player.getRace() == Race.DARK_ELF) ? (player.getLevel() >= MIN_LVL) ? "30357-03.htm" : "30357-02.html" : "30357-01.html";
				break;
			}
			case State.STARTED: {
				if (st.hasQuestItems(IMP_SHACKLES)) {
					final long shackles = st.getQuestItemsCount(IMP_SHACKLES);
					st.giveAdena((shackles * 12) + (shackles >= 10 ? 500 : 0), true);
					st.takeItems(IMP_SHACKLES, -1);
					Q00281_HeadForTheHills.giveNewbieReward(player);
					htmltext = "30357-06.html";
				} else {
					htmltext = "30357-05.html";
				}
				break;
			}
		}
		return htmltext;
	}
}
