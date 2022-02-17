package com.l2jserver.gameserver.instancemanager;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.entity.Fort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FortSiegeGuardManager {

  private static final Logger LOG = LogManager.getLogger(FortSiegeGuardManager.class.getName());

  private final String fortName;
  private final int residenceId;
  private final Map<Integer, List<L2Spawn>> siegeGuards = new HashMap<>();

  private final ConnectionFactory connectionFactory;

  public FortSiegeGuardManager(ConnectionFactory connectionFactory, Fort fort) {
    this.connectionFactory = connectionFactory;
    fortName = fort.getName();
    residenceId = fort.getResidenceId();
  }

  public void spawnSiegeGuard() {
    try {
      final List<L2Spawn> monsterList = siegeGuards.get(residenceId);
      if (monsterList != null) {
        for (L2Spawn spawnDat : monsterList) {
          spawnDat.doSpawn();
          if (spawnDat.getRespawnDelay() == 0) {
            spawnDat.stopRespawn();
          } else {
            spawnDat.startRespawn();
          }
        }
      }
    } catch (Exception e) {
      LOG.warn("Error spawning siege guards for fort " + fortName + ":" + e.getMessage(), e);
    }
  }

  /** Unspawn guards. */
  public void unspawnSiegeGuard() {
    try {
      final List<L2Spawn> monsterList = siegeGuards.get(residenceId);
      if (monsterList != null) {
        for (L2Spawn spawnDat : monsterList) {
          spawnDat.stopRespawn();
          if (spawnDat.getLastSpawn() != null) {
            spawnDat.getLastSpawn().doDie(spawnDat.getLastSpawn());
          }
        }
      }
    } catch (Exception e) {
      LOG.warn("Error unspawning siege guards for fort " + fortName + ":" + e.getMessage(), e);
    }
  }

  void loadSiegeGuard() {
    siegeGuards.clear();
    try (var con = connectionFactory.getConnection();
        var ps =
            con.prepareStatement(
                "SELECT npcId, x, y, z, heading, respawnDelay FROM fort_siege_guards WHERE fortId = ?")) {
      ps.setInt(1, residenceId);
      try (var rs = ps.executeQuery()) {
        final List<L2Spawn> siegeGuardSpawns = new ArrayList<>();
        while (rs.next()) {
          L2Spawn spawn = createSpawn(rs);

          siegeGuardSpawns.add(spawn);
        }
        siegeGuards.put(residenceId, siegeGuardSpawns);
      }
    } catch (Exception e) {
      LOG.warn("Error loading siege guard for fort " + fortName + ": " + e.getMessage(), e);
    }
  }

  private L2Spawn createSpawn(ResultSet rs) throws SQLException {
    int npcId = rs.getInt("npcId");
    final L2Spawn spawn = new L2Spawn(npcId);
    spawn.setAmount(1);
    spawn.setX(rs.getInt("x"));
    spawn.setY(rs.getInt("y"));
    spawn.setZ(rs.getInt("z"));
    spawn.setHeading(rs.getInt("heading"));
    spawn.setRespawnDelay(rs.getInt("respawnDelay"));
    spawn.setLocationId(0);
    return spawn;
  }
}
