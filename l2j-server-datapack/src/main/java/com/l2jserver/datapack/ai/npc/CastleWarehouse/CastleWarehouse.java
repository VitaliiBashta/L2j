
package com.l2jserver.datapack.ai.npc.CastleWarehouse;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Castle Warehouse Keeper AI.
 * @author malyelfik
 */
public final class CastleWarehouse extends AbstractNpcAI {
	// NPCs
	private static final int[] NPCS = {
		35099, // Warehouse Keeper (Gludio)
		35141, // Warehouse Keeper (Dion)
		35183, // Warehouse Keeper (Giran)
		35225, // Warehouse Keeper (Oren)
		35273, // Warehouse Keeper (Aden)
		35315, // Warehouse Keeper (Inadril)
		35362, // Warehouse Keeper (Goddard)
		35508, // Warehouse Keeper (Rune)
		35554, // Warehouse Keeper (Schuttgart)
	};
	// Items
	private static final int BLOOD_OATH = 9910;
	private static final int BLOOD_ALLIANCE = 9911;
	
	public CastleWarehouse() {
		super(CastleWarehouse.class.getSimpleName(), "ai/npc");
		addStartNpc(NPCS);
		addTalkId(NPCS);
		addFirstTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = event;
		switch (event) {
			case "warehouse-01.html":
			case "warehouse-02.html":
			case "warehouse-03.html":
				break;
			case "warehouse-04.html":
				htmltext = (!npc.isMyLord(player)) ? "warehouse-no.html" : getHtm(player.getHtmlPrefix(), "warehouse-04.html").replace("%blood%", Integer.toString(player.getClan().getBloodAllianceCount()));
				break;
			case "Receive":
				if (!npc.isMyLord(player)) {
					htmltext = "warehouse-no.html";
				} else if (player.getClan().getBloodAllianceCount() == 0) {
					htmltext = "warehouse-05.html";
				} else {
					giveItems(player, BLOOD_ALLIANCE, player.getClan().getBloodAllianceCount());
					player.getClan().resetBloodAllianceCount();
					htmltext = "warehouse-06.html";
				}
				break;
			case "Exchange":
				if (!npc.isMyLord(player)) {
					htmltext = "warehouse-no.html";
				} else if (!hasQuestItems(player, BLOOD_ALLIANCE)) {
					htmltext = "warehouse-08.html";
				} else {
					takeItems(player, BLOOD_ALLIANCE, 1);
					giveItems(player, BLOOD_OATH, 30);
					htmltext = "warehouse-07.html";
				}
				break;
			default:
				htmltext = null;
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		return "warehouse-01.html";
	}
}