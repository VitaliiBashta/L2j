
package com.l2jserver.datapack.ai.individual;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.util.Util;
import org.springframework.stereotype.Service;

import static com.l2jserver.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;

@Service
public class BlackdaggerWing extends AbstractNpcAI {
	// NPCs
	private static final int BLACKDAGGER_WING = 25721;
	// Skills
	private static final SkillHolder POWER_STRIKE = new SkillHolder(6833, 1);
	private static final SkillHolder RANGE_MAGIC_ATTACK = new SkillHolder(6834, 1);
	// Variables
	private static final String MID_HP_FLAG = "MID_HP_FLAG";
	private static final String POWER_STRIKE_CAST_COUNT = "POWER_STRIKE_CAST_COUNT";
	// Timers
	private static final String DAMAGE_TIMER = "DAMAGE_TIMER";
	// Misc
	private static final int MAX_CHASE_DIST = 2500;
	private static final double MID_HP_PERCENTAGE = 0.50;
	
	public BlackdaggerWing() {
		super(BlackdaggerWing.class.getSimpleName(), "ai/individual");
		addAttackId(BLACKDAGGER_WING);
		addSeeCreatureId(BLACKDAGGER_WING);
		addSpellFinishedId(BLACKDAGGER_WING);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
		if (Util.calculateDistance(npc, npc.getSpawn(), false, false) > MAX_CHASE_DIST) {
			npc.teleToLocation(npc.getSpawn().getX(), npc.getSpawn().getY(), npc.getSpawn().getZ());
		}
		
		if ((npc.getCurrentHp() < (npc.getMaxHp() * MID_HP_PERCENTAGE)) && !npc.getVariables().getBoolean(MID_HP_FLAG, false)) {
			npc.getVariables().set(MID_HP_FLAG, true);
			startQuestTimer(DAMAGE_TIMER, 10000, npc, attacker);
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onSeeCreature(L2Npc npc, L2Character creature, boolean isSummon) {
		if (npc.getVariables().getBoolean(MID_HP_FLAG, false)) {
			final L2Character mostHated = ((L2Attackable) npc).getMostHated();
			if ((mostHated != null) && mostHated.isPlayer() && (mostHated != creature)) {
				if (getRandom(5) < 1) {
					addSkillCastDesire(npc, creature, RANGE_MAGIC_ATTACK, 9999900000000000L);
				}
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, Skill skill) {
		if (skill.getId() == POWER_STRIKE.getSkillId()) {
			npc.getVariables().set(POWER_STRIKE_CAST_COUNT, npc.getVariables().getInt(POWER_STRIKE_CAST_COUNT) + 1);
			if (npc.getVariables().getInt(POWER_STRIKE_CAST_COUNT) > 3) {
				addSkillCastDesire(npc, player, RANGE_MAGIC_ATTACK, 9999900000000000L);
				npc.getVariables().set(POWER_STRIKE_CAST_COUNT, 0);
			}
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (DAMAGE_TIMER.equals(event)) {
			npc.getAI().setIntention(AI_INTENTION_ATTACK);
			startQuestTimer(DAMAGE_TIMER, 30000, npc, player);
		}
		return super.onAdvEvent(event, npc, player);
	}
}
