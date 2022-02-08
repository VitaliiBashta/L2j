
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
 * Consume Rest effect implementation.
 */
public final class ConsumeRest extends AbstractEffect {
	private final double _power;
	
	public ConsumeRest(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getDouble("power", 0);
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public int getEffectFlags() {
		return EffectFlag.RELAXING.getMask();
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
		
		if ((target.getCurrentHp() + 1) > target.getMaxRecoverableHp()) {
			target.sendPacket(SystemMessageId.SKILL_DEACTIVATED_HP_FULL);
			return false;
		}
		
		final double consume = _power * getTicksMultiplier();
		double mp = target.getCurrentMp();
		if ((consume < 0) && ((mp + consume) <= 0)) {
			target.sendPacket(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
			return false;
		}
		
		target.setCurrentMp(Math.min(mp + consume, target.getMaxRecoverableMp()));
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
