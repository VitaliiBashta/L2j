
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.instancemanager.TerritoryWarManager;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2SiegeFlagInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Outpost Destroy effect implementation.
 * @author UnAfraid
 */
public final class OutpostDestroy extends AbstractEffect {
	public OutpostDestroy(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		final L2PcInstance player = info.getEffector().getActingPlayer();
		if (!player.isClanLeader()) {
			return;
		}
		
		if (TerritoryWarManager.getInstance().isTWInProgress()) {
			final L2SiegeFlagInstance flag = TerritoryWarManager.getInstance().getHQForClan(player.getClan());
			if (flag != null) {
				flag.deleteMe();
			}
			TerritoryWarManager.getInstance().setHQForClan(player.getClan(), null);
		}
	}
}
