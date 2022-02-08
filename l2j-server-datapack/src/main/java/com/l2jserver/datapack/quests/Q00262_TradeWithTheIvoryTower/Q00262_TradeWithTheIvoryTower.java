
package com.l2jserver.datapack.quests.Q00262_TradeWithTheIvoryTower;

import static com.l2jserver.gameserver.config.Configuration.rates;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Trade With The Ivory Tower (262)
 * @author ivantotov
 */
public final class Q00262_TradeWithTheIvoryTower extends Quest {
	// NPCs
	private static final int VOLLODOS = 30137;
	// Items
	private static final int SPORE_SAC = 707;
	// Misc
	private static final int MIN_LEVEL = 8;
	private static final int REQUIRED_ITEM_COUNT = 10;
	// Monsters
	private static final Map<Integer, Integer> MOBS_SAC = new HashMap<>();
	
	static {
		MOBS_SAC.put(20007, 3); // Green Fungus
		MOBS_SAC.put(20400, 4); // Blood Fungus
	}
	
	public Q00262_TradeWithTheIvoryTower() {
		super(262, Q00262_TradeWithTheIvoryTower.class.getSimpleName(), "Trade With The Ivory Tower");
		addStartNpc(VOLLODOS);
		addTalkId(VOLLODOS);
		addKillId(MOBS_SAC.keySet());
		registerQuestItems(SPORE_SAC);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equalsIgnoreCase("30137-03.htm")) {
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		final L2PcInstance partyMember = getRandomPartyMember(player, 1);
		if (partyMember == null) {
			return super.onKill(npc, player, isSummon);
		}
		
		final QuestState st = getQuestState(partyMember, false);
		double chance = MOBS_SAC.get(npc.getId()) * rates().getRateQuestDrop();
		if (getRandom(10) < chance) {
			st.rewardItems(SPORE_SAC, 1);
			if (st.getQuestItemsCount(SPORE_SAC) >= REQUIRED_ITEM_COUNT) {
				st.setCond(2, true);
			} else {
				st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = player.getLevel() >= MIN_LEVEL ? "30137-02.htm" : "30137-01.htm";
				break;
			}
			case State.STARTED: {
				switch (st.getCond()) {
					case 1: {
						if (st.getQuestItemsCount(SPORE_SAC) < REQUIRED_ITEM_COUNT) {
							htmltext = "30137-04.html";
						}
						break;
					}
					case 2: {
						if (st.getQuestItemsCount(SPORE_SAC) >= REQUIRED_ITEM_COUNT) {
							htmltext = "30137-05.html";
							st.giveAdena(3000, true);
							st.exitQuest(true, true);
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