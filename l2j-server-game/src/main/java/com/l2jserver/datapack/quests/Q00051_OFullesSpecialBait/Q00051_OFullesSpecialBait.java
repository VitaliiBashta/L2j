
package com.l2jserver.datapack.quests.Q00051_OFullesSpecialBait;

import static com.l2jserver.gameserver.config.Configuration.rates;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * O'Fulle's Special Bait (51)<br>
 * Original Jython script by Kilkenny.
 * @author nonom
 */
public class Q00051_OFullesSpecialBait extends Quest {
	// NPCs
	private static final int OFULLE = 31572;
	private static final int FETTERED_SOUL = 20552;
	// Items
	private static final int LOST_BAIT = 7622;
	private static final int ICY_AIR_LURE = 7611;
	
	public Q00051_OFullesSpecialBait() {
		super(51, Q00051_OFullesSpecialBait.class.getSimpleName(), "O'Fulle's Special Bait");
		addStartNpc(OFULLE);
		addTalkId(OFULLE);
		addKillId(FETTERED_SOUL);
		registerQuestItems(LOST_BAIT);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		
		String htmltext = event;
		switch (event) {
			case "31572-03.htm":
				st.startQuest();
				break;
			case "31572-07.html":
				if ((st.isCond(2)) && (st.getQuestItemsCount(LOST_BAIT) >= 100)) {
					htmltext = "31572-06.htm";
					st.giveItems(ICY_AIR_LURE, 4);
					st.exitQuest(false, true);
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
		if (st.getQuestItemsCount(LOST_BAIT) < 100) {
			double chance = 33 * rates().getRateQuestDrop();
			if (getRandom(100) < chance) {
				st.rewardItems(LOST_BAIT, 1);
				st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		
		if (st.getQuestItemsCount(LOST_BAIT) >= 100) {
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
				htmltext = (player.getLevel() >= 36) ? "31572-01.htm" : "31572-02.html";
				break;
			case State.STARTED:
				htmltext = (st.isCond(1)) ? "31572-05.html" : "31572-04.html";
				break;
		}
		return htmltext;
	}
}