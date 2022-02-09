
package com.l2jserver.datapack.quests.Q00240_ImTheOnlyOneYouCanTrust;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * I'm the Only One You Can Trust (240)
 * @author malyelfik
 */
public class Q00240_ImTheOnlyOneYouCanTrust extends Quest {
	// NPC
	private static final int KINTAIJIN = 32640;
	// Monster
	private static final int[] MOBS = {
		22617,
		22618,
		22619,
		22620,
		22621,
		22622,
		22623,
		22624,
		22625,
		22626,
		22627,
		22628,
		22629,
		22630,
		22631,
		22632,
		22633
	};
	// Item
	private static final int STAKATO_FANG = 14879;
	
	public Q00240_ImTheOnlyOneYouCanTrust() {
		super(240, Q00240_ImTheOnlyOneYouCanTrust.class.getSimpleName(), "I'm the Only One You Can Trust");
		addStartNpc(KINTAIJIN);
		addTalkId(KINTAIJIN);
		addKillId(MOBS);
		registerQuestItems(STAKATO_FANG);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		
		if (event.equalsIgnoreCase("32640-3.htm")) {
			st.startQuest();
		}
		return event;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		final L2PcInstance partyMember = getRandomPartyMember(player, 1);
		if (partyMember == null) {
			return super.onKill(npc, player, isSummon);
		}
		
		final QuestState st = getQuestState(partyMember, false);
		st.giveItems(STAKATO_FANG, 1);
		if (st.getQuestItemsCount(STAKATO_FANG) >= 25) {
			st.setCond(2, true);
		} else {
			st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (st.getState()) {
			case State.CREATED:
				htmltext = (player.getLevel() >= 81) ? "32640-1.htm" : "32640-0.htm";
				break;
			case State.STARTED:
				switch (st.getCond()) {
					case 1:
						htmltext = (!st.hasQuestItems(STAKATO_FANG)) ? "32640-8.html" : "32640-9.html";
						break;
					case 2:
						if (st.getQuestItemsCount(STAKATO_FANG) >= 25) {
							st.giveAdena(147200, true);
							st.takeItems(STAKATO_FANG, -1);
							st.addExpAndSp(589542, 36800);
							st.exitQuest(false, true);
							htmltext = "32640-10.html";
						}
						break;
				}
				break;
			case State.COMPLETED:
				htmltext = "32640-11.html";
				break;
		}
		return htmltext;
	}
}