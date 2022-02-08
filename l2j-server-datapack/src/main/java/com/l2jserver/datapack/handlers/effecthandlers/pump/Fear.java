
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.ai.CtrlEvent;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2DefenderInstance;
import com.l2jserver.gameserver.model.actor.instance.L2FortCommanderInstance;
import com.l2jserver.gameserver.model.actor.instance.L2SiegeFlagInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.EffectFlag;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Fear effect implementation.
 * @author littlecrow
 */
public final class Fear extends AbstractEffect {
	public Fear(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean canStart(BuffInfo info) {
		return info.getEffected().isPlayer() || info.getEffected().isSummon() || (info.getEffected().isAttackable() && //
			!((info.getEffected() instanceof L2DefenderInstance) || (info.getEffected() instanceof L2FortCommanderInstance) || //
				(info.getEffected() instanceof L2SiegeFlagInstance) || (info.getEffected().getTemplate().getRace() == Race.SIEGE_WEAPON)));
	}
	
	@Override
	public int getEffectFlags() {
		return EffectFlag.FEAR.getMask();
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.FEAR;
	}
	
	@Override
	public int getTicks() {
		return 5;
	}
	
	@Override
	public boolean onActionTime(BuffInfo info) {
		info.getEffected().getAI().notifyEvent(CtrlEvent.EVT_AFRAID, info.getEffector(), false);
		return false;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffected().isCastingNow() && info.getEffected().canAbortCast()) {
			info.getEffected().abortCast();
		}
		
		info.getEffected().getAI().notifyEvent(CtrlEvent.EVT_AFRAID, info.getEffector(), true);
	}
}
