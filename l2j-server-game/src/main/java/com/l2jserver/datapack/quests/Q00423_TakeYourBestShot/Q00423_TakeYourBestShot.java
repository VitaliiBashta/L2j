
package com.l2jserver.datapack.quests.Q00423_TakeYourBestShot;

import com.l2jserver.datapack.quests.Q00249_PoisonedPlainsOfTheLizardmen.Q00249_PoisonedPlainsOfTheLizardmen;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.util.Util;

/**
 * Take Your Best Shot (423)
 * @author ivantotov
 */
public final class Q00423_TakeYourBestShot extends Quest {
	// NPCs
	private static final int BATRACOS = 32740;
	private static final int JOHNNY = 32744;
	// Monster
	private static final int TANTA_GUARD = 18862;
	// Item
	private static final int SEER_UGOROS_PASS = 15496;
	// Misc
	private static final int MIN_LEVEL = 82;
	
	public Q00423_TakeYourBestShot() {
		super(423, Q00423_TakeYourBestShot.class.getSimpleName(), "Take Your Best Shot!");
		addStartNpc(JOHNNY, BATRACOS);
		addTalkId(JOHNNY, BATRACOS);
		addKillId(TANTA_GUARD);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		if (qs == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "32744-06.htm": {
				if (qs.isCreated() && (player.getLevel() >= MIN_LEVEL)) {
					qs.startQuest();
					qs.setMemoState(1);
					htmltext = event;
				}
				break;
			}
			case "32744-04.html":
			case "32744-05.htm": {
				if (!hasQuestItems(player, SEER_UGOROS_PASS) && (player.getLevel() >= MIN_LEVEL)) {
					htmltext = event;
				}
				break;
			}
			case "32744-07.html": {
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && Util.checkIfInRange(1500, npc, killer, true)) {
			switch (npc.getId()) {
				case TANTA_GUARD: {
					if ((qs.isMemoState(1)) && !hasQuestItems(killer, SEER_UGOROS_PASS)) {
						qs.setMemoState(2);
						qs.setCond(2, true);
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated()) {
			if (npc.getId() == JOHNNY) {
				if (hasQuestItems(player, SEER_UGOROS_PASS)) {
					htmltext = "32744-02.htm";
				} else {
					htmltext = ((player.getLevel() >= MIN_LEVEL) && player.hasQuestCompleted(Q00249_PoisonedPlainsOfTheLizardmen.class.getSimpleName())) ? "32744-03.htm" : "32744-01.htm";
				}
			} else if (npc.getId() == BATRACOS) {
				htmltext = (qs.hasQuestItems(SEER_UGOROS_PASS)) ? "32740-04.html" : "32740-01.html";
			}
		} else if (qs.isStarted()) {
			switch (npc.getId()) {
				case JOHNNY: {
					if (qs.isMemoState(1)) {
						htmltext = "32744-08.html";
					} else if (qs.isMemoState(2)) {
						htmltext = "32744-09.html";
					}
					break;
				}
				case BATRACOS: {
					if (qs.isMemoState(1)) {
						htmltext = "32740-02.html";
					} else if (qs.isMemoState(2)) {
						giveItems(player, SEER_UGOROS_PASS, 1);
						qs.exitQuest(true, true);
						htmltext = "32740-03.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
}
