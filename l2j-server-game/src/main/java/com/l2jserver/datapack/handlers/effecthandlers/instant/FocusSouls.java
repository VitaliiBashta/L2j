
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.stats.Stats;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Focus Souls effect implementation.
 * @author nBd, Adry_85
 */
public final class FocusSouls extends AbstractEffect {
	private final int _charge;
	
	public FocusSouls(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_charge = params.getInt("charge", 0);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (!info.getEffected().isPlayer() || info.getEffected().isAlikeDead()) {
			return;
		}
		
		final L2PcInstance target = info.getEffected().getActingPlayer();
		final int maxSouls = (int) target.calcStat(Stats.MAX_SOULS, 0, null, null);
		if (maxSouls > 0) {
			int amount = _charge;
			if ((target.getChargedSouls() < maxSouls)) {
				int count = ((target.getChargedSouls() + amount) <= maxSouls) ? amount : (maxSouls - target.getChargedSouls());
				target.increaseSouls(count);
			} else {
				target.sendPacket(SystemMessageId.SOUL_CANNOT_BE_INCREASED_ANYMORE);
			}
		}
	}
}