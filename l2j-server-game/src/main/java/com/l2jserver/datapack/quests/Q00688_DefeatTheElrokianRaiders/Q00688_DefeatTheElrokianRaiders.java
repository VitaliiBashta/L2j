
package com.l2jserver.datapack.quests.Q00688_DefeatTheElrokianRaiders;

import static com.l2jserver.gameserver.config.Configuration.rates;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Defeat the Elrokian Raiders! (688)
 * @author Adry_85
 */
public class Q00688_DefeatTheElrokianRaiders extends Quest {
	// NPCs
	private static final int ELROKI = 22214;
	private static final int DINN = 32105;
	// Item
	private static final int DINOSAUR_FANG_NECKLACE = 8785;
	// Misc
	private static final int MIN_LEVEL = 75;
	private static final int DROP_RATE = 448;
	
	public Q00688_DefeatTheElrokianRaiders() {
		super(688, Q00688_DefeatTheElrokianRaiders.class.getSimpleName(), "Defeat the Elrokian Raiders!");
		addStartNpc(DINN);
		addTalkId(DINN);
		addKillId(ELROKI);
		registerQuestItems(DINOSAUR_FANG_NECKLACE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "32105-02.htm":
			case "32105-10.html": {
				htmltext = event;
				break;
			}
			case "32105-03.html": {
				st.startQuest();
				htmltext = event;
				break;
			}
			case "32105-06.html": {
				if (st.hasQuestItems(DINOSAUR_FANG_NECKLACE)) {
					st.giveAdena(3000 * st.getQuestItemsCount(DINOSAUR_FANG_NECKLACE), true);
					st.takeItems(DINOSAUR_FANG_NECKLACE, -1);
					htmltext = event;
				}
				break;
			}
			case "donation": {
				if (st.getQuestItemsCount(DINOSAUR_FANG_NECKLACE) < 100) {
					htmltext = "32105-07.html";
				} else {
					if (getRandom(1000) < 500) {
						st.giveAdena(450000, true);
						htmltext = "32105-08.html";
					} else {
						st.giveAdena(150000, true);
						htmltext = "32105-09.html";
					}
					st.takeItems(DINOSAUR_FANG_NECKLACE, 100);
				}
				break;
			}
			case "32105-11.html": {
				if (st.hasQuestItems(DINOSAUR_FANG_NECKLACE)) {
					st.giveAdena(3000 * st.getQuestItemsCount(DINOSAUR_FANG_NECKLACE), true);
				}
				st.exitQuest(true, true);
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		final L2PcInstance partyMember = getRandomPartyMember(player, 1);
		if (partyMember == null) {
			return super.onKill(npc, player, isSummon);
		}
		
		final QuestState st = getQuestState(partyMember, false);
		
		double chance = (DROP_RATE * rates().getRateQuestDrop());
		if (getRandom(1000) < chance) {
			st.rewardItems(DINOSAUR_FANG_NECKLACE, 1);
			st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = (player.getLevel() >= MIN_LEVEL) ? "32105-01.htm" : "32105-04.html";
				break;
			}
			case State.STARTED: {
				htmltext = (st.hasQuestItems(DINOSAUR_FANG_NECKLACE)) ? "32105-05.html" : "32105-12.html";
				break;
			}
		}
		return htmltext;
	}
}
