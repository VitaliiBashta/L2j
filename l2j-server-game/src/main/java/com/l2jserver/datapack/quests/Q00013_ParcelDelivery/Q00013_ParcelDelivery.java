
package com.l2jserver.datapack.quests.Q00013_ParcelDelivery;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Parcel Delivery (13)
 * @author ivantotov
 * @since 2.6.0.0
 */
public final class Q00013_ParcelDelivery extends Quest {
	// NPCs
	private static final int FUNDIN = 31274;
	private static final int VULCAN = 31539;
	// Item
	private static final int PACKAGE_TO_VULCAN = 7263;
	// Misc
	private static final int MIN_LEVEL = 74;
	
	public Q00013_ParcelDelivery() {
		super(13, Q00013_ParcelDelivery.class.getSimpleName(), "Parcel Delivery");
		addStartNpc(FUNDIN);
		addTalkId(FUNDIN, VULCAN);
		registerQuestItems(PACKAGE_TO_VULCAN);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		if (qs == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "31274-03.htm": {
				qs.startQuest();
				qs.setMemoState(11);
				giveItems(player, PACKAGE_TO_VULCAN, 1);
				htmltext = event;
				break;
			}
			case "31539-02.html": {
				if (hasQuestItems(player, PACKAGE_TO_VULCAN)) {
					giveAdena(player, 157834, true);
					addExpAndSp(player, 589092, 58794);
					qs.exitQuest(false, true);
					htmltext = event;
				} else {
					htmltext = "31539-03.html";
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
			if (npc.getId() == FUNDIN) {
				htmltext = (player.getLevel() >= MIN_LEVEL) ? "31274-01.htm" : "31274-02.html";
			}
		} else if (qs.isStarted()) {
			if (npc.getId() == FUNDIN) {
				if (qs.isMemoState(11)) {
					htmltext = "31274-04.html";
				}
			} else if (npc.getId() == VULCAN) {
				if (hasQuestItems(player, PACKAGE_TO_VULCAN) && qs.isMemoState(11)) {
					htmltext = "31539-01.html";
				}
			}
		} else if (qs.isCompleted()) {
			if (npc.getId() == FUNDIN) {
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}
