package com.l2jserver.gameserver.model.conditions;

import com.l2jserver.gameserver.instancemanager.GrandBossManager;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.TvTEvent;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.skills.Skill;

public class ConditionPlayerCanEscape extends Condition {
  private final boolean _val;

  public ConditionPlayerCanEscape(boolean val) {
    _val = val;
  }

  @Override
  public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
    boolean canTeleport = true;
    final L2PcInstance player = effector.getActingPlayer();
    if (player == null) {
      canTeleport = false;
    } else if (!TvTEvent.onEscapeUse(player.getObjectId())) {
      canTeleport = false;
    } else if (player.isInDuel()) {
      canTeleport = false;
    } else if (player.isAfraid()) {
      canTeleport = false;
    } else if (player.isCombatFlagEquipped()) {
      canTeleport = false;
    } else if (player.isFlying() || player.isFlyingMounted()) {
      canTeleport = false;
    } else if (player.isInOlympiadMode()) {
      canTeleport = false;
    } else if ((GrandBossManager.getInstance().getZone(player) != null)
        && !player.canOverrideCond(PcCondOverride.SKILL_CONDITIONS)) {
      canTeleport = false;
    }
    return (_val == canTeleport);
  }
}
