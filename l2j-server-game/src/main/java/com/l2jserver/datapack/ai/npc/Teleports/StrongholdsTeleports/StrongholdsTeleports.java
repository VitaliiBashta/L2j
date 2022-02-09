
package com.l2jserver.datapack.ai.npc.Teleports.StrongholdsTeleports;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Strongholds teleport AI.<br>
 * Original Jython script by Kerberos.
 * @author Plim
 */
public final class StrongholdsTeleports extends AbstractNpcAI {
	// NPCs
	private final static int[] NPCs = {
		32163,
		32181,
		32184,
		32186
	};
	
	public StrongholdsTeleports() {
		super(StrongholdsTeleports.class.getSimpleName(), "ai/npc/Teleports");
		addFirstTalkId(NPCs);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		if (player.getLevel() < 20) {
			return String.valueOf(npc.getId()) + ".htm";
		}
		return String.valueOf(npc.getId()) + "-no.htm";
	}
}
