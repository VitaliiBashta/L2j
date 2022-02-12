
package com.l2jserver.datapack.hellbound.ai.npc.Budenka;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class Budenka extends AbstractNpcAI {
	// NPCs
	private static final int BUDENKA = 32294;
	// Items
	private static final int STANDART_CERT = 9851;
	private static final int PREMIUM_CERT = 9852;
	
	public Budenka() {
		super(Budenka.class.getSimpleName(), "hellbound/AI/NPC");
		addStartNpc(BUDENKA);
		addFirstTalkId(BUDENKA);
		addTalkId(BUDENKA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		switch (event) {
			case "Budenka-02.html":
			case "Budenka-03.html":
			case "Budenka-04.html":
			case "Budenka-05.html": {
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		if (hasQuestItems(player, STANDART_CERT, PREMIUM_CERT)) {
			htmltext = "Budenka-07.html";
		} else if (hasQuestItems(player, STANDART_CERT)) {
			htmltext = "Budenka-06.html";
		} else {
			htmltext = "Budenka-01.html";
		}
		return htmltext;
	}
}