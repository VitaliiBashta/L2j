package com.l2jserver.gameserver.model.entity.clanhall;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.instancemanager.ClanHallSiegeManager;
import com.l2jserver.gameserver.instancemanager.MapRegionManager;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.L2SiegeClan;
import com.l2jserver.gameserver.model.L2SiegeClan.SiegeClanType;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Siegable;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static com.l2jserver.gameserver.config.Configuration.clanhall;

public abstract class ClanHallSiegeEngine extends Quest implements Siegable {

  public static final Logger _log = LoggerFactory.getLogger(ClanHallSiegeEngine.class);
  public static final int FORTRESS_RESSISTANCE = 21;
  public static final int DEVASTATED_CASTLE = 34;
  public static final int BANDIT_STRONGHOLD = 35;
  public static final int RAINBOW_SPRINGS = 62;
  public static final int BEAST_FARM = 63;
  public static final int FORTRESS_OF_DEAD = 64;
  private static final String SQL_LOAD_ATTACKERS =
      "SELECT attacker_id FROM clanhall_siege_attackers WHERE clanhall_id = ?";
  private static final String SQL_SAVE_ATTACKERS =
      "INSERT INTO clanhall_siege_attackers VALUES (?,?)";
  private static final String SQL_LOAD_GUARDS =
      "SELECT * FROM clanhall_siege_guards WHERE clanHallId = ?";
  private final Map<Integer, L2SiegeClan> _attackers = new ConcurrentHashMap<>();
  public SiegableHall _hall;
  public ScheduledFuture<?> _siegeTask;
  public boolean _missionAccomplished = false;
  private List<L2Spawn> _guards;

  public ClanHallSiegeEngine(String name, String descr, final int hallId) {
    super(-1, name, descr);

    _hall = ClanHallSiegeManager.getInstance().getSiegableHall(hallId);
    _hall.setSiege(this);

    _siegeTask =
        ThreadPoolManager.getInstance()
            .scheduleGeneral(
                new PrepareOwner(),
                _hall.getNextSiegeTime() - System.currentTimeMillis() - 3600000);
    _log.info("{} siege scheduled for {}.", _hall.getName(), getSiegeDate().getTime());
    loadAttackers();
  }

  public void loadAttackers() {
    try (var con = ConnectionFactory.getInstance().getConnection();
        var ps = con.prepareStatement(SQL_LOAD_ATTACKERS)) {
      ps.setInt(1, _hall.getId());
      try (var rset = ps.executeQuery()) {
        while (rset.next()) {
          final int id = rset.getInt("attacker_id");
          L2SiegeClan clan = new L2SiegeClan(id, SiegeClanType.ATTACKER);
          _attackers.put(id, clan);
        }
      }
    } catch (Exception e) {
      _log.warn("Could not load siege attackers!", e);
    }
  }

  public final void saveAttackers() {
    try (var con = ConnectionFactory.getInstance().getConnection();
        var ps =
            con.prepareStatement("DELETE FROM clanhall_siege_attackers WHERE clanhall_id = ?")) {
      ps.setInt(1, _hall.getId());
      ps.execute();

      if (_attackers.size() > 0) {
        try (var insert = con.prepareStatement(SQL_SAVE_ATTACKERS)) {
          for (L2SiegeClan clan : _attackers.values()) {
            insert.setInt(1, _hall.getId());
            insert.setInt(2, clan.getClanId());
            insert.execute();
            insert.clearParameters();
          }
        }
      }
      _log.info("Successfully saved attackers to database.");
    } catch (Exception e) {
      _log.warn("Couldn't save attacker list!", e);
    }
  }

  public final Map<Integer, L2SiegeClan> getAttackers() {
    return _attackers;
  }

  public void prepareOwner() {
    if (_hall.getOwnerId() > 0) {
      final L2SiegeClan clan = new L2SiegeClan(_hall.getOwnerId(), SiegeClanType.ATTACKER);
      _attackers.put(clan.getClanId(), new L2SiegeClan(clan.getClanId(), SiegeClanType.ATTACKER));
    }

    _hall.free();
    _hall.banishForeigners();
    SystemMessage msg =
        SystemMessage.getSystemMessage(SystemMessageId.REGISTRATION_TERM_FOR_S1_ENDED);
    msg.addString(getName());
    Broadcast.toAllOnlinePlayers(msg);
    _hall.updateSiegeStatus(SiegeStatus.WAITING_BATTLE);

    _siegeTask = ThreadPoolManager.getInstance().scheduleGeneral(new SiegeStarts(), 3600000);
  }

