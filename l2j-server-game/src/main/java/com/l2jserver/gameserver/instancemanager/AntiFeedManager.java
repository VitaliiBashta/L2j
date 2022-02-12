/*
 * Copyright © 2004-2021 L2J Server
 *
 * This file is part of L2J Server.
 *
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.instancemanager;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.L2GameClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.l2jserver.gameserver.config.Configuration.customs;

public final class AntiFeedManager {
  public static final int GAME_ID = 0;
  public static final int OLYMPIAD_ID = 1;
  public static final int TVT_ID = 2;
  public static final int L2EVENT_ID = 3;

  private final Map<Integer, Long> _lastDeathTimes = new ConcurrentHashMap<>();
  private final Map<Integer, Map<Integer, AtomicInteger>> _eventIPs = new ConcurrentHashMap<>();

  protected AntiFeedManager() {}

  public static AntiFeedManager getInstance() {
    return SingletonHolder._instance;
  }

  /**
   * Set time of the last player's death to current
   *
   * @param objectId Player's objectId
   */
  public void setLastDeathTime(int objectId) {
    _lastDeathTimes.put(objectId, System.currentTimeMillis());
  }

  /** Clears all timestamps */
  public void clear() {
    _lastDeathTimes.clear();
  }

  /**
   * Register new event for dualbox check. Should be called only once.
   *
   * @param eventId
   */
  public void registerEvent(int eventId) {
    _eventIPs.putIfAbsent(eventId, new ConcurrentHashMap<>());
  }

  /**
   * @param eventId
   * @param player
   * @param max
   * @return If number of all simultaneous connections from player's IP address lower than max then
   *     increment connection count and return true.<br>
   *     False if number of all simultaneous connections from player's IP address higher than max.
   */
  public boolean tryAddPlayer(int eventId, L2PcInstance player, int max) {
    return tryAddClient(eventId, player.getClient(), max);
  }

  /**
   * @param eventId
   * @param client
   * @param max
   * @return If number of all simultaneous connections from player's IP address lower than max then
   *     increment connection count and return true.<br>
   *     False if number of all simultaneous connections from player's IP address higher than max.
   */
  public boolean tryAddClient(int eventId, L2GameClient client, int max) {
    if (client == null) {
      return false; // unable to determine IP address
    }

    final Map<Integer, AtomicInteger> event = _eventIPs.get(eventId);
    if (event == null) {
      return false; // no such event registered
    }

    final Integer addrHash = client.getConnectionAddress().hashCode();
    final AtomicInteger connectionCount = event.computeIfAbsent(addrHash, k -> new AtomicInteger());
    int whiteListCount = customs().getDualboxCheckWhitelist().getOrDefault(addrHash, 0);
    if ((whiteListCount < 0) || ((connectionCount.get() + 1) <= (max + whiteListCount))) {
      connectionCount.incrementAndGet();
      return true;
    }
    return false;
  }

  /**
   * Decreasing number of active connection from player's IP address
   *
   * @param eventId
   * @param player
   * @return true if success and false if any problem detected.
   */
  public boolean removePlayer(int eventId, L2PcInstance player) {
    return removeClient(eventId, player.getClient());
  }

  /**
   * Decreasing number of active connection from player's IP address
   *
   * @param eventId
   * @param client
   * @return true if success and false if any problem detected.
   */
  public boolean removeClient(int eventId, L2GameClient client) {
    if (client == null) {
      return false; // unable to determine IP address
    }

    final Map<Integer, AtomicInteger> event = _eventIPs.get(eventId);
    if (event == null) {
      return false; // no such event registered
    }

    final Integer addrHash = client.getConnectionAddress().hashCode();

    return event.computeIfPresent(
            addrHash,
            (k, v) -> {
              if (v.decrementAndGet() == 0) {
                return null;
              }
              return v;
            })
        != null;
  }

  /** Clear all entries for this eventId. */
  public void clear(int eventId) {
    final Map<Integer, AtomicInteger> event = _eventIPs.get(eventId);
    if (event != null) {
      event.clear();
    }
  }

  /** */
  public int getLimit(L2PcInstance player, int max) {
    return getLimit(player.getClient(), max);
  }

  /** @return maximum number of allowed connections (whitelist + max) */
  public int getLimit(L2GameClient client, int max) {
    if (client == null) {
      return max;
    }

    final Integer addrHash = client.getConnectionAddress().hashCode();
    int limit = max;
    if (customs().getDualboxCheckWhitelist().containsKey(addrHash)) {
      limit += customs().getDualboxCheckWhitelist().get(addrHash);
    }
    return limit;
  }

  private static class SingletonHolder {
    protected static final AntiFeedManager _instance = new AntiFeedManager();
  }
}
