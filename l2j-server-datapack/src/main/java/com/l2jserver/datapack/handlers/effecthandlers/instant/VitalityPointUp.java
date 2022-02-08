
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.serverpackets.UserInfo;

/**
 * Vitality Point Up effect implementation.
 * @author Adry_85
 */
public final class VitalityPointUp extends AbstractEffect {
	private final float _value;
	
	public VitalityPointUp(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_value = params.getFloat("value", 0);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if ((info.getEffected() != null) && info.getEffected().isPlayer()) {
			info.getEffected().getActingPlayer().updateVitalityPoints(_value, false, false);
			info.getEffected().getActingPlayer().sendPacket(new UserInfo(info.getEffected().getActingPlayer()));
		}
	}
}
