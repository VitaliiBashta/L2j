package com.l2jserver.datapack.ai.individual;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import org.springframework.stereotype.Service;

@Service
public class CrimsonHatuOtis extends AbstractNpcAI {
  // NPC
  private static final int CRIMSON_HATU_OTIS = 18558;
  // Skills
  private static final SkillHolder BOSS_SPINING_SLASH = new SkillHolder(4737);
  private static final SkillHolder BOSS_HASTE = new SkillHolder(4175);

  public CrimsonHatuOtis() {
    super(CrimsonHatuOtis.class.getSimpleName(), "ai/individual");
    addAttackId(CRIMSON_HATU_OTIS);
    addKillId(CRIMSON_HATU_OTIS);
  }

  @Override
  public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
    if (npc.isScriptValue(0)) {
      npc.setScriptValue(1);
      startQuestTimer("SKILL", 5000, npc, null);
    } else if (npc.isScriptValue(1) && (npc.getCurrentHp() < (npc.getMaxHp() * 0.3))) {
      broadcastNpcSay(
          npc, Say2.NPC_ALL, NpcStringId.IVE_HAD_IT_UP_TO_HERE_WITH_YOU_ILL_TAKE_CARE_OF_YOU);
      npc.setScriptValue(2);
      startQuestTimer("BUFF", 1000, npc, null);
    }
    return super.onAttack(npc, attacker, damage, isSummon);
  }

  @Override
  public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
    switch (event) {
      case "SKILL":
        {
          if (npc.isDead()) {
            cancelQuestTimer("SKILL", npc, null);
            return null;
          }
          npc.setTarget(player);
          npc.doCast(BOSS_SPINING_SLASH);
          startQuestTimer("SKILL", 60000, npc, null);
          break;
        }
      case "BUFF":
        {
          if (npc.isScriptValue(2)) {
            npc.setTarget(npc);
            npc.doCast(BOSS_HASTE);
          }
          break;
        }
    }
    return super.onAdvEvent(event, npc, player);
  }

  @Override
  public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
    cancelQuestTimer("SKILL", npc, null);
    cancelQuestTimer("BUFF", npc, null);
    return super.onKill(npc, player, isSummon);
  }
}
