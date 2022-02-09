
package com.l2jserver.datapack.quests.Q00137_TempleChampionPart1;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Temple Champion - 1 (137)
 * @author nonom
 */
public class Q00137_TempleChampionPart1 extends Quest {
	// NPCs
	private static final int SYLVAIN = 30070;
	private static final int MOBS[] = {
		20083, // Granite Golem
		20144, // Hangman Tree
		20199, // Amber Basilisk
		20200, // Strain
		20201, // Ghoul
		20202, // Dead Seeker
	};
	// Items
	private static final int FRAGMENT = 10340;
	private static final int EXECUTOR = 10334;
	private static final int MISSIONARY = 10339;
	
	public Q00137_TempleChampionPart1() {
		super(137, Q00137_TempleChampionPart1.class.getSimpleName(), "Temple Champion - 1");
		addStartNpc(SYLVAIN);
		addTalkId(SYLVAIN);
		addKillId(MOBS);
		registerQuestItems(FRAGMENT);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		switch (event) {
			case "30070-02.htm":
				st.startQuest();
				break;
			case "30070-05.html":
				st.set("talk", "1");
				break;
			case "30070-06.html":
				st.set("talk", "2");
				break;
			case "30070-08.html":
				st.unset("talk");
				st.setCond(2, true);
				break;
			case "30070-16.html":
				if (st.isCond(3) && (st.hasQuestItems(EXECUTOR) && st.hasQuestItems(MISSIONARY))) {
					st.takeItems(EXECUTOR, -1);
					st.takeItems(MISSIONARY, -1);
					st.giveAdena(69146, true);
					if (player.getLevel() < 41) {
						st.addExpAndSp(219975, 13047);
					}
					st.exitQuest(false, true);
				}
				break;
		}
		return event;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && st.isStarted() && st.isCond(2) && (st.getQuestItemsCount(FRAGMENT) < 30)) {
			st.giveItems(FRAGMENT, 1);
			if (st.getQuestItemsCount(FRAGMENT) >= 30) {
				st.setCond(3, true);
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
		if (st.isCompleted()) {
			return getAlreadyCompletedMsg(player);
		}
		switch (st.getCond()) {
			case 1:
				switch (st.getInt("talk")) {
					case 1:
						htmltext = "30070-05.html";
						break;
					case 2:
						htmltext = "30070-06.html";
						break;
					default:
						htmltext = "30070-03.html";
						break;
				}
				break;
			case 2:
				htmltext = "30070-08.html";
				break;
			case 3:
				if (st.getInt("talk") == 1) {
					htmltext = "30070-10.html";
				} else if (st.getQuestItemsCount(FRAGMENT) >= 30) {
					st.set("talk", "1");
					htmltext = "30070-09.html";
					st.takeItems(FRAGMENT, -1);
				}
				break;
			default:
				htmltext = ((player.getLevel() >= 35) && st.hasQuestItems(EXECUTOR, MISSIONARY)) ? "30070-01.htm" : "30070-00.html";
				break;
		}
		return htmltext;
	}
}
