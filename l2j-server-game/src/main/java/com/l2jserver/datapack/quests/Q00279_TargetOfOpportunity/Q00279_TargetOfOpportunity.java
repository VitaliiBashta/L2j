
package com.l2jserver.datapack.quests.Q00279_TargetOfOpportunity;

import static com.l2jserver.gameserver.config.Configuration.rates;

import java.util.Arrays;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Target of Opportunity (279)
 * @author GKR
 */
public final class Q00279_TargetOfOpportunity extends Quest {
	// NPCs
	private static final int JERIAN = 32302;
	private static final int[] MONSTERS = {
		22373,
		22374,
		22375,
		22376
	};
	// Items
	private static final int[] SEAL_COMPONENTS = {
		15517,
		15518,
		15519,
		15520
	};
	private static final int[] SEAL_BREAKERS = {
		15515,
		15516
	};
	
	public Q00279_TargetOfOpportunity() {
		super(279, Q00279_TargetOfOpportunity.class.getSimpleName(), "Target of Opportunity");
		addStartNpc(JERIAN);
		addTalkId(JERIAN);
		addKillId(MONSTERS);
		registerQuestItems(SEAL_COMPONENTS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if ((st == null) || (player.getLevel() < 82)) {
			return getNoQuestMsg(player);
		}
		
		if (event.equalsIgnoreCase("32302-05.html")) {
			st.startQuest();
			st.set("progress", "1");
		} else if (event.equalsIgnoreCase("32302-08.html") && (st.getInt("progress") == 1) && st.hasQuestItems(SEAL_COMPONENTS[0]) && st.hasQuestItems(SEAL_COMPONENTS[1]) && st.hasQuestItems(SEAL_COMPONENTS[2]) && st.hasQuestItems(SEAL_COMPONENTS[3])) {
			st.giveItems(SEAL_BREAKERS[0], 1);
			st.giveItems(SEAL_BREAKERS[1], 1);
			st.exitQuest(true, true);
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		L2PcInstance pl = getRandomPartyMember(player, "progress", "1");
		final int idx = Arrays.binarySearch(MONSTERS, npc.getId());
		if ((pl == null) || (idx < 0)) {
			return null;
		}
		
		final QuestState st = getQuestState(pl, false);
		if (getRandom(1000) < (int) (311 * rates().getRateQuestDrop())) {
			if (!st.hasQuestItems(SEAL_COMPONENTS[idx])) {
				st.giveItems(SEAL_COMPONENTS[idx], 1);
				if (haveAllExceptThis(st, idx)) {
					st.setCond(2, true);
				} else {
					st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
		}
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		if (st.getState() == State.CREATED) {
			htmltext = (player.getLevel() >= 82) ? "32302-01.htm" : "32302-02.html";
		} else if ((st.getState() == State.STARTED) && (st.getInt("progress") == 1)) {
			htmltext = (st.hasQuestItems(SEAL_COMPONENTS[0]) && st.hasQuestItems(SEAL_COMPONENTS[1]) && st.hasQuestItems(SEAL_COMPONENTS[2]) && st.hasQuestItems(SEAL_COMPONENTS[3])) ? "32302-07.html" : "32302-06.html";
		}
		return htmltext;
	}
	
	private static boolean haveAllExceptThis(QuestState st, int idx) {
		for (int i = 0; i < SEAL_COMPONENTS.length; i++) {
			if (i == idx) {
				continue;
			}
			
			if (!st.hasQuestItems(SEAL_COMPONENTS[i])) {
				return false;
			}
		}
		return true;
	}
}
