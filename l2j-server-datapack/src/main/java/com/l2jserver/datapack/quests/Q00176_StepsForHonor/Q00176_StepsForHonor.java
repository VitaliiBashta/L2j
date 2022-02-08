
package com.l2jserver.datapack.quests.Q00176_StepsForHonor;

import com.l2jserver.gameserver.instancemanager.TerritoryWarManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Steps for Honor (176)
 * @author malyelfik
 */
public class Q00176_StepsForHonor extends Quest {
	// NPC
	private static final int RAPIDUS = 36479;
	// Item
	private static final int CLOAK = 14603;
	// Misc
	private static final int MIN_LEVEL = 80;
	
	public Q00176_StepsForHonor() {
		super(176, Q00176_StepsForHonor.class.getSimpleName(), "Steps for Honor");
		addStartNpc(RAPIDUS);
		addTalkId(RAPIDUS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equalsIgnoreCase("36479-04.html")) {
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (st.getState()) {
			case State.CREATED:
				htmltext = (player.getLevel() >= MIN_LEVEL) ? "36479-03.html" : "36479-02.html";
				break;
			case State.STARTED:
				if (TerritoryWarManager.getInstance().isTWInProgress()) {
					return "36479-05.html";
				}
				switch (st.getCond()) {
					case 1:
						htmltext = "36479-06.html";
						break;
					case 2:
						st.setCond(3, true);
						htmltext = "36479-07.html";
						break;
					case 3:
						htmltext = "36479-08.html";
						break;
					case 4:
						st.setCond(5, true);
						htmltext = "36479-09.html";
						break;
					case 5:
						htmltext = "36479-10.html";
						break;
					case 6:
						st.setCond(7, true);
						htmltext = "36479-11.html";
						break;
					case 7:
						htmltext = "36479-12.html";
						break;
					case 8:
						st.giveItems(CLOAK, 1);
						st.exitQuest(false, true);
						htmltext = "36479-13.html";
						break;
				}
				break;
			case State.COMPLETED:
				htmltext = "36479-01.html";
				break;
		}
		return htmltext;
	}
}