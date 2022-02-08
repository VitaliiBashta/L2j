
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Teleport effect implementation.
 * @author Adry_85
 */
public final class Teleport extends AbstractEffect {
	private final Location _loc;
	
	public Teleport(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_loc = new Location(params.getInt("x", 0), params.getInt("y", 0), params.getInt("z", 0));
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.TELEPORT;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().teleToLocation(_loc, true);
	}
}
