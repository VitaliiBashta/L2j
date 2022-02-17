package com.l2jserver.gameserver.model.events.impl.character;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;

public class OnCreatureAttack implements IBaseEvent {
  private final L2Character _attacker;
  private final L2Character _target;

  public OnCreatureAttack(L2Character attacker, L2Character target) {
    _attacker = attacker;
    _target = target;
  }

  public final L2Character getAttacker() {
    return _attacker;
  }

  public final L2Character getTarget() {
    return _target;
  }

  @Override
  public EventType getType() {
    return EventType.ON_CREATURE_ATTACK;
  }
}
