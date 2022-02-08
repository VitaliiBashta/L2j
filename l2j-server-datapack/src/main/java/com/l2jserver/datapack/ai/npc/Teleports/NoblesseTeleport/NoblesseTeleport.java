
package com.l2jserver.datapack.ai.npc.Teleports.NoblesseTeleport;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Noblesse teleport AI.<br>
 * Original Jython script by Ham Wong.
 * @author Plim
 */
public final class NoblesseTeleport extends AbstractNpcAI {
	// Item
	private static final int OLYMPIAD_TOKEN = 13722;
	// NPCs
	private static final int[] NPCs = {
		30006,
		30059,
		30080,
		30134,
		30146,
		30177,
		30233,
		30256,
		30320,
		30540,
		30576,
		30836,
		30848,
		30878,
		30899,
		31275,
		31320,
		31964,
		32163
	};
	
	public NoblesseTeleport() {
		super(NoblesseTeleport.class.getSimpleName(), "ai/npc/Teleports");
		addStartNpc(NPCs);
		addTalkId(NPCs);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if ("teleportWithToken".equals(event)) {
			if (hasQuestItems(player, OLYMPIAD_TOKEN)) {
				npc.showChatWindow(player, 3);
			} else {
				return "noble-nopass.htm";
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		return player.isNoble() ? "nobleteleporter.htm" : "nobleteleporter-no.htm";
	}
}
