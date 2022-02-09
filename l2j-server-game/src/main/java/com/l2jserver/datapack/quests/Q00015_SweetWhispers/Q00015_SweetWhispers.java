
package com.l2jserver.datapack.quests.Q00015_SweetWhispers;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Sweet Whisper (15)<br>
 * Original jython script by disKret.
 * @author nonom
 */
public class Q00015_SweetWhispers extends Quest {
	// NPCs
	private static final int VLADIMIR = 31302;
	private static final int HIERARCH = 31517;
	private static final int M_NECROMANCER = 31518;
	
	public Q00015_SweetWhispers() {
		super(15, Q00015_SweetWhispers.class.getSimpleName(), "Sweet Whispers");
		addStartNpc(VLADIMIR);
		addTalkId(VLADIMIR, HIERARCH, M_NECROMANCER);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return htmltext;
		}
		
		switch (event) {
			case "31302-01.html":
				st.startQuest();
				break;
			case "31518-01.html":
				if (st.isCond(1)) {
					st.setCond(2);
				}
				break;
			case "31517-01.html":
				if (st.isCond(2)) {
					st.addExpAndSp(350531, 28204);
					st.exitQuest(false, true);
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
				if (npcId == VLADIMIR) {
					htmltext = (player.getLevel() >= 60) ? "31302-00.htm" : "31302-00a.html";
				}
				break;
			case State.STARTED:
				switch (npcId) {
					case VLADIMIR:
						if (st.isCond(1)) {
							htmltext = "31302-01a.html";
						}
						break;
					case M_NECROMANCER:
						switch (st.getCond()) {
							case 1:
								htmltext = "31518-00.html";
								break;
							case 2:
								htmltext = "31518-01a.html";
								break;
						}
						break;
					case HIERARCH:
						if (st.isCond(2)) {
							htmltext = "31517-00.html";
						}
						break;
				}
				break;
		}
		return htmltext;
	}
}
