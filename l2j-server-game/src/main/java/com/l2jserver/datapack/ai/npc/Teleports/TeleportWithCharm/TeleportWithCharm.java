
package com.l2jserver.datapack.ai.npc.Teleports.TeleportWithCharm;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Charm teleport AI.<br>
 * Original Jython script by DraX.
 * @author Plim
 */
public final class TeleportWithCharm extends AbstractNpcAI {
	// NPCs
	private final static int WHIRPY = 30540;
	private final static int TAMIL = 30576;
	// Items
	private final static int ORC_GATEKEEPER_CHARM = 1658;
	private final static int DWARF_GATEKEEPER_TOKEN = 1659;
	// Locations
	private final static Location ORC_TELEPORT = new Location(-80826, 149775, -3043);
	private final static Location DWARF_TELEPORT = new Location(-80826, 149775, -3043);
	
	public TeleportWithCharm() {
		super(TeleportWithCharm.class.getSimpleName(), "ai/npc/Teleports");
		addStartNpc(WHIRPY, TAMIL);
		addTalkId(WHIRPY, TAMIL);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		switch (npc.getId()) {
			case WHIRPY: {
				if (hasQuestItems(player, DWARF_GATEKEEPER_TOKEN)) {
					takeItems(player, DWARF_GATEKEEPER_TOKEN, 1);
					player.teleToLocation(DWARF_TELEPORT);
				} else {
					return "30540-01.htm";
				}
				break;
			}
			case TAMIL: {
				if (hasQuestItems(player, ORC_GATEKEEPER_CHARM)) {
					takeItems(player, ORC_GATEKEEPER_CHARM, 1);
					player.teleToLocation(ORC_TELEPORT);
				} else {
					return "30576-01.htm";
				}
				break;
			}
		}
		return super.onTalk(npc, player);
	}
}
