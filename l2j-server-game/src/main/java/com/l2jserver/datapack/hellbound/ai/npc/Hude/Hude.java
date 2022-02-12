
package com.l2jserver.datapack.hellbound.ai.npc.Hude;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.gameserver.data.xml.impl.MultisellData;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class Hude extends AbstractNpcAI {
	// NPCs
	private static final int HUDE = 32298;
	// Items
	private static final int BASIC_CERT = 9850;
	private static final int STANDART_CERT = 9851;
	private static final int PREMIUM_CERT = 9852;
	private static final int MARK_OF_BETRAYAL = 9676;
	private static final int LIFE_FORCE = 9681;
	private static final int CONTAINED_LIFE_FORCE = 9682;
	private static final int MAP = 9994;
	private static final int STINGER = 10012;
	
	public Hude() {
		super(Hude.class.getSimpleName(), "hellbound/AI/NPC");
		addFirstTalkId(HUDE);
		addStartNpc(HUDE);
		addTalkId(HUDE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		switch (event) {
			case "scertif": {
				if (HellboundEngine.getInstance().getLevel() > 3) {
					if (hasQuestItems(player, BASIC_CERT) && (getQuestItemsCount(player, MARK_OF_BETRAYAL) >= 30) && (getQuestItemsCount(player, STINGER) >= 60)) {
						takeItems(player, MARK_OF_BETRAYAL, 30);
						takeItems(player, STINGER, 60);
						takeItems(player, BASIC_CERT, 1);
						giveItems(player, STANDART_CERT, 1);
						return "32298-04a.htm";
					}
				}
				return "32298-04b.htm";
			}
			case "pcertif": {
				if (HellboundEngine.getInstance().getLevel() > 6) {
					if (hasQuestItems(player, STANDART_CERT) && (getQuestItemsCount(player, LIFE_FORCE) >= 56) && (getQuestItemsCount(player, CONTAINED_LIFE_FORCE) >= 14)) {
						takeItems(player, LIFE_FORCE, 56);
						takeItems(player, CONTAINED_LIFE_FORCE, 14);
						takeItems(player, STANDART_CERT, 1);
						giveItems(player, PREMIUM_CERT, 1);
						giveItems(player, MAP, 1);
						return "32298-06a.htm";
					}
				}
				return "32298-06b.htm";
			}
			case "multisell1": {
				if (hasQuestItems(player, STANDART_CERT) || hasQuestItems(player, PREMIUM_CERT)) {
					MultisellData.getInstance().separateAndSend(322980001, player, npc, false);
				}
				break;
			}
			case "multisell2": {
				if (hasQuestItems(player, PREMIUM_CERT)) {
					MultisellData.getInstance().separateAndSend(322980002, player, npc, false);
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		if (!hasAtLeastOneQuestItem(player, BASIC_CERT, STANDART_CERT, PREMIUM_CERT)) {
			htmltext = "32298-01.htm";
		} else if (hasQuestItems(player, BASIC_CERT) && !hasAtLeastOneQuestItem(player, STANDART_CERT, PREMIUM_CERT)) {
			htmltext = "32298-03.htm";
		} else if (hasQuestItems(player, STANDART_CERT) && !hasQuestItems(player, PREMIUM_CERT)) {
			htmltext = "32298-05.htm";
		} else if (hasQuestItems(player, PREMIUM_CERT)) {
			htmltext = "32298-07.htm";
		}
		return htmltext;
	}
}