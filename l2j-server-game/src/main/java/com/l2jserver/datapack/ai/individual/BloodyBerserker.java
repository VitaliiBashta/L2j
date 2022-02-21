package com.l2jserver.datapack.ai.individual;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class BloodyBerserker extends AbstractNpcAI {
  // NPCs
  private static final int BLOODY_BERSERKER = 22855;

  private static final int[] BLOODY_FAMILY = {
    22854, // Bloody Karik
    22855, // Bloody Berserker
    22856, // Bloody Karinness
  };

  public BloodyBerserker() {
    super(BloodyBerserker.class.getSimpleName(), "ai/individual");
    addKillId(BLOODY_BERSERKER);
    addAttackId(BLOODY_BERSERKER);
    addTeleportId(BLOODY_BERSERKER);
    addMoveFinishedId(BLOODY_BERSERKER);
  }

  @Override
  public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
    switch (npc.getId()) {
      case BLOODY_BERSERKER:
        {
          final double DistSpawn =
              npc.calculateDistance(npc.getSpawn().getLocation(), false, false);
          if (DistSpawn > 3000) {
            npc.disableCoreAI(true);
            npc.teleToLocation(npc.getSpawn().getLocation());
          } else {
            if ((DistSpawn > 500)
                && (getRandom(100) < 1)
                && (npc.isInCombat())
                && (!npc.isCastingNow())) {
              for (int object : BLOODY_FAMILY) {
                for (L2Spawn spawn : SpawnTable.getInstance().getSpawns(object)) {
                  final L2Npc obj = spawn.getLastSpawn();
                  if ((obj != null) && !obj.isDead() && (Math.abs(npc.getZ() - obj.getZ()) < 150)) {
                    if (npc.calculateDistance(obj, false, false)
                        > obj.getTemplate().getClanHelpRange()) {
                      if ((npc.calculateDistance(obj, false, false) < 3000)
                          && GeoData.getInstance().canSeeTarget(npc, obj)) {
                        npc.disableCoreAI(true);
                        ((L2Attackable) npc).setCanReturnToSpawnPoint(false);
                        addMoveToDesire(
                            npc,
                            new Location(
                                obj.getX() + getRandom(-100, 100),
                                obj.getY() + getRandom(-100, 100),
                                obj.getZ() + 20,
                                0),
                            0);
                      }
                    }
                  }
                }
              }
            }
          }
          break;
        }
    }
    return super.onAttack(npc, attacker, damage, isSummon);
  }

  @Override
  public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
    switch (event) {
      case "CORE_AI":
        {
          if (npc != null) {
            ((L2Attackable) npc).clearAggroList();
            npc.disableCoreAI(false);
            startQuestTimer("RETURN_SPAWN", 300000, npc, null);
          }
          break;
        }
      case "RETURN_SPAWN":
        {
          if (npc != null) {
            ((L2Attackable) npc).setCanReturnToSpawnPoint(true);
          }
          break;
        }
    }
    return super.onAdvEvent(event, npc, player);
  }

  @Override
  public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
    switch (npc.getId()) {
      case BLOODY_BERSERKER:
        {
          if (getRandom(100) < 5) {
            final int newZ = npc.getZ() + 20;
            addAttackDesire(
                addSpawn(npc.getId(), npc.getX(), npc.getY(), newZ, npc.getHeading(), false, 0),
                killer);
            addAttackDesire(
                addSpawn(
                    npc.getId(), npc.getX(), npc.getY() - 10, newZ, npc.getHeading(), false, 0),
                killer);
            addAttackDesire(
                addSpawn(
                    npc.getId(), npc.getX(), npc.getY() - 20, newZ, npc.getHeading(), false, 0),
                killer);
            addAttackDesire(
                addSpawn(
                    npc.getId(), npc.getX(), npc.getY() + 10, newZ, npc.getHeading(), false, 0),
                killer);
            addAttackDesire(
                addSpawn(
                    npc.getId(), npc.getX(), npc.getY() + 20, newZ, npc.getHeading(), false, 0),
                killer);
          }
          break;
        }
    }
    return super.onKill(npc, killer, isSummon);
  }

  @Override
  protected void onTeleport(L2Npc npc) {
    startQuestTimer("CORE_AI", 100, npc, null);
  }

  @Override
  public void onMoveFinished(L2Npc npc) {
    startQuestTimer("CORE_AI", 100, npc, null);
  }
}
