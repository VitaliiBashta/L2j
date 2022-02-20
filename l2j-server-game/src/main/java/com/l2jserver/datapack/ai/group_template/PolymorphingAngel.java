package com.l2jserver.datapack.ai.group_template;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PolymorphingAngel extends AbstractNpcAI {
  private static final Map<Integer, Integer> ANGELSPAWNS = new HashMap<>();

  static {
    ANGELSPAWNS.put(20830, 20859);
    ANGELSPAWNS.put(21067, 21068);
    ANGELSPAWNS.put(21062, 21063);
    ANGELSPAWNS.put(20831, 20860);
    ANGELSPAWNS.put(21070, 21071);
  }

  public PolymorphingAngel() {
    super(PolymorphingAngel.class.getSimpleName(), "ai/group_template");
    addKillId(ANGELSPAWNS.keySet());
  }

  @Override
  public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
    final L2Attackable newNpc = (L2Attackable) addSpawn(ANGELSPAWNS.get(npc.getId()), npc);
    newNpc.setRunning();
    return super.onKill(npc, killer, isSummon);
  }
}
