
package com.l2jserver.datapack.handlers.effecthandlers.consume;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.EffectFlag;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Chameleon Rest effect implementation.
 */
public final class ConsumeChameleonRest extends AbstractEffect {
	private final double _power;
	
	public ConsumeChameleonRest(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getDouble("power", 0);
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public int getEffectFlags() {
		return (EffectFlag.SILENT_MOVE.getMask() | EffectFlag.RELAXING.getMask());
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.RELAXING;
	}
	
	@Override
	public boolean onActionTime(BuffInfo info) {
		if (info.getEffected().isDead()) {
			return false;
		}
		
		final L2Character target = info.getEffected();
		if (target.isPlayer()) {
			if (!target.getActingPlayer().isSitting()) {
				return false;
			}
		}
		
		final double manaDam = _power * getTicksMultiplier();
		if ((manaDam < 0) && ((target.getCurrentMp() + manaDam) <= 0)) {
			target.sendPacket(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
			return false;
		}
		
		target.setCurrentMp(Math.min(target.getCurrentMp() + manaDam, target.getMaxRecoverableMp()));
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffected().isPlayer()) {
			info.getEffected().getActingPlayer().sitDown(false);
		} else {
			info.getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_REST);
		}
	}
}