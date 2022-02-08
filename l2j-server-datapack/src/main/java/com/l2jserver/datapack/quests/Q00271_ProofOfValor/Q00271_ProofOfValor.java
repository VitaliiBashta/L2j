
package com.l2jserver.datapack.quests.Q00271_ProofOfValor;

import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Proof of Valor (271)
 * @author xban1x
 */
public final class Q00271_ProofOfValor extends Quest {
	// NPC
	private static final int RUKAIN = 30577;
	// Items
	private static final int KASHA_WOLF_FANG = 1473;
	// Monsters
	private static final int KASHA_WOLF = 20475;
	// Rewards
	private static final int HEALING_POTION = 1061;
	private static final int NECKLACE_OF_COURAGE = 1506;
	private static final int NECKLACE_OF_VALOR = 1507;
	// Misc
	private static final int MIN_LVL = 4;
	
	public Q00271_ProofOfValor() {
		super(271, Q00271_ProofOfValor.class.getSimpleName(), "Proof of Valor");
		addStartNpc(RUKAIN);
		addTalkId(RUKAIN);
		addKillId(KASHA_WOLF);
		registerQuestItems(KASHA_WOLF_FANG);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equalsIgnoreCase("30577-04.htm")) {
			st.startQuest();
			return hasAtLeastOneQuestItem(player, NECKLACE_OF_VALOR, NECKLACE_OF_COURAGE) ? "30577-08.html" : event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && st.isCond(1)) {
			final long count = st.getQuestItemsCount(KASHA_WOLF_FANG);
			int amount = ((getRandom(100) < 25) && (count < 49)) ? 2 : 1;
			st.giveItems(KASHA_WOLF_FANG, amount);
			if ((count + amount) >= 50) {
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
				htmltext = (player.getRace() == Race.ORC) ? (player.getLevel() >= MIN_LVL) ? (hasAtLeastOneQuestItem(player, NECKLACE_OF_VALOR, NECKLACE_OF_COURAGE)) ? "30577-07.htm" : "30577-03.htm" : "30577-02.htm" : "30577-01.htm";
				break;
			}
			case State.STARTED: {
				switch (st.getCond()) {
					case 1: {
						htmltext = "30577-05.html";
						break;
					}
					case 2: {
						if (st.getQuestItemsCount(KASHA_WOLF_FANG) >= 50) {
							if (getRandom(100) <= 13) {
								st.rewardItems(NECKLACE_OF_VALOR, 1);
								st.rewardItems(HEALING_POTION, 10);
							} else {
								st.rewardItems(NECKLACE_OF_COURAGE, 1);
							}
							st.takeItems(KASHA_WOLF_FANG, -1);
							st.exitQuest(true, true);
							htmltext = "30577-06.html";
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
