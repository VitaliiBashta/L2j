
package com.l2jserver.datapack.ai.npc.Asamah;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.quests.Q00111_ElrokianHuntersProof.Q00111_ElrokianHuntersProof;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.QuestState;
import org.springframework.stereotype.Service;

@Service
public class Asamah extends AbstractNpcAI {
	// NPC
	private static final int ASAMAH = 32115;
	
	public Asamah() {
		super(Asamah.class.getSimpleName(), "ai/npc");
		addFirstTalkId(ASAMAH);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		if (event.equals("32115-03.htm") || event.equals("32115-04.htm")) {
			htmltext = event;
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = player.getQuestState(Q00111_ElrokianHuntersProof.class.getSimpleName());
		return ((st != null) && (st.isCompleted())) ? "32115-01.htm" : "32115-02.htm";
	}
}
