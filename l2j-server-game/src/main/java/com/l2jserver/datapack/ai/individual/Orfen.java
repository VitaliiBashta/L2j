package com.l2jserver.datapack.ai.individual;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.enums.audio.Music;
import com.l2jserver.gameserver.instancemanager.GrandBossManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.zone.type.L2BossZone;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.l2jserver.gameserver.config.Configuration.grandBoss;

@Service
public class Orfen extends AbstractNpcAI {
  private static final Location[] POS = {
    new Location(43728, 17220, -4342),
    new Location(55024, 17368, -5412),
    new Location(53504, 21248, -5486),
    new Location(53248, 24576, -5262)
  };

  private static final NpcStringId[] TEXT = {
    NpcStringId.S1_STOP_KIDDING_YOURSELF_ABOUT_YOUR_OWN_POWERLESSNESS,
    NpcStringId.S1_ILL_MAKE_YOU_FEEL_WHAT_TRUE_FEAR_IS,
    NpcStringId.YOURE_REALLY_STUPID_TO_HAVE_CHALLENGED_ME_S1_GET_READY,
    NpcStringId.S1_DO_YOU_THINK_THATS_GOING_TO_WORK
  };

  private static final int ORFEN = 29014;
  // private static final int RAIKEL = 29015;
  private static final int RAIKEL_LEOS = 29016;
  // private static final int RIBA = 29017;
  private static final int RIBA_IREN = 29018;
  private static final List<L2Attackable> MINIONS = new CopyOnWriteArrayList<>();
  private static final byte ALIVE = 0;
  private static final byte DEAD = 1;
  // Skills
  private static final SkillHolder PARALYSIS = new SkillHolder(4064);
  private static final SkillHolder NPC_MORTAL_BLOW = new SkillHolder(4067, 4);
  private static final SkillHolder ORFEN_HEAL = new SkillHolder(4516);
  private static boolean _IsTeleported;
  private static L2BossZone ZONE;
  private final GrandBossManager grandBossManager;

  public Orfen(GrandBossManager grandBossManager) {
    super(Orfen.class.getSimpleName(), "ai/individual");
    this.grandBossManager = grandBossManager;
    int[] mobs = {ORFEN, RAIKEL_LEOS, RIBA_IREN};
    registerMobs(mobs);
    _IsTeleported = false;
    ZONE = this.grandBossManager.getZone(POS[0]);
    StatsSet info = grandBossManager.getStatsSet(ORFEN);
    int status = grandBossManager.getBossStatus(ORFEN);
    if (status == DEAD) {
      // load the unlock date and time for Orfen from DB
      long temp = info.getLong("respawn_time") - System.currentTimeMillis();
      // if Orfen is locked until a certain time, mark it so and start the unlock timer
      // the unlock time has not yet expired.
      if (temp > 0) {
        startQuestTimer("orfen_unlock", temp, null, null);
      } else {
        // the time has already expired while the server was offline. Immediately spawn Orfen.
        int i = getRandom(10);
        Location loc;
        if (i < 4) {
          loc = POS[1];
        } else if (i < 7) {
          loc = POS[2];
        } else {
          loc = POS[3];
        }
        L2GrandBossInstance orfen = (L2GrandBossInstance) addSpawn(ORFEN, loc, false, 0);
        grandBossManager.setBossStatus(ORFEN, ALIVE);
        spawnBoss(orfen);
      }
    } else {
      int loc_x = info.getInt("loc_x");
      int loc_y = info.getInt("loc_y");
      int loc_z = info.getInt("loc_z");
      int heading = info.getInt("heading");
      int hp = info.getInt("currentHP");
      int mp = info.getInt("currentMP");
      L2GrandBossInstance orfen =
          (L2GrandBossInstance) addSpawn(ORFEN, loc_x, loc_y, loc_z, heading, false, 0);
      orfen.setCurrentHpMp(hp, mp);
      spawnBoss(orfen);
    }
  }

