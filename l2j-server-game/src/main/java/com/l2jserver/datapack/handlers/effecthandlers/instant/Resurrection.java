package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.stats.Formulas;
import com.l2jserver.gameserver.taskmanager.DecayTaskManager;

public final class Resurrection extends AbstractEffect {
	private final int _power;
	
	public Resurrection(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getInt("power", 0);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.RESURRECTION;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		L2Character target = info.getEffected();
		L2Character activeChar = info.getEffector();
		
		if (activeChar.isPlayer()) {
			if (target.getActingPlayer() != null) {
				target.getActingPlayer().reviveRequest(activeChar.getActingPlayer(), info.getSkill(), target.isPet(), _power, 0);
			}
		} else {
			DecayTaskManager.getInstance().cancel(target);
			target.doRevive(Formulas.calculateSkillResurrectRestorePercent(_power, activeChar));
		}
	}
}