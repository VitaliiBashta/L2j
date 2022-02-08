
package com.l2jserver.datapack.ai.individual;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.util.Util;

/**
 * Muscle Bomber AI.
 * @author Zoey76
 * @since 2.6.0.0
 */
public class MuscleBomber extends AbstractNpcAI {
	// NPC
	private static final int MUSCLE_BOMBER = 25724;
	private static final int DRAKOS_ASSASSIN = 22823;
	// Skills
	private static final SkillHolder ENHANCE_LVL_1 = new SkillHolder(6842, 1);
	private static final SkillHolder ENHANCE_LVL_2 = new SkillHolder(6842, 2);
	// Variables
	private static final String HIGH_HP_FLAG = "HIGH_HP_FLAG";
	private static final String MED_HP_FLAG = "MED_HP_FLAG";
	private static final String LIMIT_FLAG = "LIMIT_FLAG";
	// Timers
	private static final String TIMER_SUMMON = "TIMER_SUMMON";
	private static final String TIMER_LIMIT = "TIMER_LIMIT";
	// Misc
	private static final int MAX_CHASE_DIST = 2500;
	private static final double HIGH_HP_PERCENTAGE = 0.80;
	private static final double MED_HP_PERCENTAGE = 0.50;
	
	public MuscleBomber() {
		super(MuscleBomber.class.getSimpleName(), "ai/individual");
		addAttackId(MUSCLE_BOMBER);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
		if (Util.calculateDistance(npc, npc.getSpawn(), false, false) > MAX_CHASE_DIST) {
			npc.teleToLocation(npc.getSpawn().getX(), npc.getSpawn().getY(), npc.getSpawn().getZ());
		}
		
		if ((npc.getCurrentHp() < (npc.getMaxHp() * HIGH_HP_PERCENTAGE)) && !npc.getVariables().getBoolean(HIGH_HP_FLAG, false)) {
			npc.getVariables().set(HIGH_HP_FLAG, true);
			addSkillCastDesire(npc, npc, ENHANCE_LVL_1, 999999999000000000L);
		}
		
		if ((npc.getCurrentHp() < (npc.getMaxHp() * MED_HP_PERCENTAGE)) && !npc.getVariables().getBoolean(MED_HP_FLAG, false)) {
			npc.getVariables().set(MED_HP_FLAG, true);
			addSkillCastDesire(npc, npc, ENHANCE_LVL_2, 999999999000000000L);
			startQuestTimer(TIMER_SUMMON, 60000, npc, attacker);
			startQuestTimer(TIMER_LIMIT, 300000, npc, attacker);
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		switch (event) {
			case TIMER_LIMIT: {
				npc.getVariables().set(LIMIT_FLAG, true);
				break;
			}
			case TIMER_SUMMON: {
				if (!npc.isDead() && !npc.getVariables().getBoolean(LIMIT_FLAG, false)) {
					if (player != null) {
						addAttackDesire(addSpawn(DRAKOS_ASSASSIN, npc.getX() + getRandom(100), npc.getY() + getRandom(10), npc.getZ(), npc.getHeading(), false, 0), player);
						addAttackDesire(addSpawn(DRAKOS_ASSASSIN, npc.getX() + getRandom(100), npc.getY() + getRandom(10), npc.getZ(), npc.getHeading(), false, 0), player);
					}
					startQuestTimer(TIMER_SUMMON, 60000, npc, player);
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
}
