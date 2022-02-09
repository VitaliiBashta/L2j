
package com.l2jserver.datapack.ai.group_template;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;

/**
 * Frozen Labyrinth AI.
 * @author malyelfik
 */
public final class FrozenLabyrinth extends AbstractNpcAI {
	// Monsters
	private static final int PRONGHORN_SPIRIT = 22087;
	private static final int PRONGHORN = 22088;
	private static final int LOST_BUFFALO = 22093;
	private static final int FROST_BUFFALO = 22094;
	
	public FrozenLabyrinth() {
		super(FrozenLabyrinth.class.getSimpleName(), "ai/group_template");
		addAttackId(PRONGHORN, FROST_BUFFALO);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, Skill skill) {
		if (npc.isScriptValue(0) && (skill != null) && !skill.isMagic()) {
			final int spawnId = (npc.getId() == PRONGHORN) ? PRONGHORN_SPIRIT : LOST_BUFFALO;
			int diff = 0;
			for (int i = 0; i < 6; i++) {
				final int x = diff < 60 ? npc.getX() + diff : npc.getX();
				final int y = diff >= 60 ? npc.getY() + (diff - 40) : npc.getY();
				
				final L2Npc monster = addSpawn(spawnId, x, y, npc.getZ(), npc.getHeading(), false, 0);
				addAttackDesire(monster, attacker);
				diff += 20;
			}
			npc.setScriptValue(1);
			npc.deleteMe();
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
}