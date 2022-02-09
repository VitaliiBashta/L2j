package com.l2jserver.datapack.ai.group_template;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;

public final class GiantsCave extends AbstractNpcAI {
  // NPC
  private static final int[] SCOUTS = {
    22668, // Gamlin (Scout)
    22669, // Leogul (Scout)
  };

  public GiantsCave() {
    super(GiantsCave.class.getSimpleName(), "ai/group_template");
    addAttackId(SCOUTS);
    addAggroRangeEnterId(SCOUTS);
  }

  @Override
  public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
    if (event.equals("ATTACK") && (player != null) && (npc != null) && !npc.isDead()) {
      if (npc.getId() == SCOUTS[0]) // Gamlin
      {
        broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId._INTRUDER_DETECTED);
      } else {
        broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId.OH_GIANTS_AN_INTRUDER_HAS_BEEN_DISCOVERED);
      }

      for (L2Character characters : npc.getKnownList().getKnownCharactersInRadius(450)) {
        if ((characters != null) && (characters.isAttackable()) && (getRandomBoolean())) {
          addAttackDesire((L2Npc) characters, player);
        }
      }
    } else if (event.equals("CLEAR") && (npc != null) && !npc.isDead()) {
      npc.setScriptValue(0);
    }
    return super.onAdvEvent(event, npc, player);
  }

  @Override
  public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
    if (npc.isScriptValue(0)) {
      npc.setScriptValue(1);
      startQuestTimer("ATTACK", 6000, npc, attacker);
      startQuestTimer("CLEAR", 120000, npc, null);
    }
    return super.onAttack(npc, attacker, damage, isSummon);
  }

  @Override
  public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon) {
    if (npc.isScriptValue(0)) {
      npc.setScriptValue(1);
      if (getRandomBoolean()) {
        broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.YOU_GUYS_ARE_DETECTED);
      } else {
        broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.WHAT_KIND_OF_CREATURES_ARE_YOU);
      }
      startQuestTimer("ATTACK", 6000, npc, player);
      startQuestTimer("CLEAR", 120000, npc, null);
    }
    return super.onAggroRangeEnter(npc, player, isSummon);
  }
}