  @Override
  public void startSiege() {
    if ((_attackers.size() < 1)
        && (_hall.getId() != 21)) // Fortress of resistance don't have attacker list
    {
      onSiegeEnds();
      _attackers.clear();
      _hall.updateNextSiege();
      _siegeTask =
          ThreadPoolManager.getInstance()
              .scheduleGeneral(new PrepareOwner(), _hall.getSiegeDate().getTimeInMillis());
      _hall.updateSiegeStatus(SiegeStatus.WAITING_BATTLE);
      SystemMessage sm =
          SystemMessage.getSystemMessage(
              SystemMessageId.SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST);
      sm.addString(_hall.getName());
      Broadcast.toAllOnlinePlayers(sm);
      return;
    }

    _hall.spawnDoor();
    loadGuards();
    spawnSiegeGuards();
    _hall.updateSiegeZone(true);

    final byte state = 1;
    for (L2SiegeClan sClan : _attackers.values()) {
      final L2Clan clan = ClanTable.getInstance().getClan(sClan.getClanId());
      if (clan == null) {
        continue;
      }

      for (L2PcInstance pc : clan.getOnlineMembers(0)) {
        pc.setSiegeState(state);
        pc.broadcastUserInfo();
        pc.setIsInHideoutSiege(true);
      }
    }

    _hall.updateSiegeStatus(SiegeStatus.RUNNING);
    onSiegeStarts();
    _siegeTask =
        ThreadPoolManager.getInstance().scheduleGeneral(new SiegeEnds(), _hall.getSiegeLength());
  }

  public final void loadGuards() {
    if (_guards == null) {
      _guards = new ArrayList<>();
      try (var con = ConnectionFactory.getInstance().getConnection();
          var ps = con.prepareStatement(SQL_LOAD_GUARDS)) {
        ps.setInt(1, _hall.getId());
        try (var rset = ps.executeQuery()) {
          while (rset.next()) {
            final L2Spawn spawn = new L2Spawn(rset.getInt("npcId"));
            spawn.setX(rset.getInt("x"));
            spawn.setY(rset.getInt("y"));
            spawn.setZ(rset.getInt("z"));
            spawn.setHeading(rset.getInt("heading"));
            spawn.setRespawnDelay(rset.getInt("respawnDelay"));
            spawn.setAmount(1);
            _guards.add(spawn);
          }
        }
      } catch (Exception e) {
        _log.warn("Couldn't load siege guards!", e);
      }
    }
  }

  private void spawnSiegeGuards() {
    for (L2Spawn guard : _guards) {
      guard.init();
    }
  }

  @Override
  public void endSiege() {
    SystemMessage end = SystemMessage.getSystemMessage(SystemMessageId.SIEGE_OF_S1_HAS_ENDED);
    end.addString(_hall.getName());
    Broadcast.toAllOnlinePlayers(end);

    L2Clan winner = getWinner();
    if (_missionAccomplished && (winner != null)) {
      _hall.setOwner(winner);
      winner.setHideoutId(_hall.getId());
      var finalMsg =
          SystemMessage.getSystemMessage(SystemMessageId.CLAN_S1_VICTORIOUS_OVER_S2_S_SIEGE);
      finalMsg.addString(winner.getName());
      finalMsg.addString(_hall.getName());
      Broadcast.toAllOnlinePlayers(finalMsg);
    } else {
      var finalMsg = SystemMessage.getSystemMessage(SystemMessageId.SIEGE_S1_DRAW);
      finalMsg.addString(_hall.getName());
      Broadcast.toAllOnlinePlayers(finalMsg);
    }
    _missionAccomplished = false;

    _hall.updateSiegeZone(false);
    _hall.updateNextSiege();
    _hall.spawnDoor(false);
    _hall.banishForeigners();

    final byte state = 0;
    for (L2SiegeClan sClan : _attackers.values()) {
      final L2Clan clan = ClanTable.getInstance().getClan(sClan.getClanId());
      if (clan == null) {
        continue;
      }

      for (L2PcInstance player : clan.getOnlineMembers(0)) {
        player.setSiegeState(state);
        player.broadcastUserInfo();
        player.setIsInHideoutSiege(false);
      }
    }

    // Update pvp flag for winners when siege zone becomes inactive
    for (L2Character chr : _hall.getSiegeZone().getCharactersInside()) {
      if ((chr != null) && chr.isPlayer()) {
        chr.getActingPlayer().startPvPFlag();
      }
    }

    _attackers.clear();

    onSiegeEnds();

    _siegeTask =
        ThreadPoolManager.getInstance()
            .scheduleGeneral(
                new PrepareOwner(),
                _hall.getNextSiegeTime() - System.currentTimeMillis() - 3600000);
    _log.info("Siege of {} scheduled for {}.", _hall.getName(), _hall.getSiegeDate().getTime());

    _hall.updateSiegeStatus(SiegeStatus.REGISTERING);
    unSpawnSiegeGuards();
  }

