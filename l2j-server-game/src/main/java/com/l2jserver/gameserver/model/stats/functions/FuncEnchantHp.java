package com.l2jserver.gameserver.model.stats.functions;

import com.l2jserver.gameserver.data.xml.impl.EnchantItemHPBonusData;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.stats.Stats;

public class FuncEnchantHp extends AbstractFunction {
  private EnchantItemHPBonusData enchantItemHPBonusData;

  public FuncEnchantHp(Stats stat, int order, Object owner, double value, Condition applyCond) {
    super(stat, order, owner, value, applyCond);
  }

  @Override
  public double calc(L2Character effector, L2Character effected, Skill skill, double initVal) {
    if ((getApplyCond() != null) && !getApplyCond().test(effector, effected, skill)) {
      return initVal;
    }

    final L2ItemInstance item = (L2ItemInstance) getFuncOwner();
    if (item.getEnchantLevel() > 0) {
      return initVal + enchantItemHPBonusData.getHPBonus(item);
    }
    return initVal;
  }
}
