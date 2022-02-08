
package com.l2jserver.datapack.ai.npc.CastleSiegeManager;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Castle Siege Manager AI.
 * @author St3eT
 */
public final class CastleSiegeManager extends AbstractNpcAI {
	// NPCs
	private static final int[] SIEGE_MANAGER = {
		35104, // Gludio Castle
		35146, // Dion Castle
		35188, // Giran Castle
		35232, // Oren Castle
		35278, // Aden Castle
		35320, // Innadril Castle
		35367, // Goddard Castle
		35513, // Rune Castle
		35559, // Schuttgard Castle
		35639, // Fortress of the Dead
		35420, // Devastated Castle
	};
	
	public CastleSiegeManager() {
		super(CastleSiegeManager.class.getSimpleName(), "ai/npc");
		addFirstTalkId(SIEGE_MANAGER);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		if (player.isClanLeader() && (player.getClanId() == npc.getCastle().getOwnerId())) {
			if (isInSiege(npc)) {
				htmltext = "CastleSiegeManager.html";
			} else {
				htmltext = "CastleSiegeManager-01.html";
			}
		} else if (isInSiege(npc)) {
			htmltext = "CastleSiegeManager-02.html";
		} else {
			if (npc.getConquerableHall() != null) {
				npc.getConquerableHall().showSiegeInfo(player);
			} else {
				npc.getCastle().getSiege().listRegisterClan(player);
			}
		}
		return htmltext;
	}
	
	private boolean isInSiege(L2Npc npc) {
		if ((npc.getConquerableHall() != null) && npc.getConquerableHall().isInSiege()) {
			return true;
		} else if (npc.getCastle().getSiege().isInProgress()) {
			return true;
		}
		return false;
	}
}