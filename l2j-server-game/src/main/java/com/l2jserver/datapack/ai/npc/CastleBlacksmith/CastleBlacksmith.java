
package com.l2jserver.datapack.ai.npc.CastleBlacksmith;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.ClanPrivilege;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Castle Blacksmith AI.
 * @author malyelfik
 */
public final class CastleBlacksmith extends AbstractNpcAI {
	// Blacksmith IDs
	private static final int[] NPCS = {
		35098, // Blacksmith (Gludio)
		35140, // Blacksmith (Dion)
		35182, // Blacksmith (Giran)
		35224, // Blacksmith (Oren)
		35272, // Blacksmith (Aden)
		35314, // Blacksmith (Innadril)
		35361, // Blacksmith (Goddard)
		35507, // Blacksmith (Rune)
		35553, // Blacksmith (Schuttgart)
	};
	
	public CastleBlacksmith() {
		super(CastleBlacksmith.class.getSimpleName(), "ai/npc");
		addStartNpc(NPCS);
		addTalkId(NPCS);
		addFirstTalkId(NPCS);
	}
	
	private boolean hasRights(L2PcInstance player, L2Npc npc) {
		return player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) || npc.isMyLord(player) || ((player.getClanId() == npc.getCastle().getOwnerId()) && player.hasClanPrivilege(ClanPrivilege.CS_MANOR_ADMIN));
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		return (event.equalsIgnoreCase(npc.getId() + "-02.html") && hasRights(player, npc)) ? event : null;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		return (hasRights(player, npc)) ? npc.getId() + "-01.html" : "no.html";
	}
}