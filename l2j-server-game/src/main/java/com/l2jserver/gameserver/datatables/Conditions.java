package com.l2jserver.gameserver.datatables;

import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.conditions.ConditionLogicAnd;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerRace;

class Conditions {

  static Condition races(Condition cond, String value) {
    final String[] racesVal = value.split(",");
    final Race[] races = new Race[racesVal.length];
    for (int r = 0; r < racesVal.length; r++) {
      if (racesVal[r] != null) {
        races[r] = Race.valueOf(racesVal[r]);
      }
    }
    return joinAnd(cond, new ConditionPlayerRace(races));
  }

  static Condition joinAnd(Condition cond, Condition c) {
    if (cond == null) {
      return c;
    }
    if (cond instanceof ConditionLogicAnd) {
      ((ConditionLogicAnd) cond).add(c);
      return cond;
    }
    ConditionLogicAnd and = new ConditionLogicAnd();
    and.add(cond);
    and.add(c);
    return and;
  }
}
