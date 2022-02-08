
package com.l2jserver.datapack.ai.npc.Teleports.ElrokiTeleporters;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Elroki teleport AI.
 * @author Plim
 */
public final class ElrokiTeleporters extends AbstractNpcAI {
	// NPCs
	private static final int ORAHOCHIN = 32111;
	private static final int GARIACHIN = 32112;
	// Locations
	private static final Location TELEPORT_ORAHOCIN = new Location(5171, -1889, -3165);
	private static final Location TELEPORT_GARIACHIN = new Location(7651, -5416, -3155);
	
	public ElrokiTeleporters() {
		super(ElrokiTeleporters.class.getSimpleName(), "ai/npc/Teleports");
		addFirstTalkId(ORAHOCHIN, GARIACHIN);
		addStartNpc(ORAHOCHIN, GARIACHIN);
		addTalkId(ORAHOCHIN, GARIACHIN);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker) {
		if (!talker.isInCombat()) {
			talker.teleToLocation((npc.getId() == ORAHOCHIN) ? TELEPORT_ORAHOCIN : TELEPORT_GARIACHIN);
		} else {
			return npc.getId() + "-no.html";
		}
		return super.onTalk(npc, talker);
	}
}