package com.l2jserver.gameserver.model.conditions;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.skills.Skill;

public class ConditionItemId extends Condition {
  private final int itemId;

  public ConditionItemId(int itemId) {
    this.itemId = itemId;
  }

  @Override
  public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
    return (item != null) && (item.getId() == itemId);
  }
}
