package com.l2jserver.datapack.ai.individual;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class DivineBeast extends AbstractNpcAI {
  private static final int DIVINE_BEAST = 14870;
  private static final int TRANSFORMATION_ID = 258;
  private static final int CHECK_TIME = 2 * 1000;

  public DivineBeast() {
    super(DivineBeast.class.getSimpleName(), "ai");
    addSummonSpawnId(DIVINE_BEAST);
  }

  @Override
  public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
    if ((player == null) || !player.hasServitor()) {
      cancelQuestTimer(event, npc, player);
    } else if (player.getTransformationId() != TRANSFORMATION_ID) {
      cancelQuestTimer(event, npc, player);
      player.getSummon().unSummon(player);
    }

    return super.onAdvEvent(event, npc, player);
  }

  @Override
  public void onSummonSpawn(L2Summon summon) {
    startQuestTimer("VALIDATE_TRANSFORMATION", CHECK_TIME, null, summon.getActingPlayer(), true);
  }
}
