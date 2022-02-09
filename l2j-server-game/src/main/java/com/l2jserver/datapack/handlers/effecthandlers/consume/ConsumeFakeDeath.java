
package com.l2jserver.datapack.handlers.effecthandlers.consume;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ChangeWaitType;
import com.l2jserver.gameserver.network.serverpackets.Revive;

/**
 * Consume Fake Death effect implementation.
 * @author mkizub
 */
public final class ConsumeFakeDeath extends AbstractEffect {
	private final double _power;
	
	public ConsumeFakeDeath(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getDouble("power", 0);
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.FAKE_DEATH;
	}
	
	@Override
	public boolean onActionTime(BuffInfo info) {
		if (info.getEffected().isDead()) {
			return false;
		}
		
		final L2Character target = info.getEffected();
		final double manaDam = _power * getTicksMultiplier();
		
		if ((manaDam < 0) && ((target.getCurrentMp() + manaDam) <= 0)) {
			target.sendPacket(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
			return false;
		}
		
		target.setCurrentMp(Math.min(target.getCurrentMp() + manaDam, target.getMaxRecoverableMp()));
		return true;
	}
	
	@Override
	public void onExit(BuffInfo info) {
		if (info.getEffected().isPlayer()) {
			info.getEffected().getActingPlayer().setIsFakeDeath(false);
			info.getEffected().getActingPlayer().setRecentFakeDeath(true);
		}
		
		info.getEffected().broadcastPacket(new ChangeWaitType(info.getEffected(), ChangeWaitType.WT_STOP_FAKE_DEATH));
		info.getEffected().broadcastPacket(new Revive(info.getEffected()));
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().startFakeDeath();
	}
}
