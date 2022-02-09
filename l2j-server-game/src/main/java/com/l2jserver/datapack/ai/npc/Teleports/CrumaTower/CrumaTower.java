
package com.l2jserver.datapack.ai.npc.Teleports.CrumaTower;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Cruma Tower teleport AI.
 * @author Plim
 */
public final class CrumaTower extends AbstractNpcAI {
	// NPC
	private static final int MOZELLA = 30483;
	// Locations
	private static final Location TELEPORT_LOC1 = new Location(17776, 113968, -11671);
	private static final Location TELEPORT_LOC2 = new Location(17680, 113968, -11671);
	// Misc
	private static final int MAX_LEVEL = 55;
	
	public CrumaTower() {
		super(CrumaTower.class.getSimpleName(), "ai/npc/Teleports");
		addFirstTalkId(MOZELLA);
		addStartNpc(MOZELLA);
		addTalkId(MOZELLA);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker) {
		if (talker.getLevel() <= MAX_LEVEL) {
			talker.teleToLocation(getRandomBoolean() ? TELEPORT_LOC1 : TELEPORT_LOC2);
			return null;
		}
		return "30483-1.html";
	}
}