  private void unSpawnSiegeGuards() {
    if (_guards != null) {
      for (L2Spawn guard : _guards) {
        guard.stopRespawn();
        if (guard.getLastSpawn() != null) {
          guard.getLastSpawn().deleteMe();
        }
      }
    }
  }

  @Override
  public L2SiegeClan getAttackerClan(int clanId) {
    return _attackers.get(clanId);
  }

  @Override
  public L2SiegeClan getAttackerClan(L2Clan clan) {
    return getAttackerClan(clan.getId());
  }

  @Override
  public List<L2SiegeClan> getAttackerClans() {
    return new ArrayList<>(_attackers.values());
  }

  @Override
  public List<L2PcInstance> getAttackersInZone() {
    final List<L2PcInstance> attackers = new ArrayList<>();
    for (L2PcInstance pc : _hall.getSiegeZone().getPlayersInside()) {
      final L2Clan clan = pc.getClan();
      if ((clan != null) && _attackers.containsKey(clan.getId())) {
        attackers.add(pc);
      }
    }
    return attackers;
  }

  @Override
  public boolean checkIsAttacker(L2Clan clan) {
    if (clan == null) {
      return false;
    }

    return _attackers.containsKey(clan.getId());
  }

  @Override
  public L2SiegeClan getDefenderClan(int clanId) {
    return null;
  }

  @Override
  public L2SiegeClan getDefenderClan(L2Clan clan) {
    return null;
  }

  @Override
  public List<L2SiegeClan> getDefenderClans() {
    return null;
  }

  @Override
  public boolean checkIsDefender(L2Clan clan) {
    return false;
  }

  @Override
  public List<L2Npc> getFlag(L2Clan clan) {
    List<L2Npc> result = null;
    L2SiegeClan sClan = getAttackerClan(clan);
    if (sClan != null) {
      result = sClan.getFlag();
    }
    return result;
  }

  @Override
  public Calendar getSiegeDate() {
    return _hall.getSiegeDate();
  }

  @Override
  public boolean giveFame() {
    return clanhall().enableFame();
  }

  @Override
  public int getFameFrequency() {
    return clanhall().getFameFrequency();
  }

  @Override
  public int getFameAmount() {
    return clanhall().getFameAmount();
  }

  @Override
  public void updateSiege() {
    cancelSiegeTask();
    _siegeTask =
        ThreadPoolManager.getInstance()
            .scheduleGeneral(new PrepareOwner(), _hall.getNextSiegeTime() - 3600000);
    _log.info("{} siege scheduled for {}.", _hall.getName(), _hall.getSiegeDate().getTime());
  }

  public void cancelSiegeTask() {
    if (_siegeTask != null) {
      _siegeTask.cancel(false);
    }
  }

  public abstract L2Clan getWinner();

  public void onSiegeStarts() {}

  public void onSiegeEnds() {}

  public final void broadcastNpcSay(final L2Npc npc, final int type, final NpcStringId messageId) {
    final NpcSay npcSay = new NpcSay(npc.getObjectId(), type, npc.getId(), messageId);
    final int sourceRegion = MapRegionManager.getInstance().getMapRegionLocId(npc);
    for (L2PcInstance pc : L2World.getInstance().getPlayers()) {
      if ((pc != null) && (MapRegionManager.getInstance().getMapRegionLocId(pc) == sourceRegion)) {
        pc.sendPacket(npcSay);
      }
    }
  }

  public Location getInnerSpawnLoc(L2PcInstance player) {
    return null;
  }

  public boolean canPlantFlag() {
    return true;
  }

  public boolean doorIsAutoAttackable() {
    return true;
  }

  public class PrepareOwner implements Runnable {
    @Override
    public void run() {
      prepareOwner();
    }
  }

  public class SiegeStarts implements Runnable {
    @Override
    public void run() {
      startSiege();
    }
  }

  public class SiegeEnds implements Runnable {
    @Override
    public void run() {
      endSiege();
    }
  }
}
