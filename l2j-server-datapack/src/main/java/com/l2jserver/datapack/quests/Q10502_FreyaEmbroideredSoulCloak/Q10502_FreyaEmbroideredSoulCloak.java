
package com.l2jserver.datapack.quests.Q10502_FreyaEmbroideredSoulCloak;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.util.Util;

/**
 * Freya Embroidered Soul Cloak (10502)
 * @author Zoey76
 */
public class Q10502_FreyaEmbroideredSoulCloak extends Quest {
	// NPC
	private static final int OLF_ADAMS = 32612;
	// Monster
	private static final int FREYA = 29179;
	// Items
	private static final int FREYAS_SOUL_FRAGMENT = 21723;
	private static final int SOUL_CLOAK_OF_FREYA = 21720;
	// Misc
	private static final int MIN_LEVEL = 82;
	private static final int FRAGMENT_COUNT = 20;
	
	public Q10502_FreyaEmbroideredSoulCloak() {
		super(10502, Q10502_FreyaEmbroideredSoulCloak.class.getSimpleName(), "Freya Embroidered Soul Cloak");
		addStartNpc(OLF_ADAMS);
		addTalkId(OLF_ADAMS);
		addKillId(FREYA);
		registerQuestItems(FREYAS_SOUL_FRAGMENT);
	}
	
	@Override
	public void actionForEachPlayer(L2PcInstance player, L2Npc npc, boolean isSummon) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && st.isCond(1) && Util.checkIfInRange(1500, npc, player, false)) {
			final long currentCount = getQuestItemsCount(player, FREYAS_SOUL_FRAGMENT);
			final long count = getRandom(1, 3);
			if (count >= (FRAGMENT_COUNT - currentCount)) {
				giveItems(player, FREYAS_SOUL_FRAGMENT, FRAGMENT_COUNT - currentCount);
				st.setCond(2, true);
			} else {
				giveItems(player, FREYAS_SOUL_FRAGMENT, count);
				playSound(player, Sound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && (player.getLevel() >= MIN_LEVEL) && event.equals("32612-04.html")) {
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		executeForEachPlayer(killer, npc, isSummon, true, true);
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = (player.getLevel() < MIN_LEVEL) ? "32612-02.html" : "32612-01.htm";
				break;
			}
			case State.STARTED: {
				switch (st.getCond()) {
					case 1: {
						htmltext = "32612-05.html";
						break;
					}
					case 2: {
						if (getQuestItemsCount(player, FREYAS_SOUL_FRAGMENT) >= FRAGMENT_COUNT) {
							giveItems(player, SOUL_CLOAK_OF_FREYA, 1);
							playSound(player, Sound.ITEMSOUND_QUEST_ITEMGET);
							st.exitQuest(false, true);
							htmltext = "32612-06.html";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED: {
				htmltext = "32612-03.html";
				break;
			}
		}
		return htmltext;
	}
}
