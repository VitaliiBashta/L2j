
package com.l2jserver.datapack.ai.individual;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;

/**
 * Drakos Warrior AI.
 * @author Adry_85
 * @since 2.6.0.0
 */
public class DrakosWarrior extends AbstractNpcAI {
	// NPCs
	private static final int DRAKOS_WARRIOR = 22822;
	private static final int DRAKOS_ASSASSIN = 22823;
	// Skill
	private static final SkillHolder SUMMON = new SkillHolder(6858);
	
	public DrakosWarrior() {
		super(DrakosWarrior.class.getSimpleName(), "ai/individual");
		addAttackId(DRAKOS_WARRIOR);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
		if (getRandom(100) < 1) {
			addSkillCastDesire(npc, npc, SUMMON, 99999999900000000L);
			final int count = 2 + getRandom(3);
			for (int i = 0; i < count; i++) {
				addSpawn(DRAKOS_ASSASSIN, npc.getX() + getRandom(200), npc.getY() + getRandom(200), npc.getZ(), 0, false, 0, false);
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
}
