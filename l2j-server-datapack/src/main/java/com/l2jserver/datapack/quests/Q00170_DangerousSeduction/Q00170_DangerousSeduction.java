
package com.l2jserver.datapack.quests.Q00170_DangerousSeduction;

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
 * Dangerous Seduction (170)
 * @author malyelfik
 */
public class Q00170_DangerousSeduction extends Quest {
	// NPC
	private static final int VELLIOR = 30305;
	
	// Monster
	private static final int MERKENIS = 27022;
	
	// Item
	private static final int NIGHTMARE_CRYSTAL = 1046;
	
	// Misc
	private static final int MIN_LEVEL = 21;
	
	public Q00170_DangerousSeduction() {
		super(170, Q00170_DangerousSeduction.class.getSimpleName(), "Dangerous Seduction");
		addStartNpc(VELLIOR);
		addTalkId(VELLIOR);
		addKillId(MERKENIS);
		
		registerQuestItems(NIGHTMARE_CRYSTAL);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		if (event.equalsIgnoreCase("30305-04.htm")) {
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && st.isCond(1)) {
			st.setCond(2, true);
			st.giveItems(NIGHTMARE_CRYSTAL, 1);
			npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getId(), NpcStringId.SEND_MY_SOUL_TO_LICH_KING_ICARUS));
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (st.getState()) {
			case State.CREATED:
				htmltext = (player.getRace() == Race.DARK_ELF) ? (player.getLevel() >= MIN_LEVEL) ? "30305-01.htm" : "30305-02.htm" : "30305-03.htm";
				break;
			case State.STARTED:
				if (st.isCond(1)) {
					htmltext = "30305-05.html";
				} else {
					st.giveAdena(102680, true);
					st.addExpAndSp(38607, 4018);
					st.exitQuest(false, true);
					htmltext = "30305-06.html";
				}
				break;
			case State.COMPLETED:
				htmltext = getAlreadyCompletedMsg(player);
				break;
		}
		return htmltext;
	}
}