
package com.l2jserver.datapack.quests.Q00362_BardsMandolin;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Bard's Mandolin (362)
 * @author Adry_85
 */
public final class Q00362_BardsMandolin extends Quest {
	// NPCs
	private static final int WOODROW = 30837;
	private static final int NANARIN = 30956;
	private static final int SWAN = 30957;
	private static final int GALION = 30958;
	// Items
	private static final int SWANS_FLUTE = 4316;
	private static final int SWANS_LETTER = 4317;
	private static final int THEME_OF_JOURNEY = 4410;
	// Misc
	private static final int MIN_LEVEL = 15;
	
	public Q00362_BardsMandolin() {
		super(362, Q00362_BardsMandolin.class.getSimpleName(), "Bard's Mandolin");
		addStartNpc(SWAN);
		addTalkId(SWAN, GALION, WOODROW, NANARIN);
		registerQuestItems(SWANS_FLUTE, SWANS_LETTER);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "30957-02.htm": {
				st.startQuest();
				st.setMemoState(1);
				htmltext = event;
				break;
			}
			case "30957-07.html":
			case "30957-08.html": {
				if (st.isMemoState(5)) {
					st.giveAdena(10000, true);
					st.rewardItems(THEME_OF_JOURNEY, 1);
					st.exitQuest(true, true);
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
		switch (st.getState()) {
			case State.CREATED: {
				if (npc.getId() == SWAN) {
					htmltext = (player.getLevel() >= MIN_LEVEL) ? "30957-01.htm" : "30957-03.html";
				}
				break;
			}
			case State.STARTED: {
				switch (npc.getId()) {
					case SWAN: {
						switch (st.getMemoState()) {
							case 1:
							case 2: {
								htmltext = "30957-04.html";
								break;
							}
							case 3: {
								st.setCond(4, true);
								st.setMemoState(4);
								st.giveItems(SWANS_LETTER, 1);
								htmltext = "30957-05.html";
								break;
							}
							case 4: {
								htmltext = "30957-05.html";
								break;
							}
							case 5: {
								htmltext = "30957-06.html";
								break;
							}
						}
						break;
					}
					case GALION: {
						if (st.isMemoState(2)) {
							st.setMemoState(3);
							st.setCond(3, true);
							st.giveItems(SWANS_FLUTE, 1);
							htmltext = "30958-01.html";
						} else if (st.getMemoState() >= 3) {
							htmltext = "30958-02.html";
						}
						break;
					}
					case WOODROW: {
						if (st.isMemoState(1)) {
							st.setMemoState(2);
							st.setCond(2, true);
							htmltext = "30837-01.html";
						} else if (st.isMemoState(2)) {
							htmltext = "30837-02.html";
						} else if (st.getMemoState() >= 3) {
							htmltext = "30837-03.html";
						}
						break;
					}
					case NANARIN: {
						if (st.isMemoState(4) && st.hasQuestItems(SWANS_FLUTE, SWANS_LETTER)) {
							st.setMemoState(5);
							st.setCond(5, true);
							st.takeItems(SWANS_FLUTE, -1);
							st.takeItems(SWANS_LETTER, -1);
							htmltext = "30956-01.html";
						} else if (st.getMemoState() >= 5) {
							htmltext = "30956-02.html";
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
