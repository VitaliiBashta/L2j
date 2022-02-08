
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.instancemanager.TerritoryWarManager;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2SiegeFlagInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Take Territory Flag effect implementation.
 * @author UnAfraid
 */
public final class TakeTerritoryFlag extends AbstractEffect {
	private static final int FLAG_NPC_ID = 35062;
	
	public TakeTerritoryFlag(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
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
			// Spawn a new flag
			final L2SiegeFlagInstance flag = new L2SiegeFlagInstance(player, NpcData.getInstance().getTemplate(FLAG_NPC_ID), false, false);
			flag.setTitle(player.getClan().getName());
			flag.setCurrentHpMp(flag.getMaxHp(), flag.getMaxMp());
			flag.setHeading(player.getHeading());
			flag.spawnMe(player.getX(), player.getY(), player.getZ() + 50);
			TerritoryWarManager.getInstance().addClanFlag(player.getClan(), flag);
		}
	}
}
