
package com.l2jserver.datapack.gracia.ai.npc.FortuneTelling;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import org.springframework.stereotype.Service;

@Service
public class FortuneTelling extends AbstractNpcAI {
	// NPC
	private static final int MINE = 32616;
	// Misc
	private static final int COST = 1000;
	
	public FortuneTelling() {
		super(FortuneTelling.class.getSimpleName(), "gracia/AI/NPC");
		addStartNpc(MINE);
		addTalkId(MINE);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		if (player.getAdena() < COST) {
			htmltext = "lowadena.htm";
		} else {
			takeItems(player, Inventory.ADENA_ID, COST);
			htmltext = getHtm(player.getHtmlPrefix(), "fortune.htm").replace("%fortune%", String.valueOf(getRandom(1800309, 1800695)));
		}
		return htmltext;
	}
}