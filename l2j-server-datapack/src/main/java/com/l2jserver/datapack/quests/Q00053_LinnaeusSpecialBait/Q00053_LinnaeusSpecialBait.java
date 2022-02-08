
package com.l2jserver.datapack.quests.Q00053_LinnaeusSpecialBait;

import static com.l2jserver.gameserver.config.Configuration.rates;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Linnaeus Special Bait (53)<br>
 * Original Jython script by Next and DooMita.
 * @author nonom
 */
public class Q00053_LinnaeusSpecialBait extends Quest {
	// NPCs
	private static final int LINNAEUS = 31577;
	private static final int CRIMSON_DRAKE = 20670;
	// Items
	private static final int CRIMSON_DRAKE_HEART = 7624;
	private static final int FLAMING_FISHING_LURE = 7613;
	
	public Q00053_LinnaeusSpecialBait() {
		super(53, Q00053_LinnaeusSpecialBait.class.getSimpleName(), "Linnaeus Special Bait");
		addStartNpc(LINNAEUS);
		addTalkId(LINNAEUS);
		addKillId(CRIMSON_DRAKE);
		registerQuestItems(CRIMSON_DRAKE_HEART);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		
		String htmltext = event;
		
		switch (event) {
			case "31577-1.htm":
				st.startQuest();
				break;
			case "31577-3.htm":
				if (st.isCond(2) && (st.getQuestItemsCount(CRIMSON_DRAKE_HEART) >= 100)) {
					st.giveItems(FLAMING_FISHING_LURE, 4);
					st.exitQuest(false, true);
				} else {
					htmltext = "31577-5.html";
				}
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		final L2PcInstance partyMember = getRandomPartyMember(player, 1);
		if (partyMember == null) {
			return null;
		}
		
		final QuestState st = getQuestState(partyMember, false);
		
		if (st.getQuestItemsCount(CRIMSON_DRAKE_HEART) < 100) {
			double chance = 33 * rates().getRateQuestDrop();
			if (getRandom(100) < chance) {
				st.rewardItems(CRIMSON_DRAKE_HEART, 1);
				st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		
		if (st.getQuestItemsCount(CRIMSON_DRAKE_HEART) >= 100) {
			st.setCond(2, true);
			
		}
		
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (st.getState()) {
			case State.COMPLETED:
				htmltext = getAlreadyCompletedMsg(player);
				break;
			case State.CREATED:
				htmltext = (player.getLevel() > 59) ? "31577-0.htm" : "31577-0a.html";
				break;
			case State.STARTED:
				htmltext = (st.isCond(1)) ? "31577-4.html" : "31577-2.html";
				break;
		}
		return htmltext;
	}
}
