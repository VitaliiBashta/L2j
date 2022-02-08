
package com.l2jserver.datapack.quests.Q00106_ForgottenTruth;

import com.l2jserver.datapack.quests.Q00281_HeadForTheHills.Q00281_HeadForTheHills;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.network.serverpackets.SocialAction;
import com.l2jserver.gameserver.util.Util;

/**
 * Forgotten Truth (106)
 * @author janiko
 */
public final class Q00106_ForgottenTruth extends Quest {
	// NPCs
	private static final int THIFIELL = 30358;
	private static final int KARTA = 30133;
	// Monster
	private static final int TUMRAN_ORC_BRIGAND = 27070;
	// Items
	private static final int ONYX_TALISMAN1 = 984;
	private static final int ONYX_TALISMAN2 = 985;
	private static final int ANCIENT_SCROLL = 986;
	private static final int ANCIENT_CLAY_TABLET = 987;
	private static final int KARTAS_TRANSLATION = 988;
	// Misc
	private static final int MIN_LVL = 10;
	
	public Q00106_ForgottenTruth() {
		super(106, Q00106_ForgottenTruth.class.getSimpleName(), "Forgotten Truth");
		addStartNpc(THIFIELL);
		addTalkId(THIFIELL, KARTA);
		addKillId(TUMRAN_ORC_BRIGAND);
		registerQuestItems(KARTAS_TRANSLATION, ONYX_TALISMAN1, ONYX_TALISMAN2, ANCIENT_SCROLL, ANCIENT_CLAY_TABLET);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		String htmltext = null;
		if (st == null) {
			return htmltext;
		}
		switch (event) {
			case "30358-04.htm": {
				htmltext = event;
				break;
			}
			case "30358-05.htm": {
				if (st.isCreated()) {
					st.startQuest();
					st.giveItems(ONYX_TALISMAN1, 1);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && st.isCond(2) && Util.checkIfInRange(1500, npc, killer, true)) {
			if ((getRandom(100) < 20) && st.hasQuestItems(ONYX_TALISMAN2)) {
				if (!st.hasQuestItems(ANCIENT_SCROLL)) {
					st.giveItems(ANCIENT_SCROLL, 1);
					st.playSound(Sound.ITEMSOUND_QUEST_MIDDLE);
				} else if (!st.hasQuestItems(ANCIENT_CLAY_TABLET)) {
					st.setCond(3, true);
					st.giveItems(ANCIENT_CLAY_TABLET, 1);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker) {
		final QuestState st = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		switch (npc.getId()) {
			case THIFIELL: {
				switch (st.getState()) {
					case State.CREATED: {
						if (talker.getRace() == Race.DARK_ELF) {
							htmltext = talker.getLevel() >= MIN_LVL ? "30358-03.htm" : "30358-02.htm";
						} else {
							htmltext = "30358-01.htm";
						}
						break;
					}
					case State.STARTED: {
						if (hasAtLeastOneQuestItem(talker, ONYX_TALISMAN1, ONYX_TALISMAN2) && !st.hasQuestItems(KARTAS_TRANSLATION)) {
							htmltext = "30358-06.html";
						} else if (st.isCond(4) && st.hasQuestItems(KARTAS_TRANSLATION)) {
							Q00281_HeadForTheHills.giveNewbieReward(talker);
							talker.sendPacket(new SocialAction(talker.getObjectId(), 3));
							st.giveAdena(10266, true);
							st.addExpAndSp(24195, 2074);
							st.exitQuest(false, true);
							htmltext = "30358-07.html";
						}
						break;
					}
					case State.COMPLETED: {
						htmltext = getAlreadyCompletedMsg(talker);
						break;
					}
				}
				break;
			}
			case KARTA: {
				if (st.isStarted()) {
					switch (st.getCond()) {
						case 1: {
							if (st.hasQuestItems(ONYX_TALISMAN1)) {
								st.setCond(2, true);
								st.takeItems(ONYX_TALISMAN1, -1);
								st.giveItems(ONYX_TALISMAN2, 1);
								htmltext = "30133-01.html";
							}
							break;
						}
						case 2: {
							if (st.hasQuestItems(ONYX_TALISMAN2)) {
								htmltext = "30133-02.html";
							}
							break;
						}
						case 3: {
							if (st.hasQuestItems(ANCIENT_SCROLL, ANCIENT_CLAY_TABLET)) {
								st.setCond(4, true);
								takeItems(talker, -1, ANCIENT_SCROLL, ANCIENT_CLAY_TABLET, ONYX_TALISMAN2);
								st.giveItems(KARTAS_TRANSLATION, 1);
								htmltext = "30133-03.html";
							}
							break;
						}
						case 4: {
							if (st.hasQuestItems(KARTAS_TRANSLATION)) {
								htmltext = "30133-04.html";
							}
							break;
						}
						
					}
				}
				break;
			}
		}
		return htmltext;
	}
}