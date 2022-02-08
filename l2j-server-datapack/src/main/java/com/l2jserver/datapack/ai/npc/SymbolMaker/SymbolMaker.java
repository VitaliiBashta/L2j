
package com.l2jserver.datapack.ai.npc.SymbolMaker;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.HennaEquipList;
import com.l2jserver.gameserver.network.serverpackets.HennaRemoveList;

/**
 * Symbol Maker AI.
 * @author Adry_85
 */
public final class SymbolMaker extends AbstractNpcAI {
	// NPCs
	private static final int[] NPCS = {
		31046, // Marsden
		31047, // Kell
		31048, // McDermott
		31049, // Pepper
		31050, // Thora
		31051, // Keach
		31052, // Heid
		31053, // Kidder
		31264, // Olsun
		31308, // Achim
		31953, // Rankar
	};
	
	public SymbolMaker() {
		super(SymbolMaker.class.getSimpleName(), "ai/npc");
		addFirstTalkId(NPCS);
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		switch (event) {
			case "symbol_maker.htm":
			case "symbol_maker-1.htm":
			case "symbol_maker-2.htm":
			case "symbol_maker-3.htm": {
				htmltext = event;
				break;
			}
			case "Draw": {
				player.sendPacket(new HennaEquipList(player));
				break;
			}
			case "Remove": {
				player.sendPacket(new HennaRemoveList(player));
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		return "symbol_maker.htm";
	}
}