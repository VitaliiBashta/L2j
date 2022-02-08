
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
 * Hp By Level effect implementation.
 * @author Zoey76
 */
public final class HpByLevel extends AbstractEffect {
	private final double _power;
	
	public HpByLevel(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getDouble("power", 0);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.BUFF;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffector() == null) {
			return;
		}
		
		final L2Character activeChar = info.getEffector();
		
		// Calculation
		final double abs = _power;
		final double absorb = ((activeChar.getCurrentHp() + abs) > activeChar.getMaxHp() ? activeChar.getMaxHp() : (activeChar.getCurrentHp() + abs));
		final int restored = (int) (absorb - activeChar.getCurrentHp());
		activeChar.setCurrentHp(absorb);
		// System message
		activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HP_HAS_BEEN_RESTORED).addInt(restored));
	}
}
