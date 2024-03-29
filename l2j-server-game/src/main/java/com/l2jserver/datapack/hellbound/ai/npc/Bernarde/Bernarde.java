
package com.l2jserver.datapack.hellbound.ai.npc.Bernarde;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class Bernarde extends AbstractNpcAI {
	// NPCs
	private static final int BERNARDE = 32300;
	// Misc
	private static final int NATIVE_TRANSFORM = 101;
	// Items
	private static final int HOLY_WATER = 9673;
	private static final int DARION_BADGE = 9674;
	private static final int TREASURE = 9684;
	
	public Bernarde() {
		super(Bernarde.class.getSimpleName(), "hellbound/AI/NPC");
		addFirstTalkId(BERNARDE);
		addStartNpc(BERNARDE);
		addTalkId(BERNARDE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		switch (event) {
			case "HolyWater": {
				if (HellboundEngine.getInstance().getLevel() == 2) {
					if (player.getInventory().getInventoryItemCount(DARION_BADGE, -1, false) >= 5) {
						if (player.exchangeItemsById("Quest", npc, DARION_BADGE, 5, HOLY_WATER, 1, true)) {
							return "32300-02b.htm";
						}
					}
				}
				event = "32300-02c.htm";
				break;
			}
			case "Treasure": {
				if ((HellboundEngine.getInstance().getLevel() == 3) && hasQuestItems(player, TREASURE)) {
					HellboundEngine.getInstance().updateTrust((int) (getQuestItemsCount(player, TREASURE) * 1000), true);
					takeItems(player, TREASURE, -1);
					return "32300-02d.htm";
				}
				event = "32300-02e.htm";
				break;
			}
			case "rumors": {
				event = "32300-" + HellboundEngine.getInstance().getLevel() + "r.htm";
				break;
			}
		}
		return event;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		switch (HellboundEngine.getInstance().getLevel()) {
			case 0:
			case 1:
				return isTransformed(player) ? "32300-01a.htm" : "32300-01.htm";
			case 2:
				return isTransformed(player) ? "32300-02.htm" : "32300-03.htm";
			case 3:
				return isTransformed(player) ? "32300-01c.htm" : "32300-03.htm";
			case 4:
				return isTransformed(player) ? "32300-01d.htm" : "32300-03.htm";
			default:
				return isTransformed(player) ? "32300-01f.htm" : "32300-03.htm";
		}
	}
	
	private static boolean isTransformed(L2PcInstance player) {
		return player.isTransformed() && (player.getTransformation().getId() == NATIVE_TRANSFORM);
	}
}