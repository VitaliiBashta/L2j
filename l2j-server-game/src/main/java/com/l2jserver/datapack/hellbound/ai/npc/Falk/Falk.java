
package com.l2jserver.datapack.hellbound.ai.npc.Falk;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class Falk extends AbstractNpcAI {
	// NPCs
	private static final int FALK = 32297;
	// Items
	private static final int DARION_BADGE = 9674;
	private static final int BASIC_CERT = 9850; // Basic Caravan Certificate
	private static final int STANDART_CERT = 9851; // Standard Caravan Certificate
	private static final int PREMIUM_CERT = 9852; // Premium Caravan Certificate
	
	public Falk() {
		super(Falk.class.getSimpleName(), "hellbound/AI/NPC");
		addFirstTalkId(FALK);
		addStartNpc(FALK);
		addTalkId(FALK);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		if (hasAtLeastOneQuestItem(player, BASIC_CERT, STANDART_CERT, PREMIUM_CERT)) {
			return "32297-01a.htm";
		}
		return "32297-01.htm";
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		if (hasAtLeastOneQuestItem(player, BASIC_CERT, STANDART_CERT, PREMIUM_CERT)) {
			return "32297-01a.htm";
		}
		return "32297-02.htm";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equalsIgnoreCase("badges")) {
			if (!hasAtLeastOneQuestItem(player, BASIC_CERT, STANDART_CERT, PREMIUM_CERT)) {
				if (getQuestItemsCount(player, DARION_BADGE) >= 20) {
					takeItems(player, DARION_BADGE, 20);
					giveItems(player, BASIC_CERT, 1);
					return "32297-02a.htm";
				}
				return "32297-02b.htm";
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
}