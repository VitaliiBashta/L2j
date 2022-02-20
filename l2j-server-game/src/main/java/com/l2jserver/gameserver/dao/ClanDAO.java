package com.l2jserver.gameserver.dao;

import java.util.Map;

public interface ClanDAO {

  /**
   * Gets the clan privileges.
   *
   * @param clanId the clan Id
   * @return the ranks and privileges
   */
  Map<Integer, Integer> getPrivileges(int clanId);

  /**
   * Stores the clan privileges.
   *
   * @param clanId the clan Id
   * @param rank the rank
   * @param privileges the privileges
   */
  void storePrivileges(int clanId, int rank, int privileges);
}
