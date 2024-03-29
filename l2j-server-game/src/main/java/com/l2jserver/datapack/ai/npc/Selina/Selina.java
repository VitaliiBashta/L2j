
package com.l2jserver.datapack.ai.npc.Selina;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;

/**
 * Mercenary Medic Selina AI.
 * @author Zoey76
 */
public final class Selina extends AbstractNpcAI {
	// NPC
	private static final int SELINA = 31556;
	// Items
	private static final int GOLDEN_RAM_BADGE_RECRUIT = 7246;
	private static final int GOLDEN_RAM_BADGE_SOLDIER = 7247;
	private static final int GOLDEN_RAM_COIN = 7251;
	// Skills
	private static final Map<String, BuffHolder> BUFFS = new HashMap<>();
	
	static {
		BUFFS.put("4359", new BuffHolder(4359, 2, 2)); // Focus
		BUFFS.put("4360", new BuffHolder(4360, 2, 2)); // Death Whisper
		BUFFS.put("4345", new BuffHolder(4345, 3, 3)); // Might
		BUFFS.put("4355", new BuffHolder(4355, 2, 3)); // Acumen
		BUFFS.put("4352", new BuffHolder(4352, 1, 3)); // Berserker Spirit
		BUFFS.put("4354", new BuffHolder(4354, 2, 3)); // Vampiric Rage
		BUFFS.put("4356", new BuffHolder(4356, 1, 6)); // Empower
		BUFFS.put("4357", new BuffHolder(4357, 2, 6)); // Haste
	}
	
	public Selina() {
		super(Selina.class.getSimpleName(), "ai/npc");
		addStartNpc(SELINA);
		addTalkId(SELINA);
		addFirstTalkId(SELINA);
		addSpellFinishedId(SELINA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final BuffHolder buff = BUFFS.get(event);
		if (buff != null) {
			if ((getQuestItemsCount(player, GOLDEN_RAM_COIN) >= buff.getCost())) {
				castSkill(npc, player, buff);
				return super.onAdvEvent(event, npc, player);
			}
		} else {
			LOG.warn(getClass().getSimpleName() + " AI: player " + player + " sent invalid bypass: " + event);
		}
		return "31556-02.html";
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		final String htmltext;
		if (hasQuestItems(player, GOLDEN_RAM_BADGE_SOLDIER)) {
			htmltext = "31556-08.html";
		} else if (hasQuestItems(player, GOLDEN_RAM_BADGE_RECRUIT)) {
			htmltext = "31556-01.html";
		} else {
			htmltext = "31556-09.html";
		}
		return htmltext;
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, Skill skill) {
		final BuffHolder buff = BUFFS.get(Integer.toString(skill.getId()));
		if (buff != null) {
			takeItems(player, GOLDEN_RAM_COIN, buff.getCost());
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	private static class BuffHolder extends SkillHolder {
		private final int _cost;
		
		public BuffHolder(int skillId, int skillLvl, int cost) {
			super(skillId, skillLvl);
			_cost = cost;
		}
		
		public int getCost() {
			return _cost;
		}
	}
}
