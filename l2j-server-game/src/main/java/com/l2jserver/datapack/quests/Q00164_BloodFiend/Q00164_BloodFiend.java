
package com.l2jserver.datapack.quests.Q00164_BloodFiend;

import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;

/**
 * Blood Fiend (164)
 * @author xban1x
 */
public class Q00164_BloodFiend extends Quest {
	// NPC
	private static final int CREAMEES = 30149;
	// Monster
	private static final int KIRUNAK = 27021;
	// Item
	private static final int KIRUNAK_SKULL = 1044;
	// Misc
	private static final int MIN_LVL = 21;
	
	public Q00164_BloodFiend() {
		super(164, Q00164_BloodFiend.class.getSimpleName(), "Blood Fiend");
		addStartNpc(CREAMEES);
		addTalkId(CREAMEES);
		addKillId(KIRUNAK);
		registerQuestItems(KIRUNAK_SKULL);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equals("30149-04.htm")) {
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && st.isCond(1)) {
			npc.broadcastPacket(new NpcSay(npc, Say2.ALL, NpcStringId.I_HAVE_FULFILLED_MY_CONTRACT_WITH_TRADER_CREAMEES));
			st.giveItems(KIRUNAK_SKULL, 1);
			st.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = (player.getRace() != Race.DARK_ELF) ? player.getLevel() >= MIN_LVL ? "30149-03.htm" : "30149-02.htm" : "30149-00.htm";
				break;
			}
			case State.STARTED: {
				if (st.isCond(2) && st.hasQuestItems(KIRUNAK_SKULL)) {
					st.giveAdena(42130, true);
					st.addExpAndSp(35637, 1854);
					st.exitQuest(false, true);
					htmltext = "30149-06.html";
				} else {
					htmltext = "30149-05.html";
				}
				break;
			}
			case State.COMPLETED: {
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
}
