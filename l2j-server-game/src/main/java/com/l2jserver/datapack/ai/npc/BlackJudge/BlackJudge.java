
package com.l2jserver.datapack.ai.npc.BlackJudge;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.EtcStatusUpdate;

/**
 * Black Judge AI.
 * @author St3eT
 */
public class BlackJudge extends AbstractNpcAI {
	// NPC
	private static final int BLACK_JUDGE = 30981;
	// Misc
	// @formatter:off
	private static final int[] COSTS =
	{
		3600, 8640, 25200, 50400, 86400, 144000
	};
	// @formatter:on
	
	public BlackJudge() {
		super(BlackJudge.class.getSimpleName(), "ai/npc");
		addStartNpc(BLACK_JUDGE);
		addTalkId(BLACK_JUDGE);
		addFirstTalkId(BLACK_JUDGE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		final int level = ((player.getExpertiseLevel() < 5) ? player.getExpertiseLevel() : 5);
		switch (event) {
			case "remove_info": {
				htmltext = "30981-0" + (level + 1) + ".html";
				break;
			}
			case "remove_dp": {
				if (player.getDeathPenaltyBuffLevel() > 0) {
					int cost = COSTS[level];
					
					if (player.getAdena() >= cost) {
						takeItems(player, Inventory.ADENA_ID, cost);
						player.setDeathPenaltyBuffLevel(player.getDeathPenaltyBuffLevel() - 1);
						player.sendPacket(SystemMessageId.DEATH_PENALTY_LIFTED);
						player.sendPacket(new EtcStatusUpdate(player));
					} else {
						htmltext = "30981-07.html";
					}
				} else {
					htmltext = "30981-08.html";
				}
				break;
			}
		}
		return htmltext;
	}
}