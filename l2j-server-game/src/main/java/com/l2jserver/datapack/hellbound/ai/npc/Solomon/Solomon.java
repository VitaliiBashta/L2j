package com.l2jserver.datapack.hellbound.ai.npc.Solomon;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class Solomon extends AbstractNpcAI {
  // NPCs
  private static final int SOLOMON_ID = 32355;

  public Solomon() {
    super(Solomon.class.getSimpleName(), "hellbound/AI/NPC");
    addFirstTalkId(SOLOMON_ID);
  }

  @Override
  public String onFirstTalk(L2Npc npc, L2PcInstance player) {
    if (HellboundEngine.getInstance().getLevel() == 5) {
      return "32355-01.htm";
    } else if (HellboundEngine.getInstance().getLevel() > 5) {
      return "32355-01a.htm";
    }
    return super.onFirstTalk(npc, player);
  }
}
