
package com.l2jserver.datapack.hellbound.ai.npc.Kief;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class Kief extends AbstractNpcAI {
	// NPCs
	private static final int KIEF = 32354;
	// Items
	private static final int BOTTLE = 9672; // Magic Bottle
	private static final int DARION_BADGE = 9674; // Darion's Badge
	private static final int DIM_LIFE_FORCE = 9680; // Dim Life Force
	private static final int LIFE_FORCE = 9681; // Life Force
	private static final int CONTAINED_LIFE_FORCE = 9682; // Contained Life Force
	private static final int STINGER = 10012; // Scorpion Poison Stinger
	
	public Kief() {
		super(Kief.class.getSimpleName(), "hellbound/AI/NPC");
		addFirstTalkId(KIEF);
		addStartNpc(KIEF);
		addTalkId(KIEF);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		switch (event) {
			case "Badges": {
				switch (HellboundEngine.getInstance().getLevel()) {
					case 2:
					case 3: {
						if (hasQuestItems(player, DARION_BADGE)) {
							HellboundEngine.getInstance().updateTrust((int) getQuestItemsCount(player, DARION_BADGE) * 10, true);
							takeItems(player, DARION_BADGE, -1);
							return "32354-10.htm";
						}
						break;
					}
					default: {
						htmltext = "32354-10a.htm";
						break;
					}
				}
				break;
			}
			case "Bottle": {
				if (HellboundEngine.getInstance().getLevel() >= 7) {
					if (getQuestItemsCount(player, STINGER) >= 20) {
						takeItems(player, STINGER, 20);
						giveItems(player, BOTTLE, 1);
						htmltext = "32354-11h.htm";
					} else {
						htmltext = "32354-11i.htm";
					}
				}
				break;
			}
			case "dlf": {
				if (HellboundEngine.getInstance().getLevel() == 7) {
					if (hasQuestItems(player, DIM_LIFE_FORCE)) {
						HellboundEngine.getInstance().updateTrust((int) getQuestItemsCount(player, DIM_LIFE_FORCE) * 20, true);
						takeItems(player, DIM_LIFE_FORCE, -1);
						htmltext = "32354-11a.htm";
					} else {
						htmltext = "32354-11b.htm";
					}
				}
				break;
			}
			case "lf": {
				if (HellboundEngine.getInstance().getLevel() == 7) {
					if (hasQuestItems(player, LIFE_FORCE)) {
						HellboundEngine.getInstance().updateTrust((int) getQuestItemsCount(player, LIFE_FORCE) * 80, true);
						takeItems(player, LIFE_FORCE, -1);
						htmltext = "32354-11c.htm";
					} else {
						htmltext = "32354-11d.htm";
					}
				}
				break;
			}
			case "clf": {
				if (HellboundEngine.getInstance().getLevel() == 7) {
					if (hasQuestItems(player, CONTAINED_LIFE_FORCE)) {
						HellboundEngine.getInstance().updateTrust((int) getQuestItemsCount(player, CONTAINED_LIFE_FORCE) * 200, true);
						takeItems(player, CONTAINED_LIFE_FORCE, -1);
						htmltext = "32354-11e.htm";
					} else {
						htmltext = "32354-11f.htm";
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		switch (HellboundEngine.getInstance().getLevel()) {
			case 1:
				return "32354-01.htm";
			case 2:
			case 3:
				return "32354-01a.htm";
			case 4:
				return "32354-01e.htm";
			case 5:
				return "32354-01d.htm";
			case 6:
				return "32354-01b.htm";
			case 7:
				return "32354-01c.htm";
			default:
				return "32354-01f.htm";
		}
	}
}