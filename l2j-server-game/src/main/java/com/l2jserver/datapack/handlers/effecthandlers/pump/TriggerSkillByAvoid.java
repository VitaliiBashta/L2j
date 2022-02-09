
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureAttackAvoid;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.util.Util;

/**
 * Trigger Skill By Avoid effect implementation.
 * @author Zealar
 */
public final class TriggerSkillByAvoid extends AbstractEffect {
	private final int _chance;
	private final SkillHolder _skill;
	private final TargetType _targetType;
	
	public TriggerSkillByAvoid(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_chance = params.getInt("chance", 100);
		_skill = new SkillHolder(params.getInt("skillId", 0), params.getInt("skillLevel", 0));
		_targetType = params.getEnum("targetType", TargetType.class, TargetType.ONE);
	}
	
	public void onAvoidEvent(OnCreatureAttackAvoid event) {
		if (event.isDamageOverTime() || (_chance == 0) || ((_skill.getSkillId() == 0) || (_skill.getSkillLvl() == 0))) {
			return;
		}
		
		if (((_targetType == TargetType.SELF) && (_skill.getSkill().getCastRange() > 0)) && (Util.calculateDistance(event.getAttacker(), event.getTarget(), true, false) > _skill.getSkill().getCastRange())) {
			return;
		}
		
		if (Rnd.get(100) > _chance) {
			return;
		}
		
		final var triggerSkill = _skill.getSkill();
	}
	
	@Override
	public void onExit(BuffInfo info) {
		info.getEffected().removeListenerIf(EventType.ON_CREATURE_ATTACK_AVOID, listener -> listener.getOwner() == this);
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().addListener(new ConsumerEventListener(info.getEffected(), EventType.ON_CREATURE_ATTACK_AVOID, (OnCreatureAttackAvoid event) -> onAvoidEvent(event), this));
	}
}
