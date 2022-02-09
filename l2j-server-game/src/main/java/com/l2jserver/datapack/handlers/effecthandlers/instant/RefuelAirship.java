
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2AirShipInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Refuel Airship effect implementation.
 * @author Adry_85
 */
public final class RefuelAirship extends AbstractEffect {
	private final int _value;
	
	public RefuelAirship(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_value = params.getInt("value", 0);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.REFUEL_AIRSHIP;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		final L2AirShipInstance ship = info.getEffector().getActingPlayer().getAirShip();
		ship.setFuel(ship.getFuel() + _value);
		ship.updateAbnormalEffect();
	}
}
