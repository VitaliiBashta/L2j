
package com.l2jserver.datapack.quests.Q00641_AttackSailren;

import com.l2jserver.datapack.quests.Q00126_TheNameOfEvil2.Q00126_TheNameOfEvil2;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Attack Sailren! (641)
 * @author Adry_85
 */
public class Q00641_AttackSailren extends Quest {
	// NPC
	private static final int SHILENS_STONE_STATUE = 32109;
	// Items
	public static final int GAZKH_FRAGMENT = 8782;
	public static final int GAZKH = 8784;
	
	public static int[] MOBS = {
		22196, // Velociraptor
		22197, // Velociraptor
		22198, // Velociraptor
		22218, // Velociraptor
		22223, // Velociraptor
		22199, // Pterosaur
	};
	
	public Q00641_AttackSailren() {
		super(641, Q00641_AttackSailren.class.getSimpleName(), "Attack Sailren!");
		addStartNpc(SHILENS_STONE_STATUE);
		addTalkId(SHILENS_STONE_STATUE);
		addKillId(MOBS);
		registerQuestItems(GAZKH_FRAGMENT);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		
		switch (event) {
			case "32109-1.html":
				st.startQuest();
				break;
			case "32109-2a.html":
				if (st.getQuestItemsCount(GAZKH_FRAGMENT) >= 30) {
					st.giveItems(GAZKH, 1);
					st.exitQuest(true, true);
				}
				break;
		}
		return event;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		final L2PcInstance partyMember = getRandomPartyMember(player, 1);
		if (partyMember != null) {
			final QuestState st = getQuestState(partyMember, false);
			if (st != null) {
				st.giveItems(GAZKH_FRAGMENT, 1);
				if (st.getQuestItemsCount(GAZKH_FRAGMENT) < 30) {
					st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				} else {
					st.setCond(2, true);
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		QuestState st = getQuestState(player, true);
		switch (st.getState()) {
			case State.CREATED:
				if (player.getLevel() < 77) {
					htmltext = "32109-0.htm";
				} else {
					htmltext = (player.hasQuestCompleted(Q00126_TheNameOfEvil2.class.getSimpleName())) ? "32109-0a.htm" : "32109-0b.htm";
				}
				break;
			case State.STARTED:
				htmltext = (st.isCond(1)) ? "32109-1a.html" : "32109-2.html";
				break;
		}
		return htmltext;
	}
}
