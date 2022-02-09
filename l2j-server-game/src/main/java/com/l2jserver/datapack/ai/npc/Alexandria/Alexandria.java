
package com.l2jserver.datapack.ai.npc.Alexandria;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.holders.QuestItemHolder;

/**
 * Alexandria (Armor Merchant) AI.
 * @author xban1x
 */
public final class Alexandria extends AbstractNpcAI {
	// NPC
	private static final int ALEXANDRIA = 30098;
	// Items
	private static final ItemHolder[] REQUIRED_ITEMS = new ItemHolder[] {
		new ItemHolder(57, 7500000),
		new ItemHolder(5094, 50),
		new ItemHolder(6471, 25),
		new ItemHolder(9814, 4),
		new ItemHolder(9815, 3),
		new ItemHolder(9816, 5),
		new ItemHolder(9817, 5),
	};
	// Agathions
	private static final QuestItemHolder[] LITTLE_DEVILS = new QuestItemHolder[] {
		new AdditionalQuestItemHolder(10321, 600, 1, 10408),
		new QuestItemHolder(10322, 10),
		new QuestItemHolder(10323, 10),
		new QuestItemHolder(10324, 5),
		new QuestItemHolder(10325, 5),
		new QuestItemHolder(10326, 370),
	};
	private static final QuestItemHolder[] LITTLE_ANGELS = new QuestItemHolder[] {
		new AdditionalQuestItemHolder(10315, 600, 1, 10408),
		new QuestItemHolder(10316, 10),
		new QuestItemHolder(10317, 10),
		new QuestItemHolder(10318, 5),
		new QuestItemHolder(10319, 5),
		new QuestItemHolder(10320, 370),
	};
	private static final Map<String, List<QuestItemHolder>> AGATHIONS = new HashMap<>();
	static {
		AGATHIONS.put("littleAngel", Arrays.asList(LITTLE_ANGELS));
		AGATHIONS.put("littleDevil", Arrays.asList(LITTLE_DEVILS));
	}
	
	public Alexandria() {
		super(Alexandria.class.getSimpleName(), "ai/npc");
		addStartNpc(ALEXANDRIA);
		addTalkId(ALEXANDRIA);
		addFirstTalkId(ALEXANDRIA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		if (event.equals("30098-02.html")) {
			htmltext = event;
		} else if (AGATHIONS.containsKey(event)) {
			final int chance = getRandom(1000);
			int chance2 = 0;
			int chance3 = 0;
			for (QuestItemHolder agathion : AGATHIONS.get(event)) {
				chance3 += agathion.getChance();
				if ((chance2 <= chance) && (chance < chance3)) {
					if (takeAllItems(player, REQUIRED_ITEMS)) {
						giveItems(player, agathion);
						htmltext = "30098-03.html";
						
						if (agathion instanceof AdditionalQuestItemHolder) {
							giveItems(player, ((AdditionalQuestItemHolder) agathion).getAdditionalId(), 1);
							htmltext = "30098-03a.html";
						}
					} else {
						htmltext = "30098-04.html";
					}
					break;
				}
				chance2 += agathion.getChance();
			}
		}
		return htmltext;
	}
	
	public static class AdditionalQuestItemHolder extends QuestItemHolder {
		private final int _additionalId;
		
		public AdditionalQuestItemHolder(int id, int chance, long count, int additionalId) {
			super(id, chance, count);
			_additionalId = additionalId;
		}
		
		public int getAdditionalId() {
			return _additionalId;
		}
	}
}
