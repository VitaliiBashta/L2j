package com.l2jserver.gameserver.model.conditions;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.skills.Skill;

public class ConditionTargetWeight extends Condition {
  private final int _weight;

  /** Instantiates a new condition player weight. */
  public ConditionTargetWeight(int weight) {
    _weight = weight;
  }

  @Override
  public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
    if ((effected != null) && effected.isPlayer()) {
      final L2PcInstance target = effected.getActingPlayer();
      if (!target.getDietMode() && (target.getMaxLoad() > 0)) {
        int weightproc =
            (((target.getCurrentLoad() - target.getBonusWeightPenalty()) * 100)
                / target.getMaxLoad());
        return (weightproc < _weight);
      }
    }
    return false;
  }
}
