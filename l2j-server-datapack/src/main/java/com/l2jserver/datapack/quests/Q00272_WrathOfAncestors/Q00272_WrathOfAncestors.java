
package com.l2jserver.datapack.quests.Q00272_WrathOfAncestors;

import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Wrath of Ancestors (272)
 * @author xban1x
 */
public final class Q00272_WrathOfAncestors extends Quest {
	// NPC
	private static final int LIVINA = 30572;
	// Items
	private static final int GRAVE_ROBBERS_HEAD = 1474;
	// Monsters
	private static final int[] MONSTERS = new int[] {
		20319, // Goblin Grave Robber
		20320, // Goblin Tomb Raider Leader
	};
	// Misc
	private static final int MIN_LVL = 5;
	
	public Q00272_WrathOfAncestors() {
		super(272, Q00272_WrathOfAncestors.class.getSimpleName(), "Wrath of Ancestors");
		addStartNpc(LIVINA);
		addTalkId(LIVINA);
		addKillId(MONSTERS);
		registerQuestItems(GRAVE_ROBBERS_HEAD);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equalsIgnoreCase("30572-04.htm")) {
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && st.isCond(1)) {
			st.giveItems(GRAVE_ROBBERS_HEAD, 1);
			if (st.getQuestItemsCount(GRAVE_ROBBERS_HEAD) >= 50) {
				st.setCond(2, true);
			} else {
				st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = null;
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = (player.getRace() == Race.ORC) ? (player.getLevel() >= MIN_LVL) ? "30572-03.htm" : "30572-02.htm" : "30572-01.htm";
				break;
			}
			case State.STARTED: {
				switch (st.getCond()) {
					case 1: {
						htmltext = "30572-05.html";
						break;
					}
					case 2: {
						st.giveAdena(1500, true);
						st.exitQuest(true, true);
						htmltext = "30572-06.html";
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
}