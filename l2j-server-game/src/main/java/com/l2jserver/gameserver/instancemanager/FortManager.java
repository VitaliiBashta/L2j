package com.l2jserver.gameserver.instancemanager;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.entity.Fort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class FortManager {

  private static final Logger LOG = LoggerFactory.getLogger(FortManager.class);
  private final ConnectionFactory connectionFactory;
  private List<Fort> forts;

  public FortManager(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  public static FortManager getInstance() {
    return SingletonHolder._instance;
  }

  public int findNearestFortIndex(L2Object obj) {
    return findNearestFortIndex(obj, Long.MAX_VALUE);
  }

  public int findNearestFortIndex(L2Object obj, long maxDistance) {
    int index = getFortIndex(obj);
    if (index < 0) {
      double distance;
      Fort fort;
      for (int i = 0; i < forts.size(); i++) {
        fort = forts.get(i);
        if (fort == null) {
          continue;
        }
        distance = fort.getDistance(obj);
        if (maxDistance > distance) {
          maxDistance = (long) distance;
          index = i;
        }
      }
    }
    return index;
  }

  public int getFortIndex(L2Object activeObject) {
    return getFortIndex(activeObject.getX(), activeObject.getY(), activeObject.getZ());
  }

  public int getFortIndex(int x, int y, int z) {
    Fort fort;
    for (int i = 0; i < forts.size(); i++) {
      fort = forts.get(i);
      if ((fort != null) && fort.checkIfInZone(x, y, z)) {
        return i;
      }
    }
    return -1;
  }

  public Fort getFortById(int fortId) {
    return forts.stream().filter(f -> (f.getResidenceId() == fortId)).findFirst().orElse(null);
  }

  public Fort getFortByOwner(L2Clan clan) {
    return forts.stream().filter(f -> f.getOwnerClan() == clan).findFirst().orElse(null);
  }

  public Fort getFort(String name) {
    return forts.stream()
        .filter(f -> f.getName().equalsIgnoreCase(name.trim()))
        .findFirst()
        .orElse(null);
  }

  public Fort getFort(L2Object activeObject) {
    return getFort(activeObject.getX(), activeObject.getY(), activeObject.getZ());
  }

  public Fort getFort(int x, int y, int z) {
    return forts.stream().filter(f -> f.checkIfInZone(x, y, z)).findFirst().orElse(null);
  }

  public int getFortIndex(int fortId) {
    Fort fort;
    for (int i = 0; i < forts.size(); i++) {
      fort = forts.get(i);
      if ((fort != null) && (fort.getResidenceId() == fortId)) {
        return i;
      }
    }
    return -1;
  }

  public List<Fort> getForts() {
    return forts;
  }

  @PostConstruct
  public void loadInstances() {
    forts = loadFortIds().stream().map(Fort::new).toList();

    LOG.info("Loaded {} fortress.", forts.size());

    for (Fort fort : forts) {
      fort.getSiege().getSiegeGuardManager(connectionFactory).loadSiegeGuard();
    }
    forts.forEach(Fort::activateInstance);
  }

  private List<Integer> loadFortIds() {
    List<Integer> fortIds = new ArrayList<>();
    try (var con = connectionFactory.getConnection();
        var s = con.createStatement();
        var rs = s.executeQuery("SELECT id FROM fort ORDER BY id")) {
      while (rs.next()) {
        fortIds.add(rs.getInt("id"));
      }
    } catch (Exception ex) {
      throw new IllegalStateException("There has been an error loading fort instances!", ex);
    }
    return fortIds;
  }

  private static class SingletonHolder {
    protected static final FortManager _instance = new FortManager(null);
  }
}
