
package com.l2jserver.datapack.quests.Q00116_BeyondTheHillsOfWinter;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Beyond the Hills of Winter (116)
 * @author Adry_85
 */
public final class Q00116_BeyondTheHillsOfWinter extends Quest {
	// NPCs
	private static final int FILAUR = 30535;
	private static final int OBI = 32052;
	// Items
	private static final ItemHolder THIEF_KEY = new ItemHolder(1661, 10);
	private static final ItemHolder BANDAGE = new ItemHolder(1833, 20);
	private static final ItemHolder ENERGY_STONE = new ItemHolder(5589, 5);
	private static final int SUPPLYING_GOODS = 8098;
	// Reward
	private static final int SOULSHOT_D = 1463;
	// Misc
	private static final int MIN_LEVEL = 30;
	
	public Q00116_BeyondTheHillsOfWinter() {
		super(116, Q00116_BeyondTheHillsOfWinter.class.getSimpleName(), "Beyond the Hills of Winter");
		addStartNpc(FILAUR);
		addTalkId(FILAUR, OBI);
		registerQuestItems(SUPPLYING_GOODS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "30535-02.htm": {
				st.startQuest();
				st.setMemoState(1);
				htmltext = event;
				break;
			}
			case "30535-05.html": {
				if (st.isMemoState(1)) {
					st.setMemoState(2);
					st.setCond(2, true);
					st.giveItems(SUPPLYING_GOODS, 1);
					htmltext = event;
				}
				break;
			}
			case "32052-02.html": {
				if (st.isMemoState(2)) {
					htmltext = event;
				}
				break;
			}
			case "MATERIAL": {
				if (st.isMemoState(2)) {
					st.rewardItems(SOULSHOT_D, 1740);
					st.addExpAndSp(82792, 4981);
					st.exitQuest(false, true);
					htmltext = "32052-03.html";
				}
				break;
			}
			case "ADENA": {
				if (st.isMemoState(2)) {
					st.giveAdena(17387, true);
					st.addExpAndSp(82792, 4981);
					st.exitQuest(false, true);
					htmltext = "32052-03.html";
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
		switch (st.getState()) {
			case State.COMPLETED: {
				if (npc.getId() == FILAUR) {
					htmltext = getAlreadyCompletedMsg(player);
				}
				break;
			}
			case State.CREATED: {
				if (npc.getId() == FILAUR) {
					htmltext = (player.getLevel() >= MIN_LEVEL) ? "30535-01.htm" : "30535-03.htm";
				}
				break;
			}
			case State.STARTED: {
				switch (npc.getId()) {
					case FILAUR: {
						if (st.isMemoState(1)) {
							htmltext = (hasAllItems(player, true, THIEF_KEY, BANDAGE, ENERGY_STONE)) ? "30535-04.html" : "30535-06.html";
						} else if (st.isMemoState(2)) {
							htmltext = "30535-07.html";
						}
						break;
					}
					case OBI: {
						if (st.isMemoState(2) && st.hasQuestItems(SUPPLYING_GOODS)) {
							htmltext = "32052-01.html";
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
