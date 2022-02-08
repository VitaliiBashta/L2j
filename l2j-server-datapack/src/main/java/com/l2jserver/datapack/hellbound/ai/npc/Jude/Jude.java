
package com.l2jserver.datapack.hellbound.ai.npc.Jude;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Jude AI.
 * @author DS
 */
public final class Jude extends AbstractNpcAI {
	// NPCs
	private static final int JUDE = 32356;
	private static final int NATIVE_TREASURE = 9684;
	private static final int RING_OF_WIND_MASTERY = 9677;
	
	public Jude() {
		super(Jude.class.getSimpleName(), "hellbound/AI/NPC");
		addFirstTalkId(JUDE);
		addStartNpc(JUDE);
		addTalkId(JUDE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if ("TreasureSacks".equalsIgnoreCase(event)) {
			if (HellboundEngine.getInstance().getLevel() == 3) {
				if (getQuestItemsCount(player, NATIVE_TREASURE) >= 40) {
					takeItems(player, NATIVE_TREASURE, 40);
					giveItems(player, RING_OF_WIND_MASTERY, 1);
					return "32356-02.htm";
				}
			}
			return "32356-02a.htm";
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		switch (HellboundEngine.getInstance().getLevel()) {
			case 0:
			case 1:
			case 2:
				return "32356-01.htm";
			case 3:
			case 4:
				return "32356-01c.htm";
			case 5:
				return "32356-01a.htm";
			default:
				return "32356-01b.htm";
		}
	}
}