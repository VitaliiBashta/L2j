
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Unsummon Agathion effect implementation.
 * @author Zoey76
 */
public final class UnsummonAgathion extends AbstractEffect {
	public UnsummonAgathion(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		final L2PcInstance player = info.getEffector().getActingPlayer();
		if (player != null) {
			player.setAgathionId(0);
			player.broadcastUserInfo();
		}
	}
}
