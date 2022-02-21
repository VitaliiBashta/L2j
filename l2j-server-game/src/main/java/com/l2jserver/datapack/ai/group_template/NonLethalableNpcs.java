package com.l2jserver.datapack.ai.group_template;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import org.springframework.stereotype.Service;

@Service
public class NonLethalableNpcs extends AbstractNpcAI {
  private static final int[] NPCS = {
    22854, // Bloody Karik
    22855, // Bloody Berserker
    22856, // Bloody Karinness
    22857, // Knoriks
    35062, // Headquarters
  };

  public NonLethalableNpcs() {
    super(NonLethalableNpcs.class.getSimpleName(), "ai/group_template");
    addSpawnId(NPCS);
  }

  @Override
  public String onSpawn(L2Npc npc) {
    npc.setLethalable(false);
    return super.onSpawn(npc);
  }
}
