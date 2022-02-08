
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Transfer Damage effect implementation.
 * @author UnAfraid
 */
public final class TransferDamage extends AbstractEffect {
	public TransferDamage(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public void onExit(BuffInfo info) {
		if (info.getEffected().isPlayable() && info.getEffector().isPlayer()) {
			((L2Playable) info.getEffected()).setTransferDamageTo(null);
		}
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffected().isPlayable() && info.getEffector().isPlayer()) {
			((L2Playable) info.getEffected()).setTransferDamageTo(info.getEffector().getActingPlayer());
		}
	}
}