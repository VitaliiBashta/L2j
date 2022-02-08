
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.enums.EffectCalculationType;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.stat.CharStat;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.stats.Stats;
import com.l2jserver.gameserver.model.stats.functions.FuncAdd;
import com.l2jserver.gameserver.model.stats.functions.FuncMul;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Max Mp effect implementation.
 * @author Adry_85
 * @since 2.6.0.0
 */
public final class MaxMp extends AbstractEffect {
	private final double _power;
	private final EffectCalculationType _type;
	private final boolean _heal;
	
	public MaxMp(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_type = params.getEnum("type", EffectCalculationType.class, EffectCalculationType.DIFF);
		switch (_type) {
			case DIFF: {
				_power = params.getDouble("power", 0);
				break;
			}
			default: {
				_power = 1 + (params.getDouble("power", 0) / 100.0);
			}
		}
		_heal = params.getBoolean("heal", false);
		
		if (params.isEmpty()) {
			_log.warning(getClass().getSimpleName() + ": must have parameters.");
		}
	}
	
	@Override
	public void onStart(BuffInfo info) {
		final L2Character effected = info.getEffected();
		final CharStat charStat = effected.getStat();
		final double currentMp = effected.getCurrentMp();
		double amount = _power;
		
		synchronized (charStat) {
			switch (_type) {
				case DIFF: {
					charStat.getActiveChar().addStatFunc(new FuncAdd(Stats.MAX_MP, 1, this, _power, null));
					if (_heal) {
						effected.setCurrentMp((currentMp + _power));
					}
					break;
				}
				case PER: {
					final double maxMp = effected.getMaxMp();
					charStat.getActiveChar().addStatFunc(new FuncMul(Stats.MAX_MP, 1, this, _power, null));
					if (_heal) {
						amount = (_power - 1) * maxMp;
						effected.setCurrentMp(currentMp + amount);
					}
					break;
				}
			}
		}
		if (_heal) {
			effected.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_MP_HAS_BEEN_RESTORED).addInt((int) amount));
		}
	}
	
	@Override
	public void onExit(BuffInfo info) {
		final CharStat charStat = info.getEffected().getStat();
		synchronized (charStat) {
			charStat.getActiveChar().removeStatsOwner(this);
		}
	}
	
}
