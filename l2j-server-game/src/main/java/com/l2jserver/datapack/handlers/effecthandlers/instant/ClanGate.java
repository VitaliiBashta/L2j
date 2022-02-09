
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Clan Gate effect implementation.
 * @author ZaKaX
 */
public final class ClanGate extends AbstractEffect {
	public ClanGate(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffected().isPlayer()) {
			final L2Clan clan = info.getEffected().getActingPlayer().getClan();
			if (clan != null) {
				SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.COURT_MAGICIAN_CREATED_PORTAL);
				clan.broadcastToOtherOnlineMembers(msg, info.getEffected().getActingPlayer());
			}
		}
	}
}
