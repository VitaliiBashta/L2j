package com.l2jserver.gameserver.model.conditions;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.skills.Skill;

public class ConditionPlayerHp extends Condition {
  private final int hp;

  public ConditionPlayerHp(int hp) {
    this.hp = hp;
  }

  @Override
  public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
    return (effector != null) && (((effector.getCurrentHp() * 100) / effector.getMaxHp()) <= hp);
  }
}
