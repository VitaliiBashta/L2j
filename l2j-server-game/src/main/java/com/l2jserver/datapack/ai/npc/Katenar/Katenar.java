
package com.l2jserver.datapack.ai.npc.Katenar;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.quests.Q00065_CertifiedSoulBreaker.Q00065_CertifiedSoulBreaker;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/**
 * Katenar AI for quests Certified Soul Breaker (65)
 * @author ivantotov
 */
public final class Katenar extends AbstractNpcAI {
	// NPC
	private static final int KATENAR = 32242;
	// Item
	private static final int SEALED_DOCUMENT = 9803;
	
	public Katenar() {
		super(Katenar.class.getSimpleName(), "ai/npc");
		addStartNpc(KATENAR);
		addTalkId(KATENAR);
		addFirstTalkId(KATENAR);
		addSpawnId(KATENAR);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final L2Npc npc0 = npc.getVariables().getObject("npc0", L2Npc.class);
		String htmltext = null;
		
		switch (event) {
			case "CREATED_50": {
				if (npc0 != null) {
					if (!npc.getVariables().getBoolean("SPAWNED", false)) {
						npc0.getVariables().set("SPAWNED", false);
					}
				}
				npc.deleteMe();
				break;
			}
			case "GOOD_LUCK": {
				final QuestState qs = player.getQuestState(Q00065_CertifiedSoulBreaker.class.getSimpleName());
				if (qs.isMemoState(14)) {
					if (npc0 != null) {
						if (!npc.getVariables().getBoolean("SPAWNED", false)) {
							npc0.getVariables().set("SPAWNED", false);
							broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.GOOD_LUCK);
						}
					}
					npc.deleteMe();
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance talker) {
		final QuestState qs = talker.getQuestState(Q00065_CertifiedSoulBreaker.class.getSimpleName());
		String htmltext = getNoQuestMsg(talker);
		final int memoState = qs.getMemoState();
		if (memoState == 12) {
			htmltext = "32242-01.html";
		} else if (memoState == 13) {
			final L2PcInstance player = npc.getVariables().getObject("player0", L2PcInstance.class);
			if (player == talker) {
				qs.setMemoState(14);
				qs.setCond(13, true);
				htmltext = "32242-02.html";
			} else {
				qs.setMemoState(14);
				qs.setCond(13, true);
				htmltext = "32242-03.html";
			}
			if (!hasQuestItems(player, SEALED_DOCUMENT)) {
				giveItems(player, SEALED_DOCUMENT, 1);
			}
		} else if (memoState == 14) {
			htmltext = "32242-04.html";
		}
		return htmltext;
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		startQuestTimer("CREATED_50", 50000, npc, null);
		final L2PcInstance player = npc.getVariables().getObject("player0", L2PcInstance.class);
		if (player != null) {
			broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.I_AM_LATE);
		}
		return super.onSpawn(npc);
	}
}