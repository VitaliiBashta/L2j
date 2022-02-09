package com.l2jserver.datapack.quests.Q00295_DreamingOfTheSkies;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.util.Util;

public final class Q00295_DreamingOfTheSkies extends Quest {
	// NPC
	private static final int ARIN = 30536;
	// Monster
	private static final int MAGICAL_WEAVER = 20153;
	// Item
	private static final int FLOATING_STONE = 1492;
	// Reward
	private static final int RING_OF_FIREFLY = 1509;
	// Misc
	private static final int MIN_LVL = 11;
	
	public Q00295_DreamingOfTheSkies() {
		super(295, Q00295_DreamingOfTheSkies.class.getSimpleName(), "Dreaming of the Skies");
		addStartNpc(ARIN);
		addTalkId(ARIN);
		addKillId(MAGICAL_WEAVER);
		registerQuestItems(FLOATING_STONE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCreated() && event.equals("30536-03.htm")) {
			qs.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1) && Util.checkIfInRange(1500, npc, killer, true)) {
			if (giveItemRandomly(killer, npc, FLOATING_STONE, (getRandom(100) > 25) ? 1 : 2, 50, 1.0, true)) {
				qs.setCond(2);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker) {
		final QuestState qs = getQuestState(talker, true);
		String html = getNoQuestMsg(talker);
		if (qs.isCreated()) {
			html = (talker.getLevel() >= MIN_LVL) ? "30536-02.htm" : "30536-01.htm";
		} else if (qs.isStarted()) {
			if (qs.isCond(2)) {
				if (hasQuestItems(talker, RING_OF_FIREFLY)) {
					giveAdena(talker, 2400, true);
					html = "30536-06.html";
				} else {
					giveItems(talker, RING_OF_FIREFLY, 1);
					html = "30536-05.html";
				}
				takeItems(talker, FLOATING_STONE, -1);
				qs.exitQuest(true, true);
			} else {
				html = "30536-04.html";
			}
		}
		return html;
	}
}