  public void spawnBoss(L2GrandBossInstance npc) {
    grandBossManager.addBoss(npc);
    npc.broadcastPacket(Music.BS01_A_7000.getPacket());
    startQuestTimer("check_orfen_pos", 10000, npc, null, true);
    // Spawn minions
    int x = npc.getX();
    int y = npc.getY();
    L2Attackable mob;
    mob = (L2Attackable) addSpawn(RAIKEL_LEOS, x + 100, y + 100, npc.getZ(), 0, false, 0);
    mob.setIsRaidMinion(true);
    MINIONS.add(mob);
    mob = (L2Attackable) addSpawn(RAIKEL_LEOS, x + 100, y - 100, npc.getZ(), 0, false, 0);
    mob.setIsRaidMinion(true);
    MINIONS.add(mob);
    mob = (L2Attackable) addSpawn(RAIKEL_LEOS, x - 100, y + 100, npc.getZ(), 0, false, 0);
    mob.setIsRaidMinion(true);
    MINIONS.add(mob);
    mob = (L2Attackable) addSpawn(RAIKEL_LEOS, x - 100, y - 100, npc.getZ(), 0, false, 0);
    mob.setIsRaidMinion(true);
    MINIONS.add(mob);
    startQuestTimer("check_minion_loc", 10000, npc, null, true);
  }

