package com.l2jserver.gameserver.model;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.enums.Race;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.l2jserver.gameserver.config.Configuration.character;

public class L2MapRegion {
  private final String _name;
  private final String _town;
  private final int _locId;
  private final int _castle;
  private final int _bbs;
  private final Map<Race, String> _bannedRace = new HashMap<>();
  private List<int[]> _maps = null;
  private List<Location> _spawnLocs = null;
  private List<Location> _otherSpawnLocs = null;
  private List<Location> _chaoticSpawnLocs = null;
  private List<Location> _banishSpawnLocs = null;

  public L2MapRegion(String name, String town, int locId, int castle, int bbs) {
    _name = name;
    _town = town;
    _locId = locId;
    _castle = castle;
    _bbs = bbs;
  }

  public final String getName() {
    return _name;
  }

  public final String getTown() {
    return _town;
  }

  public final int getLocId() {
    return _locId;
  }

  public final int getCastle() {
    return _castle;
  }

  public final int getBbs() {
    return _bbs;
  }

  public final void addMap(int x, int y) {
    if (_maps == null) {
      _maps = new ArrayList<>();
    }

    _maps.add(new int[] {x, y});
  }

  public final List<int[]> getMaps() {
    return _maps;
  }

  public final boolean isZoneInRegion(int x, int y) {
    if (_maps == null) {
      return false;
    }

    for (int[] map : _maps) {
      if ((map[0] == x) && (map[1] == y)) {
        return true;
      }
    }
    return false;
  }

  // Respawn
  public final void addSpawn(int x, int y, int z) {
    if (_spawnLocs == null) {
      _spawnLocs = new ArrayList<>();
    }

    _spawnLocs.add(new Location(x, y, z));
  }

  public final void addOtherSpawn(int x, int y, int z) {
    if (_otherSpawnLocs == null) {
      _otherSpawnLocs = new ArrayList<>();
    }

    _otherSpawnLocs.add(new Location(x, y, z));
  }

  public final void addChaoticSpawn(int x, int y, int z) {
    if (_chaoticSpawnLocs == null) {
      _chaoticSpawnLocs = new ArrayList<>();
    }

    _chaoticSpawnLocs.add(new Location(x, y, z));
  }

  public final void addBanishSpawn(int x, int y, int z) {
    if (_banishSpawnLocs == null) {
      _banishSpawnLocs = new ArrayList<>();
    }

    _banishSpawnLocs.add(new Location(x, y, z));
  }

  public final List<Location> getSpawns() {
    return _spawnLocs;
  }

  public final Location getSpawnLoc() {
    if (character().randomRespawnInTown()) {
      return _spawnLocs.get(Rnd.get(_spawnLocs.size()));
    }
    return _spawnLocs.get(0);
  }

  public final Location getOtherSpawnLoc() {
    if (_otherSpawnLocs != null) {
      if (character().randomRespawnInTown()) {
        return _otherSpawnLocs.get(Rnd.get(_otherSpawnLocs.size()));
      }
      return _otherSpawnLocs.get(0);
    }
    return getSpawnLoc();
  }

  public final Location getChaoticSpawnLoc() {
    if (_chaoticSpawnLocs != null) {
      if (character().randomRespawnInTown()) {
        return _chaoticSpawnLocs.get(Rnd.get(_chaoticSpawnLocs.size()));
      }
      return _chaoticSpawnLocs.get(0);
    }
    return getSpawnLoc();
  }

  public final Location getBanishSpawnLoc() {
    if (_banishSpawnLocs != null) {
      if (character().randomRespawnInTown()) {
        return _banishSpawnLocs.get(Rnd.get(_banishSpawnLocs.size()));
      }
      return _banishSpawnLocs.get(0);
    }
    return getSpawnLoc();
  }

  public final void addBannedRace(String race, String point) {
    _bannedRace.put(Race.valueOf(race), point);
  }

  public final Map<Race, String> getBannedRace() {
    return _bannedRace;
  }
}
