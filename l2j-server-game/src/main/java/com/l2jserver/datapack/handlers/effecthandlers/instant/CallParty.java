
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Call Party effect implementation.
 * @author Adry_85
 */
public final class CallParty extends AbstractEffect {
	public CallParty(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (!info.getEffector().isInParty()) {
			return;
		}
		
		for (L2PcInstance partyMember : info.getEffector().getParty().getMembers()) {
			if (info.getEffector().getActingPlayer().canSummonTarget(partyMember)) {
				if (info.getEffector() != partyMember) {
					partyMember.teleToLocation(info.getEffector().getLocation(), true);
				}
			}
		}
	}
}
