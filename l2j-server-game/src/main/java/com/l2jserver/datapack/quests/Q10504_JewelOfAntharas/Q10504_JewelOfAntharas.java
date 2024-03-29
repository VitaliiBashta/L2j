
package com.l2jserver.datapack.quests.Q10504_JewelOfAntharas;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.util.Util;

/**
 * Jewel of Antharas (10504)
 * @author Zoey76
 */
public final class Q10504_JewelOfAntharas extends Quest {
	// NPC
	private static final int THEODRIC = 30755;
	// Monster
	private static final int ANTHARAS = 29068;
	// Items
	private static final int CLEAR_CRYSTAL = 21905;
	private static final int FILLED_CRYSTAL_ANTHARAS_ENERGY = 21907;
	private static final int JEWEL_OF_ANTHARAS = 21898;
	private static final int PORTAL_STONE = 3865;
	// Misc
	private static final int MIN_LEVEL = 84;
	
	public Q10504_JewelOfAntharas() {
		super(10504, Q10504_JewelOfAntharas.class.getSimpleName(), "Jewel of Antharas");
		addStartNpc(THEODRIC);
		addTalkId(THEODRIC);
		addKillId(ANTHARAS);
		registerQuestItems(CLEAR_CRYSTAL, FILLED_CRYSTAL_ANTHARAS_ENERGY);
	}
	
	@Override
	public void actionForEachPlayer(L2PcInstance player, L2Npc npc, boolean isSummon) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && st.isCond(1) && Util.checkIfInRange(1500, npc, player, false)) {
			takeItems(player, CLEAR_CRYSTAL, -1);
			giveItems(player, FILLED_CRYSTAL_ANTHARAS_ENERGY, 1);
			playSound(player, Sound.ITEMSOUND_QUEST_ITEMGET);
			st.setCond(2, true);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = null;
		if ((player.getLevel() >= MIN_LEVEL) && hasQuestItems(player, PORTAL_STONE)) {
			switch (event) {
				case "30755-05.htm":
				case "30755-06.htm": {
					htmltext = event;
					break;
				}
				case "30755-07.html": {
					st.startQuest();
					giveItems(player, CLEAR_CRYSTAL, 1);
					htmltext = event;
					break;
				}
			}
		}
		return htmltext;
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
				if (player.getLevel() < MIN_LEVEL) {
					htmltext = "30755-02.html";
				} else if (!hasQuestItems(player, PORTAL_STONE)) {
					htmltext = "30755-04.html";
				} else {
					htmltext = "30755-01.htm";
				}
				break;
			}
			case State.STARTED: {
				switch (st.getCond()) {
					case 1: {
						if (hasQuestItems(player, CLEAR_CRYSTAL)) {
							htmltext = "30755-08.html";
						} else {
							giveItems(player, CLEAR_CRYSTAL, 1);
							htmltext = "30755-09.html";
						}
						break;
					}
					case 2: {
						giveItems(player, JEWEL_OF_ANTHARAS, 1);
						playSound(player, Sound.ITEMSOUND_QUEST_ITEMGET);
						st.exitQuest(false, true);
						htmltext = "30755-10.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED: {
				htmltext = "30755-03.html";
				break;
			}
		}
		return htmltext;
	}
}
