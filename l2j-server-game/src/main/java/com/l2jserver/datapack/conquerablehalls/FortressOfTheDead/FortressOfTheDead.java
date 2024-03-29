package com.l2jserver.datapack.conquerablehalls.FortressOfTheDead;

import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.clanhall.ClanHallSiegeEngine;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Service
public class FortressOfTheDead extends ClanHallSiegeEngine {
  private static final int LIDIA = 35629;
  private static final int ALFRED = 35630;
  private static final int GISELLE = 35631;

  private static Map<Integer, Integer> damageToLidia = new HashMap<>();

  private final GameTimeController gameTimeController;

  public FortressOfTheDead(GameTimeController gameTimeController) {
    super(FortressOfTheDead.class.getSimpleName(), "conquerablehalls", FORTRESS_OF_DEAD);
    this.gameTimeController = gameTimeController;
    addKillId(LIDIA);
    addKillId(ALFRED);
    addKillId(GISELLE);

    addSpawnId(LIDIA);
    addSpawnId(ALFRED);
    addSpawnId(GISELLE);

    addAttackId(LIDIA);
  }

  @Override
  public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
    if (!_hall.isInSiege()) {
      return null;
    }

    synchronized (this) {
      final L2Clan clan = attacker.getClan();

      if ((clan != null) && checkIsAttacker(clan)) {
        final int id = clan.getId();
        if ((id > 0) && damageToLidia.containsKey(id)) {
          int newDamage = damageToLidia.get(id);
          newDamage += damage;
          damageToLidia.put(id, newDamage);
        } else {
          damageToLidia.put(id, damage);
        }
      }
    }
    return null;
  }

  @Override
  public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
    if (!_hall.isInSiege()) {
      return null;
    }

    final int npcId = npc.getId();

    if ((npcId == ALFRED) || (npcId == GISELLE)) {
      broadcastNpcSay(
          npc, Say2.NPC_SHOUT, NpcStringId.AARGH_IF_I_DIE_THEN_THE_MAGIC_FORCE_FIELD_OF_BLOOD_WILL);
    }
    if (npcId == LIDIA) {
      broadcastNpcSay(
          npc,
          Say2.NPC_SHOUT,
          NpcStringId
              .GRARR_FOR_THE_NEXT_2_MINUTES_OR_SO_THE_GAME_ARENA_ARE_WILL_BE_CLEANED_THROW_ANY_ITEMS_YOU_DONT_NEED_TO_THE_FLOOR_NOW);
      _missionAccomplished = true;
      synchronized (this) {
        cancelSiegeTask();
        endSiege();
      }
    }

    return null;
  }

  @Override
  public String onSpawn(L2Npc npc) {
    if (npc.getId() == LIDIA) {
      broadcastNpcSay(
          npc,
          Say2.NPC_SHOUT,
          NpcStringId
              .HMM_THOSE_WHO_ARE_NOT_OF_THE_BLOODLINE_ARE_COMING_THIS_WAY_TO_TAKE_OVER_THE_CASTLE_HUMPH_THE_BITTER_GRUDGES_OF_THE_DEAD_YOU_MUST_NOT_MAKE_LIGHT_OF_THEIR_POWER);
    } else if (npc.getId() == ALFRED) {
      broadcastNpcSay(
          npc,
          Say2.NPC_SHOUT,
          NpcStringId
              .HEH_HEH_I_SEE_THAT_THE_FEAST_HAS_BEGUN_BE_WARY_THE_CURSE_OF_THE_HELLMANN_FAMILY_HAS_POISONED_THIS_LAND);
    } else if (npc.getId() == GISELLE) {
      broadcastNpcSay(
          npc,
          Say2.NPC_SHOUT,
          NpcStringId
              .ARISE_MY_FAITHFUL_SERVANTS_YOU_MY_PEOPLE_WHO_HAVE_INHERITED_THE_BLOOD_IT_IS_THE_CALLING_OF_MY_DAUGHTER_THE_FEAST_OF_BLOOD_WILL_NOW_BEGIN);
    }
    return null;
  }

  @Override
  public void startSiege() {
    // Siege must start at night
    int hoursLeft = (gameTimeController.getGameTime() / 60) % 24;

    if ((hoursLeft < 0) || (hoursLeft > 6)) {
      cancelSiegeTask();
      long scheduleTime = (24 - hoursLeft) * 10 * 60000;
      _siegeTask = ThreadPoolManager.getInstance().scheduleGeneral(new SiegeStarts(), scheduleTime);
    } else {
      super.startSiege();
    }
  }

  @Override
  public L2Clan getWinner() {
    int counter = 0;
    int damagest = 0;
    for (Entry<Integer, Integer> e : damageToLidia.entrySet()) {
      final int damage = e.getValue();
      if (damage > counter) {
        counter = damage;
        damagest = e.getKey();
      }
    }
    return ClanTable.getInstance().getClan(damagest);
  }
}
