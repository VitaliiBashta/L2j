
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.instancemanager.FortManager;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.entity.Fort;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Take Fort Start effect implementation.
 * @author UnAfraid
 */
public final class TakeFortStart extends AbstractEffect {
	public TakeFortStart(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffector().isPlayer()) {
			final L2PcInstance player = info.getEffector().getActingPlayer();
			final Fort fort = FortManager.getInstance().getFort(player);
			final L2Clan clan = player.getClan();
			if ((fort != null) && (clan != null)) {
				fort.getSiege().announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.S1_TRYING_RAISE_FLAG), clan.getName());
			}
		}
	}
}
