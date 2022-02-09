
package com.l2jserver.datapack.quests.Q00646_SignsOfRevolt;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Signs of Revolt (646)<br>
 * NOTE: This quest is no longer available since Gracia Epilogue
 * @author malyelfik
 */
public class Q00646_SignsOfRevolt extends Quest {
	// NPC
	private static final int TORRANT = 32016;
	// Misc
	private static final int MIN_LEVEL = 80;
	
	public Q00646_SignsOfRevolt() {
		super(646, Q00646_SignsOfRevolt.class.getSimpleName(), "Signs of Revolt");
		addStartNpc(TORRANT);
		addTalkId(TORRANT);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		if (st != null) {
			st.exitQuest(true);
		}
		return (player.getLevel() >= MIN_LEVEL) ? "32016-01.html" : "32016-02.html";
	}
}