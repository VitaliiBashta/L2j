package com.l2jserver.gameserver.model.conditions;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.skills.Skill;

public class ConditionLogicAnd extends Condition {
  private static final Condition[] EMPTY_CONDITIONS = new Condition[0];
  public Condition[] conditions = EMPTY_CONDITIONS;

  public void add(Condition condition) {
    if (condition == null) {
      return;
    }
    if (listener != null) {
      condition.setListener(this);
    }
    final int len = conditions.length;
    final Condition[] tmp = new Condition[len + 1];
    System.arraycopy(conditions, 0, tmp, 0, len);
    tmp[len] = condition;
    conditions = tmp;
  }

  @Override
  void setListener(ConditionListener listener) {
    if (listener != null) {
      for (Condition c : conditions) {
        c.setListener(this);
      }
    } else {
      for (Condition c : conditions) {
        c.setListener(null);
      }
    }
    super.setListener(listener);
  }

  @Override
  public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
    for (Condition c : conditions) {
      if (!c.test(effector, effected, skill, item)) {
        return false;
      }
    }
    return true;
  }
}
