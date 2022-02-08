
package com.l2jserver.datapack.events.TheValentineEvent;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.event.LongTimeEvent;

/**
 * The Valentine Event event AI.
 * @author Gnacik
 */
public final class TheValentineEvent extends LongTimeEvent {
	// NPC
	private static final int NPC = 4301;
	// Item
	private static final int RECIPE = 20191;
	// Misc
	private static final String COMPLETED = TheValentineEvent.class.getSimpleName() + "_completed";
	
	private TheValentineEvent() {
		super(TheValentineEvent.class.getSimpleName(), "events");
		addStartNpc(NPC);
		addFirstTalkId(NPC);
		addTalkId(NPC);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = event;
		if (event.equalsIgnoreCase("4301-3.htm")) {
			if (player.getVariables().getBoolean(COMPLETED, false)) {
				htmltext = "4301-4.htm";
			} else {
				giveItems(player, RECIPE, 1);
				playSound(player, Sound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		return npc.getId() + ".htm";
	}
	
	public static void main(String[] args) {
		new TheValentineEvent();
	}
}
