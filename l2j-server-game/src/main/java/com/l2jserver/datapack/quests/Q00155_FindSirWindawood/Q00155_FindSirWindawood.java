
package com.l2jserver.datapack.quests.Q00155_FindSirWindawood;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Find Sir Windawood (155)
 * @author malyelfik
 */
public class Q00155_FindSirWindawood extends Quest {
	// NPCs
	private static final int ABELLOS = 30042;
	private static final int SIR_COLLIN_WINDAWOOD = 30311;
	// Items
	private static final int OFFICIAL_LETTER = 1019;
	private static final int HASTE_POTION = 734;
	// Misc
	private static final int MIN_LEVEL = 3;
	
	public Q00155_FindSirWindawood() {
		super(155, Q00155_FindSirWindawood.class.getSimpleName(), "Find Sir Windawood");
		addStartNpc(ABELLOS);
		addTalkId(ABELLOS, SIR_COLLIN_WINDAWOOD);
		registerQuestItems(OFFICIAL_LETTER);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equalsIgnoreCase("30042-03.htm")) {
			st.startQuest();
			st.giveItems(OFFICIAL_LETTER, 1);
			return event;
		}
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (npc.getId()) {
			case ABELLOS:
				switch (st.getState()) {
					case State.CREATED:
						htmltext = (player.getLevel() >= MIN_LEVEL) ? "30042-02.htm" : "30042-01.htm";
						break;
					case State.STARTED:
						htmltext = "30042-04.html";
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
				}
				break;
			case SIR_COLLIN_WINDAWOOD:
				if (st.isStarted() && st.hasQuestItems(OFFICIAL_LETTER)) {
					st.giveItems(HASTE_POTION, 1);
					st.exitQuest(false, true);
					htmltext = "30311-01.html";
				}
				break;
		}
		return htmltext;
	}
}