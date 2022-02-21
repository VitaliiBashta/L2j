
package com.l2jserver.datapack.ai.npc.Kier;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.quests.Q00115_TheOtherSideOfTruth.Q00115_TheOtherSideOfTruth;
import com.l2jserver.datapack.quests.Q10283_RequestOfIceMerchant.Q10283_RequestOfIceMerchant;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.QuestState;
import org.springframework.stereotype.Service;

@Service
public final class Kier extends AbstractNpcAI {
	// NPC
	private static final int KIER = 32022;
	
	public Kier() {
		super(Kier.class.getSimpleName(), "ai/npc");
		addFirstTalkId(KIER);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		final QuestState st_Q00115 = player.getQuestState(Q00115_TheOtherSideOfTruth.class.getSimpleName());
		if (st_Q00115 == null) {
			htmltext = "32022-02.html";
		} else if (!st_Q00115.isCompleted()) {
			htmltext = "32022-01.html";
		}
		
		final QuestState st_Q10283 = player.getQuestState(Q10283_RequestOfIceMerchant.class.getSimpleName());
		if (st_Q10283 != null) {
			if (st_Q10283.isMemoState(2)) {
				htmltext = "32022-03.html";
			} else if (st_Q10283.isCompleted()) {
				htmltext = "32022-04.html";
			}
		}
		return htmltext;
	}
}