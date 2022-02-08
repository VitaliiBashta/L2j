
package com.l2jserver.datapack.quests.Q00313_CollectSpores;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.util.Util;

/**
 * Collect Spores (313)
 * @author ivantotov
 */
public final class Q00313_CollectSpores extends Quest {
	// NPC
	private static final int HERBIEL = 30150;
	// Item
	private static final int SPORE_SAC = 1118;
	// Misc
	private static final int MIN_LEVEL = 8;
	private static final int REQUIRED_SAC_COUNT = 10;
	// Monster
	private static final int SPORE_FUNGUS = 20509;
	
	public Q00313_CollectSpores() {
		super(313, Q00313_CollectSpores.class.getSimpleName(), "Collect Spores");
		addStartNpc(HERBIEL);
		addTalkId(HERBIEL);
		addKillId(SPORE_FUNGUS);
		registerQuestItems(SPORE_SAC);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		String htmltext = null;
		switch (event) {
			case "30150-05.htm": {
				if (st.isCreated()) {
					st.startQuest();
					htmltext = event;
				}
				break;
			}
			case "30150-04.htm": {
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && st.isCond(1) && Util.checkIfInRange(1500, npc, killer, false)) {
			if (st.giveItemRandomly(npc, SPORE_SAC, 1, REQUIRED_SAC_COUNT, 0.4, true)) {
				st.setCond(2);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = player.getLevel() >= MIN_LEVEL ? "30150-03.htm" : "30150-02.htm";
				break;
			}
			case State.STARTED: {
				switch (st.getCond()) {
					case 1: {
						if (st.getQuestItemsCount(SPORE_SAC) < REQUIRED_SAC_COUNT) {
							htmltext = "30150-06.html";
						}
						break;
					}
					case 2: {
						if (st.getQuestItemsCount(SPORE_SAC) >= REQUIRED_SAC_COUNT) {
							st.giveAdena(3500, true);
							st.exitQuest(true, true);
							htmltext = "30150-07.html";
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
}