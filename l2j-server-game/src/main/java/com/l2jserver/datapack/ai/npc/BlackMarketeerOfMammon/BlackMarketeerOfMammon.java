
package com.l2jserver.datapack.ai.npc.BlackMarketeerOfMammon;

import java.time.LocalTime;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.enums.QuestType;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Black Marketeer of Mammon - Exchange Adena for AA.
 * @author Adry_85
 */
public final class BlackMarketeerOfMammon extends AbstractNpcAI {
	// NPC
	private static final int BLACK_MARKETEER = 31092;
	// Misc
	private static final int MIN_LEVEL = 60;
	
	public BlackMarketeerOfMammon() {
		super(BlackMarketeerOfMammon.class.getSimpleName(), "ai/npc");
		addStartNpc(BLACK_MARKETEER);
		addTalkId(BLACK_MARKETEER);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker) {
		return exchangeAvailable() ? "31092-01.html" : "31092-02.html";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = event;
		if ("exchange".equals(event)) {
			if (exchangeAvailable()) {
				if (player.getLevel() >= MIN_LEVEL) {
					final QuestState qs = getQuestState(player, true);
					if (!qs.isNowAvailable()) {
						htmltext = "31092-03.html";
					} else {
						if (player.getAdena() >= 2000000) {
							qs.setState(State.STARTED);
							takeItems(player, Inventory.ADENA_ID, 2000000);
							giveItems(player, Inventory.ANCIENT_ADENA_ID, 500000);
							htmltext = "31092-04.html";
							qs.exitQuest(QuestType.DAILY, false);
						} else {
							htmltext = "31092-05.html";
						}
					}
				} else {
					htmltext = "31092-06.html";
				}
			} else {
				htmltext = "31092-02.html";
			}
		}
		
		return htmltext;
	}
	
	private boolean exchangeAvailable() {
		LocalTime localTime = LocalTime.now();
		return (localTime.isAfter(LocalTime.parse("20:00:00")) && localTime.isBefore(LocalTime.MAX));
	}
}
