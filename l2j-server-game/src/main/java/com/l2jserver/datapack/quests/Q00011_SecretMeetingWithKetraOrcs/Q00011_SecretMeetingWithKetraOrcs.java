
package com.l2jserver.datapack.quests.Q00011_SecretMeetingWithKetraOrcs;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Secret Meeting With Ketra Orcs (11)
 * @author ivantotov
 * @since 2.6.0.0
 */
public final class Q00011_SecretMeetingWithKetraOrcs extends Quest {
	// NPCs
	private static final int LEON = 31256;
	private static final int CADMON = 31296;
	private static final int WAHKAN = 31371;
	// Item
	private static final int MUNITIONS_BOX = 7231;
	// Misc
	private static final int MIN_LEVEL = 74;
	
	public Q00011_SecretMeetingWithKetraOrcs() {
		super(11, Q00011_SecretMeetingWithKetraOrcs.class.getSimpleName(), "Secret Meeting With Ketra Orcs");
		addStartNpc(CADMON);
		addTalkId(CADMON, LEON, WAHKAN);
		registerQuestItems(MUNITIONS_BOX);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		if (qs == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "31296-03.htm": {
				qs.startQuest();
				qs.setMemoState(11);
				htmltext = event;
				break;
			}
			case "31256-02.html": {
				giveItems(player, MUNITIONS_BOX, 1);
				qs.setMemoState(21);
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "31371-02.html": {
				if (hasQuestItems(player, MUNITIONS_BOX)) {
					addExpAndSp(player, 82045, 6047);
					qs.exitQuest(false, true);
					htmltext = event;
				} else {
					htmltext = "31371-03.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated()) {
			if (npc.getId() == CADMON) {
				htmltext = (player.getLevel() >= MIN_LEVEL) ? "31296-01.htm" : "31296-02.html";
			}
		} else if (qs.isStarted()) {
			if (npc.getId() == CADMON) {
				if (qs.isMemoState(11)) {
					htmltext = "31296-04.html";
				}
			} else if (npc.getId() == LEON) {
				if (qs.isMemoState(11)) {
					htmltext = "31256-01.html";
				} else if (qs.isMemoState(21)) {
					htmltext = "31256-03.html";
				}
			} else if (npc.getId() == WAHKAN) {
				if (hasQuestItems(player, MUNITIONS_BOX) && qs.isMemoState(21)) {
					htmltext = "31371-01.html";
				}
			}
		} else if (qs.isCompleted()) {
			if (npc.getId() == CADMON) {
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}
