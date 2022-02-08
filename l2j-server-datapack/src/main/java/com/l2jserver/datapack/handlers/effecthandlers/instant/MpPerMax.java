
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Mp Per Max effect implementation.
 * @author UnAfraid
 */
public final class MpPerMax extends AbstractEffect {
	private final double _power;
	
	public MpPerMax(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getDouble("power", 0);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.MANAHEAL_PERCENT;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		L2Character target = info.getEffected();
		if ((target == null) || target.isDead() || target.isDoor()) {
			return;
		}
		
		double amount = 0;
		double power = _power;
		boolean full = (power == 100.0);
		
		amount = full ? target.getMaxMp() : (target.getMaxMp() * power) / 100.0;
		// Prevents overheal and negative amount
		amount = Math.max(Math.min(amount, target.getMaxRecoverableMp() - target.getCurrentMp()), 0);
		if (amount != 0) {
			target.setCurrentMp(amount + target.getCurrentMp());
		}
		SystemMessage sm;
		if (info.getEffector().getObjectId() != target.getObjectId()) {
			sm = SystemMessage.getSystemMessage(SystemMessageId.S2_MP_HAS_BEEN_RESTORED_BY_C1);
			sm.addCharName(info.getEffector());
		} else {
			sm = SystemMessage.getSystemMessage(SystemMessageId.S1_MP_HAS_BEEN_RESTORED);
		}
		sm.addInt((int) amount);
		target.sendPacket(sm);
	}
}
