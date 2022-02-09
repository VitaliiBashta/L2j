
package com.l2jserver.datapack.quests.Q00354_ConquestOfAlligatorIsland;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Conquest of Alligator Island (354)
 * @author Adry_85
 */
public final class Q00354_ConquestOfAlligatorIsland extends Quest {
	// NPC
	private static final int KLUCK = 30895;
	// Items
	private static final int ALLIGATOR_TOOTH = 5863;
	private static final int MYSTERIOUS_MAP_PIECE = 5864;
	private static final int PIRATES_TREASURE_MAP = 5915;
	// Misc
	private static final int MIN_LEVEL = 38;
	// Mobs
	private static final Map<Integer, Double> MOB1 = new HashMap<>();
	private static final Map<Integer, Integer> MOB2 = new HashMap<>();
	static {
		MOB1.put(20804, 0.84); // crokian_lad
		MOB1.put(20805, 0.91); // dailaon_lad
		MOB1.put(20806, 0.88); // crokian_lad_warrior
		MOB1.put(20807, 0.92); // farhite_lad
		MOB2.put(22208, 14); // nos_lad
		MOB2.put(20991, 69); // tribe_of_swamp
	}
	
	public Q00354_ConquestOfAlligatorIsland() {
		super(354, Q00354_ConquestOfAlligatorIsland.class.getSimpleName(), "Conquest of Alligator Island");
		addStartNpc(KLUCK);
		addTalkId(KLUCK);
		addKillId(MOB1.keySet());
		addKillId(MOB2.keySet());
		registerQuestItems(ALLIGATOR_TOOTH, MYSTERIOUS_MAP_PIECE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "30895-04.html":
			case "30895-05.html":
			case "30895-09.html": {
				htmltext = event;
				break;
			}
			case "30895-02.html": {
				st.startQuest();
				htmltext = event;
				break;
			}
			case "ADENA": {
				final long count = st.getQuestItemsCount(ALLIGATOR_TOOTH);
				if (count >= 100) {
					st.giveAdena((count * 220) + 10700, true);
					st.takeItems(ALLIGATOR_TOOTH, -1);
					htmltext = "30895-06.html";
				} else if (count > 0) {
					st.giveAdena((count * 220) + 3100, true);
					st.takeItems(ALLIGATOR_TOOTH, -1);
					htmltext = "30895-07.html";
				} else {
					htmltext = "30895-08.html";
				}
				break;
			}
			case "30895-10.html": {
				st.exitQuest(true, true);
				htmltext = event;
				break;
			}
			case "REWARD": {
				final long count = st.getQuestItemsCount(MYSTERIOUS_MAP_PIECE);
				if (count >= 10) {
					st.giveItems(PIRATES_TREASURE_MAP, 1);
					st.takeItems(MYSTERIOUS_MAP_PIECE, 10);
					htmltext = "30895-13.html";
				} else if (count > 0) {
					htmltext = "30895-12.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		final QuestState st = getRandomPartyMemberState(player, -1, 3, npc);
		if (st != null) {
			int npcId = npc.getId();
			if (MOB1.containsKey(npcId)) {
				st.giveItemRandomly(npc, ALLIGATOR_TOOTH, 1, 0, MOB1.get(npcId), true);
			} else {
				final int itemCount = ((getRandom(100) < MOB2.get(npcId)) ? 2 : 1);
				st.giveItemRandomly(npc, ALLIGATOR_TOOTH, itemCount, 0, 1.0, true);
			}
			
			st.giveItemRandomly(npc, MYSTERIOUS_MAP_PIECE, 1, 0, 0.1, false);
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (st.isCreated()) {
			htmltext = ((player.getLevel() >= MIN_LEVEL) ? "30895-01.htm" : "30895-03.html");
		} else if (st.isStarted()) {
			htmltext = (st.hasQuestItems(MYSTERIOUS_MAP_PIECE) ? "30895-11.html" : "30895-04.html");
		}
		return htmltext;
	}
}
