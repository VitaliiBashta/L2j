package com.l2jserver.gameserver.instancemanager;

import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.L2WorldRegion;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.zone.AbstractZoneSettings;
import com.l2jserver.gameserver.model.zone.L2ZoneForm;
import com.l2jserver.gameserver.model.zone.L2ZoneRespawn;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.model.zone.form.ZoneCuboid;
import com.l2jserver.gameserver.model.zone.form.ZoneCylinder;
import com.l2jserver.gameserver.model.zone.form.ZoneNPoly;
import com.l2jserver.gameserver.model.zone.type.L2ArenaZone;
import com.l2jserver.gameserver.model.zone.type.L2OlympiadStadiumZone;
import com.l2jserver.gameserver.model.zone.type.L2RespawnZone;
import com.l2jserver.gameserver.model.zone.type.NpcSpawnTerritory;
import com.l2jserver.gameserver.util.IXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class ZoneManager extends IXmlReader {

  private static final Logger LOG = LoggerFactory.getLogger(ZoneManager.class);

  private static final Map<String, AbstractZoneSettings> settings = new HashMap<>();

  private final Map<Class<? extends L2ZoneType>, Map<Integer, ? extends L2ZoneType>> classZones =
      new HashMap<>();

  private final Map<String, NpcSpawnTerritory> _spawnTerritories = new HashMap<>();

  private int _lastDynamicId = 300000;

  private List<L2ItemInstance> _debugItems;

  public static AbstractZoneSettings getSettings(String name) {
    return settings.get(name);
  }

  public static ZoneManager getInstance() {
    return SingletonHolder.INSTANCE;
  }

  @Override
  public void load() {
    classZones.clear();
    _spawnTerritories.clear();
    parseDatapackDirectory("data/zones");
    parseDatapackDirectory("data/zones/npcSpawnTerritories");
    LOG.info("Loaded {} zone classes and {} zones.", classZones.size(), getSize());
    LOG.info("Loaded {} NPC spawn territoriers.", _spawnTerritories.size());
  }

  @Override
  public void parseDocument(Document doc, File f) {
    // Get the world regions
    final L2WorldRegion[][] worldRegions = L2World.getInstance().getWorldRegions();
    NamedNodeMap attrs;
    Node attribute;
    String zoneName;
    int[][] coords;
    int zoneId, minZ, maxZ;
    String zoneType, zoneShape;
    final List<int[]> rs = new ArrayList<>();

    for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
      if ("list".equalsIgnoreCase(n.getNodeName())) {
        attrs = n.getAttributes();
        attribute = attrs.getNamedItem("enabled");
        if ((attribute != null) && !Boolean.parseBoolean(attribute.getNodeValue())) {
          continue;
        }

        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
          if ("zone".equalsIgnoreCase(d.getNodeName())) {
            attrs = d.getAttributes();

            attribute = attrs.getNamedItem("type");
            if (attribute != null) {
              zoneType = attribute.getNodeValue();
            } else {
              LOG.warn("Missing type for zone in file {}!", f);
              continue;
            }

            attribute = attrs.getNamedItem("id");
            if (attribute != null) {
              zoneId = Integer.parseInt(attribute.getNodeValue());
            } else {
              zoneId = zoneType.equalsIgnoreCase("NpcSpawnTerritory") ? 0 : _lastDynamicId++;
            }

            attribute = attrs.getNamedItem("name");
            if (attribute != null) {
              zoneName = attribute.getNodeValue();
            } else {
              zoneName = null;
            }

            // Check zone name for NpcSpawnTerritory. Must exist and to be unique
            if (zoneType.equalsIgnoreCase("NpcSpawnTerritory")) {
              if (zoneName == null) {
                LOG.warn("Missing name for NpcSpawnTerritory in file: {}, skipping zone!", f);
                continue;
              } else if (_spawnTerritories.containsKey(zoneName)) {
                LOG.warn(
                    "Name {} already used for another zone, check file {}, skipping zone!",
                    zoneName,
                    f);
                continue;
              }
            }

            minZ = parseInteger(attrs, "minZ");
            maxZ = parseInteger(attrs, "maxZ");

            zoneType = parseString(attrs, "type");
            zoneShape = parseString(attrs, "shape");

            // Get the zone shape from xml
            L2ZoneForm zoneForm = null;
            try {
              for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                if ("node".equalsIgnoreCase(cd.getNodeName())) {
                  attrs = cd.getAttributes();
                  int[] point = new int[2];
                  point[0] = parseInteger(attrs, "X");
                  point[1] = parseInteger(attrs, "Y");
                  rs.add(point);
                }
              }

              coords = rs.toArray(new int[rs.size()][2]);
              rs.clear();

              if (coords.length == 0) {
                LOG.warn("Missing data for zone {} XML file {}!", zoneId, f);
                continue;
              }

              // Create this zone. Parsing for cuboids is a bit different than for other polygons
              // cuboids need exactly 2 points to be defined.
              // Other polygons need at least 3 (one per vertex)
              if (zoneShape.equalsIgnoreCase("Cuboid")) {
                if (coords.length == 2) {
                  zoneForm =
                      new ZoneCuboid(
                          coords[0][0], coords[1][0], coords[0][1], coords[1][1], minZ, maxZ);
                } else {
                  LOG.warn("Missing cuboid vertex in SQL data for zone {} in file {}!", zoneId, f);
                  continue;
                }
              } else if (zoneShape.equalsIgnoreCase("NPoly")) {
                // nPoly needs to have at least 3 vertices
                if (coords.length > 2) {
                  final int[] aX = new int[coords.length];
                  final int[] aY = new int[coords.length];
                  for (int i = 0; i < coords.length; i++) {
                    aX[i] = coords[i][0];
                    aY[i] = coords[i][1];
                  }
                  zoneForm = new ZoneNPoly(aX, aY, minZ, maxZ);
                } else {
                  LOG.warn("Bad data for zone {} in file {}!", zoneId, f);
                  continue;
                }
              } else if (zoneShape.equalsIgnoreCase("Cylinder")) {
                // A Cylinder zone requires a center point
                // at x,y and a radius
                attrs = d.getAttributes();
                final int zoneRad = Integer.parseInt(attrs.getNamedItem("rad").getNodeValue());
                if ((coords.length == 1) && (zoneRad > 0)) {
                  zoneForm = new ZoneCylinder(coords[0][0], coords[0][1], minZ, maxZ, zoneRad);
                } else {
                  LOG.warn("Bad data for zone {} in file {}!", zoneId, f);
                  continue;
                }
              } else {
                LOG.warn("Unknown shape: {} for zone {} in file {}!", zoneShape, zoneId, f);
                continue;
              }
            } catch (Exception e) {
              LOG.warn("Failed to load zone {} coordinates!", zoneId, e);
            }

            // No further parameters needed, if NpcSpawnTerritory is loading
            if (zoneType.equalsIgnoreCase("NpcSpawnTerritory")) {
              _spawnTerritories.put(zoneName, new NpcSpawnTerritory(zoneName, zoneForm));
              continue;
            }

            // Create the zone
            Class<?> newZone;
            Constructor<?> zoneConstructor;
            L2ZoneType temp;
            try {
              newZone = Class.forName("com.l2jserver.gameserver.model.zone.type.L2" + zoneType);
              zoneConstructor = newZone.getConstructor(int.class);
              temp = (L2ZoneType) zoneConstructor.newInstance(zoneId);
              temp.setZone(zoneForm);
            } catch (Exception e) {
              LOG.warn("No such zone type {} in file {}!", zoneType, f);
              continue;
            }

            // Check for additional parameters
            for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
              if ("stat".equalsIgnoreCase(cd.getNodeName())) {
                attrs = cd.getAttributes();
                String name = attrs.getNamedItem("name").getNodeValue();
                String val = attrs.getNamedItem("val").getNodeValue();

                temp.setParameter(name, val);
              } else if ("spawn".equalsIgnoreCase(cd.getNodeName())
                  && (temp instanceof L2ZoneRespawn)) {
                attrs = cd.getAttributes();
                int spawnX = Integer.parseInt(attrs.getNamedItem("X").getNodeValue());
                int spawnY = Integer.parseInt(attrs.getNamedItem("Y").getNodeValue());
                int spawnZ = Integer.parseInt(attrs.getNamedItem("Z").getNodeValue());
                Node val = attrs.getNamedItem("type");
                ((L2ZoneRespawn) temp)
                    .parseLoc(spawnX, spawnY, spawnZ, val == null ? null : val.getNodeValue());
              } else if ("race".equalsIgnoreCase(cd.getNodeName())
                  && (temp instanceof L2RespawnZone)) {
                attrs = cd.getAttributes();
                String race = attrs.getNamedItem("name").getNodeValue();
                String point = attrs.getNamedItem("point").getNodeValue();

                ((L2RespawnZone) temp).addRaceRespawnPoint(race, point);
              }
            }

            if (checkId(zoneId)) {
              LOG.debug(
                  "Caution: Zone ({}) from file {} overrides previous definition.", zoneId, f);
            }

            if ((zoneName != null) && !zoneName.isEmpty()) {
              temp.setName(zoneName);
            }

            addZone(zoneId, temp);

            // Register the zone into any world region it intersects with...
            // currently 11136 test for each zone :>
            int ax, ay, bx, by;
            for (int x = 0; x < worldRegions.length; x++) {
              for (int y = 0; y < worldRegions[x].length; y++) {
                ax = (x - L2World.OFFSET_X) << L2World.SHIFT_BY;
                bx = ((x + 1) - L2World.OFFSET_X) << L2World.SHIFT_BY;
                ay = (y - L2World.OFFSET_Y) << L2World.SHIFT_BY;
                by = ((y + 1) - L2World.OFFSET_Y) << L2World.SHIFT_BY;

                if (temp.getZone().intersectsRectangle(ax, bx, ay, by)) {
                  worldRegions[x][y].addZone(temp);
                }
              }
            }
          }
        }
      }
    }
  }

  @Override
  public void parseDocument(Document doc) {
    throw new IllegalArgumentException("not implemented");
  }

  /**
   * Gets the size.
   *
   * @return the size
   */
  public int getSize() {
    int i = 0;
    for (Map<Integer, ? extends L2ZoneType> map : classZones.values()) {
      i += map.size();
    }
    return i;
  }

  /** @return true, if successful */
  public boolean checkId(int id) {
    for (Map<Integer, ? extends L2ZoneType> map : classZones.values()) {
      if (map.containsKey(id)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Add new zone.
   *
   * @param <T> the generic type
   * @param id the id
   * @param zone the zone
   */
  @SuppressWarnings("unchecked")
  public <T extends L2ZoneType> void addZone(Integer id, T zone) {
    Map<Integer, T> map = (Map<Integer, T>) classZones.get(zone.getClass());
    if (map == null) {
      map = new HashMap<>();
      map.put(id, zone);
      classZones.put(zone.getClass(), map);
    } else {
      map.put(id, zone);
    }
  }

  /**
   * Return all zones by class type.
   *
   * @param <T> the generic type
   * @param zoneType Zone class
   * @return Collection of zones
   */
  @SuppressWarnings("unchecked")
  public <T extends L2ZoneType> Collection<T> getAllZones(Class<T> zoneType) {
    Map<Integer, ? extends L2ZoneType> zones = classZones.get(zoneType);
    if (zones == null) {
      return List.of();
    }
    Collection<? extends L2ZoneType> zoneTypes = zones.values();
    return (Collection<T>) zoneTypes;
  }

  /**
   * Get zone by ID.
   *
   * @param id the id
   * @return the zone by id
   * @see #getZoneById(int, Class)
   */
  public L2ZoneType getZoneById(int id) {
    for (Map<Integer, ? extends L2ZoneType> map : classZones.values()) {
      if (map.containsKey(id)) {
        return map.get(id);
      }
    }
    return null;
  }

  /**
   * Get zone by ID and zone class.
   *
   * @param <T> the generic type
   * @param id the id
   * @param zoneType the zone type
   * @return zone
   */
  @SuppressWarnings("unchecked")
  public <T extends L2ZoneType> T getZoneById(int id, Class<T> zoneType) {
    Map<Integer, ? extends L2ZoneType> zones = classZones.get(zoneType);
    if (zones == null) {
      return null;
    }
    return (T) zones.get(id);
  }

  /**
   * Returns all zones from where the object is located.
   *
   * @param object the object
   * @return zones
   */
  public List<L2ZoneType> getZones(L2Object object) {
    return getZones(object.getX(), object.getY(), object.getZ());
  }

  /**
   * Returns all zones from given coordinates.
   *
   * @param x the x
   * @param y the y
   * @param z the z
   * @return zones
   */
  public List<L2ZoneType> getZones(int x, int y, int z) {
    final L2WorldRegion region = L2World.getInstance().getRegion(x, y);
    final List<L2ZoneType> temp = new ArrayList<>();
    for (L2ZoneType zone : region.getZones()) {
      if (zone.isInsideZone(x, y, z)) {
        temp.add(zone);
      }
    }
    return temp;
  }

  /**
   * Returns all zones from given coordinates (plane).
   *
   * @param x the x
   * @param y the y
   * @return zones
   */
  public List<L2ZoneType> getZones(int x, int y) {
    final L2WorldRegion region = L2World.getInstance().getRegion(x, y);
    final List<L2ZoneType> temp = new ArrayList<>();
    for (L2ZoneType zone : region.getZones()) {
      if (zone.isInsideZone(x, y)) {
        temp.add(zone);
      }
    }
    return temp;
  }

  /**
   * Get spawm territory by name
   *
   * @param name name of territory to search
   * @return link to zone form
   */
  public NpcSpawnTerritory getSpawnTerritory(String name) {
    return _spawnTerritories.get(name);
  }

  /** Returns all spawm territories from where the object is located */
  public List<NpcSpawnTerritory> getSpawnTerritories(L2Object object) {
    List<NpcSpawnTerritory> temp = new ArrayList<>();
    for (NpcSpawnTerritory territory : _spawnTerritories.values()) {
      if (territory.isInsideZone(object.getX(), object.getY(), object.getZ())) {
        temp.add(territory);
      }
    }

    return temp;
  }

  /**
   * Gets the arena.
   *
   * @param character the character
   * @return the arena
   */
  public L2ArenaZone getArena(L2Character character) {
    if (character == null) {
      return null;
    }

    for (L2ZoneType temp : getZones(character.getX(), character.getY(), character.getZ())) {
      if ((temp instanceof L2ArenaZone) && temp.isCharacterInZone(character)) {
        return ((L2ArenaZone) temp);
      }
    }

    return null;
  }

  /** Gets the olympiad stadium. */
  public L2OlympiadStadiumZone getOlympiadStadium(L2Character character) {
    if (character == null) {
      return null;
    }

    for (L2ZoneType temp : getZones(character.getX(), character.getY(), character.getZ())) {
      if ((temp instanceof L2OlympiadStadiumZone) && temp.isCharacterInZone(character)) {
        return ((L2OlympiadStadiumZone) temp);
      }
    }
    return null;
  }

  public <T extends L2ZoneType> T getClosestZone(L2Object obj, Class<T> type) {
    T zone = getZone(obj, type);
    if (zone == null) {
      double closestdis = Double.MAX_VALUE;
      for (T temp : (Collection<T>) classZones.get(type).values()) {
        double distance = temp.getDistanceToZone(obj);
        if (distance < closestdis) {
          closestdis = distance;
          zone = temp;
        }
      }
    }
    return zone;
  }

  /**
   * Gets the zone.
   *
   * @param <T> the generic type
   * @param object the object
   * @param type the type
   * @return zone from where the object is located by type
   */
  public <T extends L2ZoneType> T getZone(L2Object object, Class<T> type) {
    if (object == null) {
      return null;
    }
    return getZone(object.getX(), object.getY(), object.getZ(), type);
  }

  public <T extends L2ZoneType> T getZone(int x, int y, int z, Class<T> type) {
    final L2WorldRegion region = L2World.getInstance().getRegion(x, y);
    for (L2ZoneType zone : region.getZones()) {
      if (zone.isInsideZone(x, y, z) && type.isInstance(zone)) {
        return (T) zone;
      }
    }
    return null;
  }

  /**
   * General storage for debug items used for visualizing zones.
   *
   * @return list of items
   */
  public List<L2ItemInstance> getDebugItems() {
    if (_debugItems == null) {
      _debugItems = new ArrayList<>();
    }
    return _debugItems;
  }

  /** Remove all debug items from l2world. */
  public void clearDebugItems() {
    if (_debugItems != null) {
      final Iterator<L2ItemInstance> it = _debugItems.iterator();
      while (it.hasNext()) {
        final L2ItemInstance item = it.next();
        if (item != null) {
          item.decayMe();
        }
        it.remove();
      }
    }
  }

  private static class SingletonHolder {
    protected static final ZoneManager INSTANCE = new ZoneManager();
  }
}
