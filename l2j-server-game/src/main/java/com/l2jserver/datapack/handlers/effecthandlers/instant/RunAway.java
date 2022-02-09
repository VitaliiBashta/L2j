
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.ai.CtrlEvent;
import com.l2jserver.gameserver.ai.L2AttackableAI;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Run Away effect implementation.
 * @author Zoey76
 */
public final class RunAway extends AbstractEffect {
	private final int _power;
	private final int _time;
	
	public RunAway(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getInt("power", 0);
		
		_time = params.getInt("time", 0);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (!info.getEffected().isAttackable()) {
			return;
		}
		
		if (Rnd.get(100) > _power) {
			return;
		}
		
		if (info.getEffected().isCastingNow() && info.getEffected().canAbortCast()) {
			info.getEffected().abortCast();
		}
		
		((L2AttackableAI) info.getEffected().getAI()).setFearTime(_time);
		
		info.getEffected().getAI().notifyEvent(CtrlEvent.EVT_AFRAID, info.getEffector(), true);
	}
}