  @Override
  public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
    int npcId = npc.getId();
    if (npcId == ORFEN) {
      if (!_IsTeleported && ((npc.getCurrentHp() - damage) < (npc.getMaxHp() / 2))) {
        _IsTeleported = true;
        setSpawnPoint(npc, 0);
      } else if (npc.isInsideRadius(attacker, 1000, false, false)
          && !npc.isInsideRadius(attacker, 300, false, false)
          && (getRandom(10) == 0)) {
        NpcSay packet = new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npcId, TEXT[getRandom(3)]);
        packet.addStringParameter(attacker.getName().toString());
        npc.broadcastPacket(packet);
        attacker.teleToLocation(npc.getLocation());
        npc.setTarget(attacker);
        npc.doCast(PARALYSIS);
      }
    } else if (npcId == RIBA_IREN) {
      if (!npc.isCastingNow() && ((npc.getCurrentHp() - damage) < (npc.getMaxHp() / 2.0))) {
        npc.setTarget(attacker);
        npc.doCast(ORFEN_HEAL);
      }
    }
    return super.onAttack(npc, attacker, damage, isSummon);
  }

  @Override
  public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
    if (event.equalsIgnoreCase("orfen_unlock")) {
      int i = getRandom(10);
      Location loc;
      if (i < 4) {
        loc = POS[1];
      } else if (i < 7) {
        loc = POS[2];
      } else {
        loc = POS[3];
      }
      L2GrandBossInstance orfen = (L2GrandBossInstance) addSpawn(ORFEN, loc, false, 0);
      grandBossManager.setBossStatus(ORFEN, ALIVE);
      spawnBoss(orfen);
    } else if (event.equalsIgnoreCase("check_orfen_pos")) {
      if ((_IsTeleported && (npc.getCurrentHp() > (npc.getMaxHp() * 0.95)))
          || (!ZONE.isInsideZone(npc) && !_IsTeleported)) {
        setSpawnPoint(npc, getRandom(3) + 1);
        _IsTeleported = false;
      } else if (_IsTeleported && !ZONE.isInsideZone(npc)) {
        setSpawnPoint(npc, 0);
      }
    } else if (event.equalsIgnoreCase("check_minion_loc")) {
      for (L2Attackable mob : MINIONS) {
        if (!npc.isInsideRadius(mob, 3000, false, false)) {
          mob.teleToLocation(npc.getLocation());
          ((L2Attackable) npc).clearAggroList();
          npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
        }
      }
    } else if (event.equalsIgnoreCase("despawn_minions")) {
      for (L2Attackable mob : MINIONS) {
        if (mob != null) {
          mob.decayMe();
        }
      }
      MINIONS.clear();
    } else if (event.equalsIgnoreCase("spawn_minion")) {
      L2Attackable mob =
          (L2Attackable) addSpawn(RAIKEL_LEOS, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0);
      mob.setIsRaidMinion(true);
      MINIONS.add(mob);
    }
    return super.onAdvEvent(event, npc, player);
  }

  @Override
  public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
    if (npc.getId() == ORFEN) {
      npc.broadcastPacket(Music.BS02_D_7000.getPacket());
      grandBossManager.setBossStatus(ORFEN, DEAD);
      // Calculate Min and Max respawn times randomly.
      long respawnTime =
          grandBoss().getIntervalOfOrfenSpawn()
              + getRandom(
                  -grandBoss().getRandomOfOrfenSpawn(), grandBoss().getRandomOfOrfenSpawn());
      respawnTime *= 3600000;
      startQuestTimer("orfen_unlock", respawnTime, null, null);
      // also save the respawn time so that the info is maintained past reboots
      StatsSet info = grandBossManager.getStatsSet(ORFEN);
      info.set("respawn_time", System.currentTimeMillis() + respawnTime);
      grandBossManager.setStatsSet(ORFEN, info);
      cancelQuestTimer("check_minion_loc", npc, null);
      cancelQuestTimer("check_orfen_pos", npc, null);
      startQuestTimer("despawn_minions", 20000, null, null);
      cancelQuestTimers("spawn_minion");
    } else if ((grandBossManager.getBossStatus(ORFEN) == ALIVE) && (npc.getId() == RAIKEL_LEOS)) {
      MINIONS.remove(npc);
      startQuestTimer("spawn_minion", 360000, npc, null);
    }
    return super.onKill(npc, killer, isSummon);
  }

  public void setSpawnPoint(L2Npc npc, int index) {
    ((L2Attackable) npc).clearAggroList();
    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
    L2Spawn spawn = npc.getSpawn();
    spawn.setLocation(POS[index]);
    npc.teleToLocation(POS[index], false);
  }

  @Override
  public String onSkillSee(
      L2Npc npc, L2PcInstance caster, Skill skill, List<L2Object> targets, boolean isSummon) {
    if (npc.getId() == ORFEN) {
      L2Character originalCaster = isSummon ? caster.getSummon() : caster;
      if ((skill.getEffectPoint() > 0)
          && (getRandom(5) == 0)
          && npc.isInsideRadius(originalCaster, 1000, false, false)) {
        NpcSay packet =
            new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getId(), TEXT[getRandom(4)]);
        packet.addStringParameter(caster.getName().toString());
        npc.broadcastPacket(packet);
        originalCaster.teleToLocation(npc.getLocation());
        npc.setTarget(originalCaster);
        npc.doCast(PARALYSIS);
      }
    }
    return super.onSkillSee(npc, caster, skill, targets, isSummon);
  }

  @Override
  public String onFactionCall(L2Npc npc, L2Npc caller, L2PcInstance attacker, boolean isSummon) {
    if ((caller == null) || (npc == null) || npc.isCastingNow()) {
      return super.onFactionCall(npc, caller, attacker, isSummon);
    }
    int npcId = npc.getId();
    int callerId = caller.getId();
    if ((npcId == RAIKEL_LEOS) && (getRandom(20) == 0)) {
      npc.setTarget(attacker);
      npc.doCast(NPC_MORTAL_BLOW);
    } else if (npcId == RIBA_IREN) {
      int chance = 1;
      if (callerId == ORFEN) {
        chance = 9;
      }
      if ((callerId != RIBA_IREN)
          && (caller.getCurrentHp() < (caller.getMaxHp() / 2.0))
          && (getRandom(10) < chance)) {
        npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
        npc.setTarget(caller);
        npc.doCast(ORFEN_HEAL);
      }
    }
    return super.onFactionCall(npc, caller, attacker, isSummon);
  }
}
