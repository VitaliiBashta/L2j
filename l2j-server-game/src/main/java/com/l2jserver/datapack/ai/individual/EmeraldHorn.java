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

@Service
public class EmeraldHorn extends AbstractNpcAI {
  private static final int EMERALD_HORN = 25718;
  // Skills
  private static final SkillHolder REFLECT_ATTACK = new SkillHolder(6823);
  private static final SkillHolder PIERCING_STORM = new SkillHolder(6824);
  private static final SkillHolder BLEED_LVL_1 = new SkillHolder(6825);
  private static final SkillHolder BLEED_LVL_2 = new SkillHolder(6825, 2);
  // Variables
  private static final String HIGH_DAMAGE_FLAG = "HIGH_DAMAGE_FLAG";
  private static final String TOTAL_DAMAGE_COUNT = "TOTAL_DAMAGE_COUNT";
  private static final String CAST_FLAG = "CAST_FLAG";
  // Timers
  private static final String DAMAGE_TIMER_15S = "DAMAGE_TIMER_15S";
  // Misc
  private static final int MAX_CHASE_DIST = 2500;

  public EmeraldHorn() {
    super(EmeraldHorn.class.getSimpleName(), "ai/individual");
    addAttackId(EMERALD_HORN);
    addSpellFinishedId(EMERALD_HORN);
  }

  @Override
  public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
    if (Util.calculateDistance(npc, npc.getSpawn(), false, false) > MAX_CHASE_DIST) {
      npc.teleToLocation(npc.getSpawn().getX(), npc.getSpawn().getY(), npc.getSpawn().getZ());
    }

    if (npc.isAffectedBySkill(REFLECT_ATTACK.getSkillId())) {
      if (npc.getVariables().getBoolean(CAST_FLAG, false)) {
        npc.getVariables()
            .set(TOTAL_DAMAGE_COUNT, npc.getVariables().getInt(TOTAL_DAMAGE_COUNT) + damage);
      }
    }

    if (npc.getVariables().getInt(TOTAL_DAMAGE_COUNT) > 5000) {
      addSkillCastDesire(npc, attacker, BLEED_LVL_2, 9999000000000000L);
      npc.getVariables().set(TOTAL_DAMAGE_COUNT, 0);
      npc.getVariables().set(CAST_FLAG, false);
      npc.getVariables().set(HIGH_DAMAGE_FLAG, true);
    }

    if (npc.getVariables().getInt(TOTAL_DAMAGE_COUNT) > 10000) {
      addSkillCastDesire(npc, attacker, BLEED_LVL_1, 9999000000000000L);
      npc.getVariables().set(TOTAL_DAMAGE_COUNT, 0);
      npc.getVariables().set(CAST_FLAG, false);
      npc.getVariables().set(HIGH_DAMAGE_FLAG, true);
    }
    return super.onAttack(npc, attacker, damage, isSummon);
  }

  @Override
  public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
    if (DAMAGE_TIMER_15S.equals(event)) {
      if (!npc.getVariables().getBoolean(HIGH_DAMAGE_FLAG, false)) {
        final L2Character mostHated = ((L2Attackable) npc).getMostHated();
        if (mostHated != null) {
          if (mostHated.isDead()) {
            ((L2Attackable) npc).stopHating(mostHated);
          } else {
            addSkillCastDesire(npc, mostHated, PIERCING_STORM, 9999000000000000L);
          }
        }
      }
      npc.getVariables().set(CAST_FLAG, false);
    }
    return super.onAdvEvent(event, npc, player);
  }

  @Override
  public String onSpellFinished(L2Npc npc, L2PcInstance player, Skill skill) {
    if (getRandom(5) < 1) {
      npc.getVariables().set(TOTAL_DAMAGE_COUNT, 0);
      npc.getVariables().set(CAST_FLAG, true);
      addSkillCastDesire(npc, npc, REFLECT_ATTACK, 99999000000000000L);
      startQuestTimer(DAMAGE_TIMER_15S, 15 * 1000, npc, player);
    }
    return super.onSpellFinished(npc, player, skill);
  }
}
