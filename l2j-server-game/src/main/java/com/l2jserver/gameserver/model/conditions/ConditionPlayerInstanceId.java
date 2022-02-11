package com.l2jserver.gameserver.model.conditions;

import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.instancezone.InstanceWorld;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.skills.Skill;

import java.util.List;

/** Instance Id condition. */
public class ConditionPlayerInstanceId extends Condition {
  private final List<Integer> instanceIds;

  public ConditionPlayerInstanceId(List<Integer> instanceIds) {
    this.instanceIds = instanceIds;
  }

  @Override
  public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
    if (effector.getActingPlayer() == null) {
      return false;
    }

    final int instanceId = effector.getInstanceId();
    if (instanceId <= 0) {
      return false; // player not in instance
    }

    final InstanceWorld world =
        InstanceManager.getInstance().getPlayerWorld(effector.getActingPlayer());
    if ((world == null) || (world.getInstanceId() != instanceId)) {
      return false; // player in the different instance
    }
    return instanceIds.contains(world.getTemplateId());
  }
}
