
package com.l2jserver.datapack.ai.npc.Teleports.Survivor;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;

/**
 * Gracia Survivor teleport AI.<br>
 * Original Jython script by Kerberos.
 * @author Plim
 */
public final class Survivor extends AbstractNpcAI {
	// NPC
	private static final int SURVIVOR = 32632;
	// Misc
	private static final int MIN_LEVEL = 75;
	// Location
	private static final Location TELEPORT = new Location(-149406, 255247, -80);
	
	public Survivor() {
		super(Survivor.class.getSimpleName(), "ai/npc/Teleports");
		addStartNpc(SURVIVOR);
		addTalkId(SURVIVOR);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if ("32632-2.htm".equals(event)) {
			if (player.getLevel() < MIN_LEVEL) {
				event = "32632-3.htm";
			} else if (player.getAdena() < 150000) {
				return event;
			} else {
				takeItems(player, Inventory.ADENA_ID, 150000);
				player.teleToLocation(TELEPORT);
				return null;
			}
		}
		return event;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		return "32632-1.htm";
	}
}
