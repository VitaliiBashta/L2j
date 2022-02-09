
package com.l2jserver.datapack.quests.Q00639_GuardiansOfTheHolyGrail;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Guardians of the Holy Grail (639)<br>
 * NOTE: This quest is no longer available since Freya(CT2.5)
 * @author corbin12
 */
public final class Q00639_GuardiansOfTheHolyGrail extends Quest {
	// NPC
	private static final int DOMINIC = 31350;
	
	public Q00639_GuardiansOfTheHolyGrail() {
		super(639, Q00639_GuardiansOfTheHolyGrail.class.getSimpleName(), "Guardians of the Holy Grail");
		addStartNpc(DOMINIC);
		addTalkId(DOMINIC);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		if (st != null) {
			st.exitQuest(true);
		}
		return "31350-01.html";
	}
}