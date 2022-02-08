
package com.l2jserver.datapack.quests.Q00014_WhereaboutsOfTheArchaeologist;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Whereabouts of the Archaeologist (14)<br>
 * Original Jython script by disKret.
 * @author nonom
 */
public class Q00014_WhereaboutsOfTheArchaeologist extends Quest {
	// NPCs
	private static final int LIESEL = 31263;
	private static final int GHOST_OF_ADVENTURER = 31538;
	// Item
	private static final int LETTER = 7253;
	
	public Q00014_WhereaboutsOfTheArchaeologist() {
		super(14, Q00014_WhereaboutsOfTheArchaeologist.class.getSimpleName(), "Whereabouts of the Archaeologist");
		addStartNpc(LIESEL);
		addTalkId(LIESEL, GHOST_OF_ADVENTURER);
		registerQuestItems(LETTER);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return htmltext;
		}
		
		switch (event) {
			case "31263-02.html":
				st.startQuest();
				st.giveItems(LETTER, 1);
				break;
			case "31538-01.html":
				if (st.isCond(1) && st.hasQuestItems(LETTER)) {
					st.giveAdena(136928, true);
					st.addExpAndSp(325881, 32524);
					st.exitQuest(false, true);
				} else {
					htmltext = "31538-02.html";
				}
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
				htmltext = getAlreadyCompletedMsg(player);
				break;
			case State.CREATED:
				if (npcId == LIESEL) {
					htmltext = (player.getLevel() < 74) ? "31263-01.html" : "31263-00.htm";
				}
				break;
			case State.STARTED:
				if (st.isCond(1)) {
					switch (npcId) {
						case LIESEL:
							htmltext = "31263-02.html";
							break;
						case GHOST_OF_ADVENTURER:
							htmltext = "31538-00.html";
							break;
					}
				}
				break;
		}
		return htmltext;
	}
}
