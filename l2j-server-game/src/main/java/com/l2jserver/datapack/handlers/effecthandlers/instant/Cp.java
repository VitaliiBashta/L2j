
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.enums.EffectCalculationType;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Cp effect implementation.
 * @author Adry_85
 * @since 2.6.0.0
 */
public final class Cp extends AbstractEffect {
	private final double _amount;
	private final EffectCalculationType _mode;
	
	public Cp(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_amount = params.getDouble("amount", 0);
		_mode = params.getEnum("mode", EffectCalculationType.class, EffectCalculationType.DIFF);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.CP;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		final L2Character target = info.getEffected();
		final L2Character activeChar = info.getEffector();
		if ((target == null) || target.isDead() || !target.isPlayer()) {
			return;
		}
		
		double amount = 0;
		switch (_mode) {
			case DIFF: {
				amount = Math.min(_amount, target.getMaxRecoverableCp() - target.getCurrentCp());
				break;
			}
			case PER: {
				if (_amount < 0) {
					amount = (target.getCurrentCp() * _amount) / 100;
				} else {
					amount = Math.min((target.getMaxCp() * _amount) / 100.0, target.getMaxRecoverableCp() - target.getCurrentCp());
				}
				break;
			}
		}
		
		if (amount != 0) {
			target.setCurrentCp(amount + target.getCurrentCp());
		}
		
		if (amount >= 0) {
			if ((activeChar != null) && (activeChar != target)) {
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_CP_HAS_BEEN_RESTORED_BY_C1);
				sm.addCharName(activeChar);
				sm.addInt((int) amount);
				target.sendPacket(sm);
			} else {
				target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CP_HAS_BEEN_RESTORED).addInt((int) amount));
			}
		}
	}
}
