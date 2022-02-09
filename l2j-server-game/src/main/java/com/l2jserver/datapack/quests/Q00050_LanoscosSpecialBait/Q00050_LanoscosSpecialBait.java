
package com.l2jserver.datapack.quests.Q00050_LanoscosSpecialBait;

import static com.l2jserver.gameserver.config.Configuration.rates;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Lanosco's Special Bait (50)<br>
 * Original Jython script by Kilkenny.
 * @author nonom
 */
public class Q00050_LanoscosSpecialBait extends Quest {
	// NPCs
	private static final int LANOSCO = 31570;
	private static final int SINGING_WIND = 21026;
	// Items
	private static final int ESSENCE_OF_WIND = 7621;
	private static final int WIND_FISHING_LURE = 7610;
	
	public Q00050_LanoscosSpecialBait() {
		super(50, Q00050_LanoscosSpecialBait.class.getSimpleName(), "Lanosco's Special Bait");
		addStartNpc(LANOSCO);
		addTalkId(LANOSCO);
		addKillId(SINGING_WIND);
		registerQuestItems(ESSENCE_OF_WIND);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		
		String htmltext = event;
		
		switch (event) {
			case "31570-03.htm":
				st.startQuest();
				break;
			case "31570-07.html":
				if ((st.isCond(2)) && (st.getQuestItemsCount(ESSENCE_OF_WIND) >= 100)) {
					htmltext = "31570-06.htm";
					st.giveItems(WIND_FISHING_LURE, 4);
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
		
		if (st.getQuestItemsCount(ESSENCE_OF_WIND) < 100) {
			double chance = 33 * rates().getRateQuestDrop();
			if (getRandom(100) < chance) {
				st.rewardItems(ESSENCE_OF_WIND, 1);
				st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		
		if (st.getQuestItemsCount(ESSENCE_OF_WIND) >= 100) {
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
				htmltext = (player.getLevel() >= 27) ? "31570-01.htm" : "31570-02.html";
				break;
			case State.STARTED:
				htmltext = (st.isCond(1)) ? "31570-05.html" : "31570-04.html";
				break;
		}
		return htmltext;
	}
}
