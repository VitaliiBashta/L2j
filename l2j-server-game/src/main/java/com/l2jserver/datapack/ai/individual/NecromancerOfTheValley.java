package com.l2jserver.datapack.ai.individual;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import org.springframework.stereotype.Service;

@Service
public class NecromancerOfTheValley extends AbstractNpcAI {
  // NPCs
  private static final int EXPLODING_ORC_GHOST = 22818;
  private static final int WRATHFUL_ORC_GHOST = 22819;
  private static final int NECROMANCER_OF_THE_VALLEY = 22858;
  // Skill
  private static final SkillHolder SELF_DESTRUCTION = new SkillHolder(6850);
  // Variable
  private static final String MID_HP_FLAG = "MID_HP_FLAG";
  // Misc
  private static final double HP_PERCENTAGE = 0.60;

  public NecromancerOfTheValley() {
    super(NecromancerOfTheValley.class.getSimpleName(), "ai/individual");
    addAttackId(NECROMANCER_OF_THE_VALLEY);
    addSpawnId(EXPLODING_ORC_GHOST);
    addSpellFinishedId(EXPLODING_ORC_GHOST);
  }

  @Override
  public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
    if (npc.getCurrentHp() < (npc.getMaxHp() * HP_PERCENTAGE)) {
      if ((getRandom(100) < 10) && !npc.getVariables().getBoolean(MID_HP_FLAG, false)) {
        npc.getVariables().set(MID_HP_FLAG, true);
        addAttackDesire(
            addSpawn((getRandomBoolean() ? EXPLODING_ORC_GHOST : WRATHFUL_ORC_GHOST), npc, true),
            attacker);
      }
    }
    return super.onAttack(npc, attacker, damage, isSummon);
  }

  @Override
  public String onSpellFinished(L2Npc npc, L2PcInstance player, Skill skill) {
    if ((skill == SELF_DESTRUCTION.getSkill()) && (npc != null) && !npc.isDead()) {
      npc.doDie(player);
    }
    return super.onSpellFinished(npc, player, skill);
  }

  @Override
  public String onSpawn(L2Npc npc) {
    for (L2Character obj : npc.getKnownList().getKnownCharactersInRadius(200)) {
      if (obj.isPlayer() && !obj.isDead()) {
        addSkillCastDesire(npc, obj, SELF_DESTRUCTION, 1000000L);
      }
    }
    return super.onSpawn(npc);
  }
}
