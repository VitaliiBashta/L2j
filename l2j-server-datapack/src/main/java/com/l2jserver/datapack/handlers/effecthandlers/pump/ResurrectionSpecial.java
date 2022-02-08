package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PetInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.EffectFlag;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.Skill;

public final class ResurrectionSpecial extends AbstractEffect {
	private final int _resPower;
	private final int _resRecovery;
	
	public ResurrectionSpecial(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_resPower = params.getInt("resPower", 0);
		_resRecovery = params.getInt("resRecovery", 0);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.RESURRECTION_SPECIAL;
	}
	
	@Override
	public int getEffectFlags() {
		return EffectFlag.RESURRECTION_SPECIAL.getMask();
	}
	
	@Override
	public void onExit(BuffInfo info) {
		if (!info.getEffected().isPlayer() && !info.getEffected().isPet()) {
			return;
		}
		L2PcInstance caster = info.getEffector().getActingPlayer();
		
		Skill skill = info.getSkill();
		
		if (info.getEffected().isPlayer()) {
			info.getEffected().getActingPlayer().reviveRequest(caster, skill, false, _resPower, _resRecovery);
			return;
		}
		if (info.getEffected().isPet()) {
			L2PetInstance pet = (L2PetInstance) info.getEffected();
			info.getEffected().getActingPlayer().reviveRequest(pet.getActingPlayer(), skill, true, _resPower, _resRecovery);
		}
	}
}