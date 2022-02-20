package com.l2jserver.gameserver.model.variables;

import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class NpcVariables extends AbstractVariables {
  @Override
  public int getInt(String key) {
    return super.getInt(key, 0);
  }


  public boolean storeMe() {
    return true;
  }

  /**
   * Gets the stored player.
   * @param name the name of the variable
   * @return the stored player or {@code null}
   */
  public L2PcInstance getPlayer(String name) {
    return getObject(name, L2PcInstance.class);
  }

  /**
   * Gets the stored summon.
   * @param name the name of the variable
   * @return the stored summon or {@code null}
   */
  public L2Summon getSummon(String name) {
    return getObject(name, L2Summon.class);
  }
}
