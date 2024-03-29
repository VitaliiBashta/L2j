
package com.l2jserver.datapack.quests.Q00380_BringOutTheFlavorOfIngredients;

import static com.l2jserver.gameserver.enums.audio.Sound.ITEMSOUND_QUEST_ITEMGET;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.ItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Bring Out the Flavor of Ingredients! (380)
 * @author Pandragon
 */
public final class Q00380_BringOutTheFlavorOfIngredients extends Quest {
	// NPC
	private static final int ROLLAND = 30069;
	// Items
	private static final int ANTIDOTE = 1831;
	private static final int RITRON_FRUIT = 5895;
	private static final int MOON_FLOWER = 5896;
	private static final int LEECH_FLUIDS = 5897;
	// Monsters
	private static final Map<Integer, ItemChanceHolder> MONSTER_CHANCES = new HashMap<>();
	{
		MONSTER_CHANCES.put(20205, new ItemChanceHolder(RITRON_FRUIT, 0.1, 4)); // Dire Wolf
		MONSTER_CHANCES.put(20206, new ItemChanceHolder(MOON_FLOWER, 0.5, 20)); // Kadif Werewolf
		MONSTER_CHANCES.put(20225, new ItemChanceHolder(LEECH_FLUIDS, 0.5, 10)); // Giant Mist Leech
	}
	// Rewards
	private static final int RITRON_RECIPE = 5959;
	private static final int RITRON_DESSERT = 5960;
	// Misc
	private static final int MIN_LVL = 24;
	
	public Q00380_BringOutTheFlavorOfIngredients() {
		super(380, Q00380_BringOutTheFlavorOfIngredients.class.getSimpleName(), "Bring Out the Flavor of Ingredients!");
		addStartNpc(ROLLAND);
		addTalkId(ROLLAND);
		addKillId(MONSTER_CHANCES.keySet());
		registerQuestItems(RITRON_FRUIT, MOON_FLOWER, LEECH_FLUIDS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs != null) {
			switch (event) {
				case "30069-03.htm":
				case "30069-04.htm":
				case "30069-06.html": {
					htmltext = event;
					break;
				}
				case "30069-05.htm": {
					if (qs.isCreated()) {
						qs.startQuest();
						htmltext = event;
					}
					break;
				}
				case "30069-13.html": {
					if (qs.isCond(9)) {
						rewardItems(player, RITRON_RECIPE, 1);
						qs.exitQuest(true, true);
						htmltext = event;
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker) {
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		switch (qs.getState()) {
			case State.CREATED: {
				htmltext = (talker.getLevel() >= MIN_LVL) ? "30069-02.htm" : "30069-01.htm";
				break;
			}
			case State.STARTED: {
				switch (qs.getCond()) {
					case 1:
					case 2:
					case 3:
					case 4: {
						if ((getQuestItemsCount(talker, ANTIDOTE) >= 2) && (getQuestItemsCount(talker, RITRON_FRUIT) >= 4) && (getQuestItemsCount(talker, MOON_FLOWER) >= 20) && (getQuestItemsCount(talker, LEECH_FLUIDS) >= 10)) {
							takeItems(talker, ANTIDOTE, 2);
							takeItems(talker, -1, RITRON_FRUIT, MOON_FLOWER, LEECH_FLUIDS);
							qs.setCond(5, true);
							htmltext = "30069-08.html";
						} else {
							htmltext = "30069-07.html";
						}
						break;
					}
					case 5: {
						qs.setCond(6, true);
						htmltext = "30069-09.html";
						break;
					}
					case 6: {
						qs.setCond(7, true);
						htmltext = "30069-10.html";
						break;
					}
					case 7: {
						qs.setCond(8, true);
						htmltext = "30069-11.html";
						break;
					}
					case 8: {
						rewardItems(talker, RITRON_DESSERT, 1);
						if (getRandom(100) < 56) {
							htmltext = "30069-15.html";
							qs.exitQuest(true, true);
						} else {
							qs.setCond(9, true);
							htmltext = "30069-12.html";
						}
						break;
					}
					case 9: {
						htmltext = "30069-12.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED: {
				htmltext = getAlreadyCompletedMsg(talker);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		if ((qs != null) && (qs.getCond() < 4)) {
			final ItemChanceHolder item = MONSTER_CHANCES.get(npc.getId());
			if (giveItemRandomly(qs.getPlayer(), npc, item.getId(), 1, item.getCount(), item.getChance(), false)) {
				if ((getQuestItemsCount(killer, RITRON_FRUIT) >= 3) && (getQuestItemsCount(killer, MOON_FLOWER) >= 20) && (getQuestItemsCount(killer, LEECH_FLUIDS) >= 10)) {
					qs.setCond(qs.getCond() + 1, true);
				} else {
					playSound(killer, ITEMSOUND_QUEST_ITEMGET);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
