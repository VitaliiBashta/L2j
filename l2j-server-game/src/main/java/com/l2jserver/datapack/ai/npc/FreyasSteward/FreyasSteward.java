
package com.l2jserver.datapack.ai.npc.FreyasSteward;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Freya's Steward AI.
 * @author Adry_85
 */
public final class FreyasSteward extends AbstractNpcAI {
	// NPC
	private static final int FREYAS_STEWARD = 32029;
	// Location
	private static final Location TELEPORT_LOC = new Location(103045, -124361, -2768);
	// Misc
	private static final int MIN_LEVEL = 82;
	
	public FreyasSteward() {
		super(FreyasSteward.class.getSimpleName(), "ai/npc");
		addStartNpc(FREYAS_STEWARD);
		addFirstTalkId(FREYAS_STEWARD);
		addTalkId(FREYAS_STEWARD);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		return "32029.html";
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		if (player.getLevel() >= MIN_LEVEL) {
			player.teleToLocation(TELEPORT_LOC);
			return null;
		}
		return "32029-1.html";
	}
}