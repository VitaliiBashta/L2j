package com.l2jserver.datapack.quests.Q00040_ASpecialOrder;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

public final class Q00040_ASpecialOrder extends Quest {
	// NPCs
	private static final int HELVETIA = 30081;
	private static final int OFULLE = 31572;
	private static final int GESTO = 30511;
	// Items
	private static final int ORANGE_SWIFT_FISH = 6450;
	private static final int ORANGE_UGLY_FISH = 6451;
	private static final int ORANGE_WIDE_FISH = 6452;
	private static final int GOLDEN_COBOL = 5079;
	private static final int BUR_COBOL = 5082;
	private static final int GREAT_COBOL = 5084;
	private static final int WONDROUS_CUBIC = 10632;
	private static final int BOX_OF_FISH = 12764;
	private static final int BOX_OF_SEED = 12765;
	// Misc
	private static final int MIN_LVL = 40;
	
	public Q00040_ASpecialOrder() {
		super(40, Q00040_ASpecialOrder.class.getSimpleName(), "A Special Order");
		addStartNpc(HELVETIA);
		addTalkId(HELVETIA, OFULLE, GESTO);
		registerQuestItems(BOX_OF_FISH, BOX_OF_SEED);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		String htmltext = null;
		switch (event) {
			case "accept": {
				st.startQuest();
				if (getRandomBoolean()) {
					st.setCond(2);
					htmltext = "30081-03.html";
				} else {
					st.setCond(5);
					htmltext = "30081-04.html";
				}
				break;
			}
			case "30081-07.html": {
				if (st.isCond(4) && st.hasQuestItems(BOX_OF_FISH)) {
					st.rewardItems(WONDROUS_CUBIC, 1);
					st.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
			case "30081-10.html": {
				if (st.isCond(7) && st.hasQuestItems(BOX_OF_SEED)) {
					st.rewardItems(WONDROUS_CUBIC, 1);
					st.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
			case "31572-02.html":
			case "30511-02.html": {
				htmltext = event;
				break;
			}
			case "31572-03.html": {
				if (st.isCond(2)) {
					st.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "30511-03.html": {
				if (st.isCond(5)) {
					st.setCond(6, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (npc.getId()) {
			case HELVETIA: {
				switch (st.getState()) {
					case State.CREATED: {
						htmltext = (player.getLevel() >= MIN_LVL) ? "30081-01.htm" : "30081-02.htm";
						break;
					}
					case State.STARTED: {
						switch (st.getCond()) {
							case 2:
							case 3: {
								htmltext = "30081-05.html";
								break;
							}
							case 4: {
								if (st.hasQuestItems(BOX_OF_FISH)) {
									htmltext = "30081-06.html";
								}
								break;
							}
							case 5:
							case 6: {
								htmltext = "30081-08.html";
								break;
							}
							case 7: {
								if (st.hasQuestItems(BOX_OF_SEED)) {
									htmltext = "30081-09.html";
								}
								break;
							}
						}
						break;
					}
					case State.COMPLETED: {
						htmltext = getAlreadyCompletedMsg(player);
						break;
					}
				}
				break;
			}
			case OFULLE: {
				switch (st.getCond()) {
					case 2: {
						htmltext = "31572-01.html";
						break;
					}
					case 3: {
						if ((st.getQuestItemsCount(ORANGE_SWIFT_FISH) >= 10) && (st.getQuestItemsCount(ORANGE_UGLY_FISH) >= 10) && (st.getQuestItemsCount(ORANGE_WIDE_FISH) >= 10)) {
							st.setCond(4, true);
							st.giveItems(BOX_OF_FISH, 1);
							takeItems(player, 10, ORANGE_SWIFT_FISH, ORANGE_UGLY_FISH, ORANGE_WIDE_FISH);
							htmltext = "31572-05.html";
						} else {
							htmltext = "31572-04.html";
						}
						break;
					}
					case 4: {
						htmltext = "31572-06.html";
						break;
					}
				}
				break;
			}
			case GESTO: {
				switch (st.getCond()) {
					case 5: {
						htmltext = "30511-01.html";
						break;
					}
					case 6: {
						if ((st.getQuestItemsCount(GOLDEN_COBOL) >= 40) && (st.getQuestItemsCount(BUR_COBOL) >= 40) && (st.getQuestItemsCount(GREAT_COBOL) >= 40)) {
							st.setCond(7, true);
							st.giveItems(BOX_OF_SEED, 1);
							takeItems(player, 40, GOLDEN_COBOL, BUR_COBOL, GREAT_COBOL);
							htmltext = "30511-05.html";
						} else {
							htmltext = "30511-04.html";
						}
						break;
					}
					case 7: {
						htmltext = "30511-06.html";
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
}