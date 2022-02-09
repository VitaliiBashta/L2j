package com.l2jserver.gameserver.instancemanager;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Instance;
import com.l2jserver.gameserver.model.instancezone.InstanceWorld;
import com.l2jserver.gameserver.util.IXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InstanceManager implements IXmlReader {

  private static final Logger LOG = LoggerFactory.getLogger(InstanceManager.class);

  private static final Map<Integer, Instance> INSTANCES = new ConcurrentHashMap<>();
  private static final Map<Integer, String> _instanceIdNames = new HashMap<>();
  private static final String ADD_INSTANCE_TIME =
      "INSERT INTO character_instance_time (charId,instanceId,time) values (?,?,?) ON DUPLICATE KEY UPDATE time=?";
  private static final String RESTORE_INSTANCE_TIMES =
      "SELECT instanceId,time FROM character_instance_time WHERE charId=?";
  private static final String DELETE_INSTANCE_TIME =
      "DELETE FROM character_instance_time WHERE charId=? AND instanceId=?";
  private final Map<Integer, InstanceWorld> _instanceWorlds = new ConcurrentHashMap<>();
  private final Map<Integer, Map<Integer, Long>> _playerInstanceTimes = new ConcurrentHashMap<>();
  private int _dynamic = 300000;

  private final ClanHallManager clanHallManager;

  protected InstanceManager(ClanHallManager clanHallManager) {
    this.clanHallManager = clanHallManager;
    // Creates the multiverse.
    INSTANCES.put(-1, new Instance(-1, "multiverse"));
    LOG.info("Multiverse Instance created.");
    // Creates the universe.
    INSTANCES.put(0, new Instance(0, "universe"));
    LOG.info("Universe Instance created.");
    load();
  }

  public static InstanceManager getInstance() {
    return SingletonHolder._instance;
  }

  @Override
  public void load() {
    _instanceIdNames.clear();
    parseDatapackFile("data/instancenames.xml");
    LOG.info("Loaded {} instance names.", _instanceIdNames.size());
  }

  public long getInstanceTime(int playerObjId, int id) {
    if (!_playerInstanceTimes.containsKey(playerObjId)) {
      restoreInstanceTimes(playerObjId);
    }
    if (_playerInstanceTimes.get(playerObjId).containsKey(id)) {
      return _playerInstanceTimes.get(playerObjId).get(id);
    }
    return -1;
  }

  public Map<Integer, Long> getAllInstanceTimes(int playerObjId) {
    if (!_playerInstanceTimes.containsKey(playerObjId)) {
      restoreInstanceTimes(playerObjId);
    }
    return _playerInstanceTimes.get(playerObjId);
  }

  public void setInstanceTime(int playerObjId, int id, long time) {
    if (!_playerInstanceTimes.containsKey(playerObjId)) {
      restoreInstanceTimes(playerObjId);
    }

    try (var con = ConnectionFactory.getInstance().getConnection();
        var ps = con.prepareStatement(ADD_INSTANCE_TIME)) {
      ps.setInt(1, playerObjId);
      ps.setInt(2, id);
      ps.setLong(3, time);
      ps.setLong(4, time);
      ps.execute();
      _playerInstanceTimes.get(playerObjId).put(id, time);
    } catch (Exception ex) {
      LOG.warn("Could not insert character instance time data!", ex);
    }
  }

  public void deleteInstanceTime(int playerObjId, int id) {
    try (var con = ConnectionFactory.getInstance().getConnection();
        var ps = con.prepareStatement(DELETE_INSTANCE_TIME)) {
      ps.setInt(1, playerObjId);
      ps.setInt(2, id);
      ps.execute();
      _playerInstanceTimes.get(playerObjId).remove(id);
    } catch (Exception ex) {
      LOG.warn("Could not delete character instance time data!", ex);
    }
  }

  public void restoreInstanceTimes(int playerObjId) {
    if (_playerInstanceTimes.containsKey(playerObjId)) {
      return; // already restored
    }
    _playerInstanceTimes.put(playerObjId, new ConcurrentHashMap<>());
    try (var con = ConnectionFactory.getInstance().getConnection();
        var ps = con.prepareStatement(RESTORE_INSTANCE_TIMES)) {
      ps.setInt(1, playerObjId);
      try (var rs = ps.executeQuery()) {
        while (rs.next()) {
          int id = rs.getInt("instanceId");
          long time = rs.getLong("time");
          if (time < System.currentTimeMillis()) {
            deleteInstanceTime(playerObjId, id);
          } else {
            _playerInstanceTimes.get(playerObjId).put(id, time);
          }
        }
      }
    } catch (Exception ex) {
      LOG.warn("Could not delete character instance time data!", ex);
    }
  }

  public String getInstanceIdName(int id) {
    if (_instanceIdNames.containsKey(id)) {
      return _instanceIdNames.get(id);
    }
    return ("UnknownInstance");
  }

  @Override
  public void parseDocument(Document doc) {
    for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
      if ("list".equals(n.getNodeName())) {
        NamedNodeMap attrs;
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
          if ("instance".equals(d.getNodeName())) {
            attrs = d.getAttributes();
            _instanceIdNames.put(
                parseInteger(attrs, "id"), attrs.getNamedItem("name").getNodeValue());
          }
        }
      }
    }
  }

  public void addWorld(InstanceWorld world) {
    _instanceWorlds.put(world.getInstanceId(), world);
  }

  public InstanceWorld getWorld(int instanceId) {
    return _instanceWorlds.get(instanceId);
  }

  /**
   * Check if the player have a World Instance where it's allowed to enter.
   *
   * @param player the player to check
   * @return the instance world
   */
  public InstanceWorld getPlayerWorld(L2PcInstance player) {
    for (InstanceWorld temp : _instanceWorlds.values()) {
      if ((temp != null) && (temp.isAllowed(player.getObjectId()))) {
        return temp;
      }
    }
    return null;
  }

  public void destroyInstance(int instanceid) {
    if (instanceid <= 0) {
      return;
    }
    final Instance temp = INSTANCES.get(instanceid);
    if (temp != null) {
      temp.removeNpcs();
      temp.removePlayers();
      temp.removeDoors();
      temp.cancelTimer();
      INSTANCES.remove(instanceid);
      _instanceWorlds.remove(instanceid);
    }
  }

  public Instance getInstance(int instanceid) {
    return INSTANCES.get(instanceid);
  }

  public Map<Integer, Instance> getInstances() {
    return INSTANCES;
  }

  public int getPlayerInstance(int objectId) {
    for (Instance temp : INSTANCES.values()) {
      if (temp == null) {
        continue;
      }
      // check if the player is in any active instance
      if (temp.containsPlayer(objectId)) {
        return temp.getId();
      }
    }
    // 0 is default instance aka the world
    return 0;
  }

  public boolean createInstance(int id) {
    if (getInstance(id) != null) {
      return false;
    }

    final Instance instance = new Instance(id);
    INSTANCES.put(id, instance);
    return true;
  }

  public boolean createInstanceFromTemplate(int id, String template) {
    if (getInstance(id) != null) {
      return false;
    }

    final Instance instance = new Instance(id);
    INSTANCES.put(id, instance);
    instance.loadInstanceTemplate(template);
    return true;
  }

  /** Create a new instance with a dynamic instance id based on a template (or null) */
  public int createDynamicInstance(String template) {
    while (getInstance(_dynamic) != null) {
      _dynamic++;
      if (_dynamic == Integer.MAX_VALUE) {
        LOG.warn("More then {} instances has been created!", Integer.MAX_VALUE - 300000);
        _dynamic = 300000;
      }
    }
    final Instance instance = new Instance(_dynamic);
    INSTANCES.put(_dynamic, instance);
    if (template != null) {
      instance.loadInstanceTemplate(template);
    }
    return _dynamic;
  }

  private static class SingletonHolder {
    protected static final InstanceManager _instance = new InstanceManager(null);
  }
}
