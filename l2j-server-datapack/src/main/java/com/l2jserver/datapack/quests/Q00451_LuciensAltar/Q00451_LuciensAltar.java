
package com.l2jserver.datapack.quests.Q00451_LuciensAltar;

import com.l2jserver.gameserver.enums.QuestType;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Lucien's Altar (451)<br>
 * Original Jython script by Bloodshed.
 * @author malyelfik
 */
public class Q00451_LuciensAltar extends Quest {
	// NPCs
	private static final int DAICHIR = 30537;
	private static final int[] ALTARS = {
		32706,
		32707,
		32708,
		32709,
		32710
	};
	
	// Items
	private static final int REPLENISHED_BEAD = 14877;
	private static final int DISCHARGED_BEAD = 14878;
	// Misc
	private static final int MIN_LEVEL = 80;
	
	public Q00451_LuciensAltar() {
		super(451, Q00451_LuciensAltar.class.getSimpleName(), "Lucien's Altar");
		addStartNpc(DAICHIR);
		addTalkId(ALTARS);
		addTalkId(DAICHIR);
		registerQuestItems(REPLENISHED_BEAD, DISCHARGED_BEAD);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		
		if (st == null) {
			return null;
		}
		
		String htmltext = null;
		if (event.equals("30537-04.htm")) {
			htmltext = event;
		} else if (event.equals("30537-05.htm")) {
			st.startQuest();
			st.giveItems(REPLENISHED_BEAD, 5);
			htmltext = event;
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		final int npcId = npc.getId();
		if (npcId == DAICHIR) {
			switch (st.getState()) {
				case State.COMPLETED:
					if (!st.isNowAvailable()) {
						htmltext = "30537-03.html";
						break;
					}
					st.setState(State.CREATED);
				case State.CREATED:
					htmltext = (player.getLevel() >= MIN_LEVEL) ? "30537-01.htm" : "30537-02.htm";
					break;
				case State.STARTED:
					if (st.isCond(1)) {
						if (st.isSet("32706") || st.isSet("32707") || st.isSet("32708") || st.isSet("32709") || st.isSet("32710")) {
							htmltext = "30537-10.html";
						} else {
							htmltext = "30537-09.html";
						}
					} else {
						st.giveAdena(255380, true); // Tauti reward: 13 773 960 exp, 16 232 820 sp, 742 800 Adena
						st.exitQuest(QuestType.DAILY, true);
						htmltext = "30537-08.html";
					}
					break;
			}
		} else if (st.isCond(1) && st.hasQuestItems(REPLENISHED_BEAD)) {
			if (st.getInt(String.valueOf(npcId)) == 0) {
				st.set(String.valueOf(npcId), "1");
				st.takeItems(REPLENISHED_BEAD, 1);
				st.giveItems(DISCHARGED_BEAD, 1);
				st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				
				if (st.getQuestItemsCount(DISCHARGED_BEAD) >= 5) {
					st.setCond(2, true);
				}
				
				htmltext = "recharge.html";
			} else {
				htmltext = "findother.html";
			}
		}
		
		return htmltext;
	}
}