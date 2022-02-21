package com.l2jserver.datapack.ai.group_template;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class PavelArchaic extends AbstractNpcAI {
  private static final int SAFETY_DEVICE = 18917; // Pavel Safety Device
  private static final int PINCER_GOLEM = 22801; // Cruel Pincer Golem
  private static final int PINCER_GOLEM2 = 22802; // Cruel Pincer Golem
  private static final int PINCER_GOLEM3 = 22803; // Cruel Pincer Golem
  private static final int JACKHAMMER_GOLEM = 22804; // Horrifying Jackhammer Golem

  public PavelArchaic() {
    super(PavelArchaic.class.getSimpleName(), "ai/group_template");
    addKillId(SAFETY_DEVICE, PINCER_GOLEM, JACKHAMMER_GOLEM);
  }

  @Override
  public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
    if (getRandom(100) < 70) {
      final L2Npc golem1 =
          addSpawn(
              PINCER_GOLEM2,
              npc.getX(),
              npc.getY(),
              npc.getZ() + 10,
              npc.getHeading(),
              false,
              0,
              false);
      addAttackDesire(golem1, killer);

      final L2Npc golem2 =
          addSpawn(
              PINCER_GOLEM3,
              npc.getX(),
              npc.getY(),
              npc.getZ() + 10,
              npc.getHeading(),
              false,
              0,
              false);
      addAttackDesire(golem2, killer);
    }
    return super.onKill(npc, killer, isSummon);
  }
}
