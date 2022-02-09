
package com.l2jserver.datapack.ai.npc.Rignos;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;

/**
 * Rignos AI.
 * @author St3eT
 */
public class Rignos extends AbstractNpcAI {
	// NPC
	private static final int RIGNOS = 32349; // Rignos
	// Item
	private static final int STAMP = 10013; // Race Stamp
	private static final int KEY = 9694; // Secret Key
	// Skill
	private static final SkillHolder TIMER = new SkillHolder(5239, 5); // Event Timer
	// Misc
	private static final int MIN_LV = 78;
	
	public Rignos() {
		super(Rignos.class.getSimpleName(), "ai/npc");
		addStartNpc(RIGNOS);
		addTalkId(RIGNOS);
		addFirstTalkId(RIGNOS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		switch (event) {
			case "32349-03.html": {
				return event;
			}
			case "startRace": {
				if (npc.isScriptValue(0)) {
					npc.setScriptValue(1);
					startQuestTimer("TIME_OUT", 1800000, npc, null);
					TIMER.getSkill().applyEffects(player, player);
					if (player.hasSummon()) {
						TIMER.getSkill().applyEffects(player.getSummon(), player.getSummon());
					}
					
					if (hasQuestItems(player, STAMP)) {
						takeItems(player, STAMP, -1);
					}
				}
				break;
			}
			case "exchange": {
				if (getQuestItemsCount(player, STAMP) >= 4) {
					giveItems(player, KEY, 3);
					takeItems(player, STAMP, -1);
				}
				break;
			}
			case "TIME_OUT": {
				npc.setScriptValue(0);
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = (npc.isScriptValue(0) && (player.getLevel() >= MIN_LV)) ? "32349.html" : "32349-02.html";
		if (getQuestItemsCount(player, STAMP) >= 4) {
			htmltext = "32349-01.html";
		}
		return htmltext;
	}
}