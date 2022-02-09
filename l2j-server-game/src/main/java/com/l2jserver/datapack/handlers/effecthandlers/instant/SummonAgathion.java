
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Summon Agathion effect implementation.
 * @author Zoey76
 */
public final class SummonAgathion extends AbstractEffect {
	
	private final int npcId;
	
	public SummonAgathion(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		npcId = params.getInt("npcId", 0);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (!info.getEffected().isPlayer()) {
			return;
		}
		
		final L2PcInstance player = info.getEffected().getActingPlayer();
		player.setAgathionId(npcId);
		player.broadcastUserInfo();
	}
}
