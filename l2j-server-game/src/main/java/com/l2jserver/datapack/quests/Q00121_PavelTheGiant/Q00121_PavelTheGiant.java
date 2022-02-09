
package com.l2jserver.datapack.quests.Q00121_PavelTheGiant;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Pavel the Giants (121)<br>
 * Original Jython script by Ethernaly.
 * @author malyelfik
 */
public class Q00121_PavelTheGiant extends Quest {
	// NPCs
	private static final int NEWYEAR = 31961;
	private static final int YUMI = 32041;
	
	public Q00121_PavelTheGiant() {
		super(121, Q00121_PavelTheGiant.class.getSimpleName(), "Pavel the Giant");
		addStartNpc(NEWYEAR);
		addTalkId(NEWYEAR, YUMI);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		
		switch (event) {
			case "31961-02.htm":
				st.startQuest();
				break;
			case "32041-02.html":
				st.addExpAndSp(346320, 26069);
				st.exitQuest(false, true);
				break;
		}
		return event;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (npc.getId()) {
			case NEWYEAR:
				switch (st.getState()) {
					case State.CREATED:
						htmltext = (player.getLevel() >= 70) ? "31961-01.htm" : "31961-00.htm";
						break;
					case State.STARTED:
						htmltext = "31961-03.html";
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
				}
				break;
			case YUMI:
				if (st.isStarted()) {
					htmltext = "32041-01.html";
				}
				break;
		}
		return htmltext;
	}
}