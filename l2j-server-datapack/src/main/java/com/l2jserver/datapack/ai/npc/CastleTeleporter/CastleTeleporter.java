
package com.l2jserver.datapack.ai.npc.CastleTeleporter;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.instancemanager.MapRegionManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Siege;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;

/**
 * Castle Teleporter AI.
 * @author malyelfik
 */
public final class CastleTeleporter extends AbstractNpcAI {
	// Teleporter IDs
	private static final int[] NPCS = {
		35095, // Mass Gatekeeper (Gludio)
		35137, // Mass Gatekeeper (Dion)
		35179, // Mass Gatekeeper (Giran)
		35221, // Mass Gatekeeper (Oren)
		35266, // Mass Gatekeeper (Aden)
		35311, // Mass Gatekeeper (Innadril)
		35355, // Mass Gatekeeper (Goddard)
		35502, // Mass Gatekeeper (Rune)
		35547, // Mass Gatekeeper (Schuttgart)
	};
	
	public CastleTeleporter() {
		super(CastleTeleporter.class.getSimpleName(), "ai/npc");
		addStartNpc(NPCS);
		addTalkId(NPCS);
		addFirstTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equalsIgnoreCase("teleporter-03.html")) {
			if (npc.isScriptValue(0)) {
				final Siege siege = npc.getCastle().getSiege();
				final int time = (siege.isInProgress() && (siege.getControlTowerCount() == 0)) ? 480000 : 30000;
				startQuestTimer("teleport", time, npc, null);
				npc.setScriptValue(1);
			}
			return event;
		} else if (event.equalsIgnoreCase("teleport")) {
			final int region = MapRegionManager.getInstance().getMapRegionLocId(npc.getX(), npc.getY());
			final NpcSay msg = new NpcSay(npc, Say2.NPC_SHOUT, NpcStringId.THE_DEFENDERS_OF_S1_CASTLE_WILL_BE_TELEPORTED_TO_THE_INNER_CASTLE);
			msg.addStringParameter(npc.getCastle().getName());
			npc.getCastle().oustAllPlayers();
			npc.setScriptValue(0);
			// TODO: Is it possible to get all the players for that region, instead of all players?
			for (L2PcInstance pl : L2World.getInstance().getPlayers()) {
				if (region == MapRegionManager.getInstance().getMapRegionLocId(pl)) {
					pl.sendPacket(msg);
				}
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		final Siege siege = npc.getCastle().getSiege();
		return (npc.isScriptValue(0)) ? (siege.isInProgress() && (siege.getControlTowerCount() == 0)) ? "teleporter-02.html" : "teleporter-01.html" : "teleporter-03.html";
	}
}