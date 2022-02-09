
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.Skill;

/**
 * Focus Max Energy effect implementation.
 * @author Adry_85
 */
public final class FocusMaxEnergy extends AbstractEffect {
	public FocusMaxEnergy(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffected().isPlayer()) {
			final Skill sonicMastery = info.getEffected().getSkills().get(992);
			final Skill focusMastery = info.getEffected().getSkills().get(993);
			int maxCharge = (sonicMastery != null) ? sonicMastery.getLevel() : (focusMastery != null) ? focusMastery.getLevel() : 0;
			if (maxCharge != 0) {
				int count = maxCharge - info.getEffected().getActingPlayer().getCharges();
				info.getEffected().getActingPlayer().increaseCharges(count, maxCharge);
			}
		}
	}
}