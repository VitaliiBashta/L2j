package com.l2jserver.datapack.ai.group_template;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import org.springframework.stereotype.Service;

@Service
public class FairyTrees extends AbstractNpcAI {
  // NPC
  private static final int SOUL_GUARDIAN = 27189; // Soul of Tree Guardian

  private static final int[] MOBS = {
    27185, // Fairy Tree of Wind
    27186, // Fairy Tree of Star
    27187, // Fairy Tree of Twilight
    27188, // Fairy Tree of Abyss
  };

  // Skill
  private static final SkillHolder VENOMOUS_POISON = new SkillHolder(4243);

  // Misc
  private static final int MIN_DISTANCE = 1500;

  public FairyTrees() {
    super(FairyTrees.class.getSimpleName(), "ai/group_template");
    addKillId(MOBS);
    addSpawnId(MOBS);
  }

  @Override
  public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
    if (npc.calculateDistance(killer, true, false) <= MIN_DISTANCE) {
      for (int i = 0; i < 20; i++) {
        final L2Npc guardian = addSpawn(SOUL_GUARDIAN, npc, false, 30000);
        final L2Playable attacker = isSummon ? killer.getSummon() : killer;
        addAttackDesire(guardian, attacker);
        if (getRandomBoolean()) {
          guardian.setTarget(attacker);
          guardian.doCast(VENOMOUS_POISON);
        }
      }
    }
    return super.onKill(npc, killer, isSummon);
  }

  @Override
  public String onSpawn(L2Npc npc) {
    npc.setIsNoRndWalk(true);
    npc.setIsImmobilized(true);
    return super.onSpawn(npc);
  }
}
