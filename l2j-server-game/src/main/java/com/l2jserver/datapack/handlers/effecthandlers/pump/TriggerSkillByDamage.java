
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureDamageReceived;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.util.Util;

/**
 * Trigger Skill By Damage effect implementation.
 * @author UnAfraid
 */
public final class TriggerSkillByDamage extends AbstractEffect {
	private final int _minAttackerLevel;
	private final int _maxAttackerLevel;
	private final int _minDamage;
	private final int _chance;
	private final SkillHolder _skill;
	private final TargetType _targetType;
	private final InstanceType _attackerType;
	
	public TriggerSkillByDamage(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_minAttackerLevel = params.getInt("minAttackerLevel", 1);
		_maxAttackerLevel = params.getInt("maxAttackerLevel", 100);
		_minDamage = params.getInt("minDamage", 1);
		_chance = params.getInt("chance", 100);
		_skill = new SkillHolder(params.getInt("skillId"), params.getInt("skillLevel", 1));
		_targetType = params.getEnum("targetType", TargetType.class, TargetType.SELF);
		_attackerType = params.getEnum("attackerType", InstanceType.class, InstanceType.L2Character);
	}
	
	public void onDamageReceivedEvent(OnCreatureDamageReceived event) {
		if (event.isDamageOverTime() || (_chance == 0) || (_skill.getSkillLvl() == 0)) {
			return;
		}
		
		if (((_targetType == TargetType.SELF) && (_skill.getSkill().getCastRange() > 0)) && (Util.calculateDistance(event.getAttacker(), event.getTarget(), true, false) > _skill.getSkill().getCastRange())) {
			return;
		}
		
		if (event.getAttacker() == event.getTarget()) {
			return;
		}
		
		if ((event.getAttacker().getLevel() < _minAttackerLevel) || (event.getAttacker().getLevel() > _maxAttackerLevel)) {
			return;
		}
		
		if ((event.getDamage() < _minDamage) || (Rnd.get(100) > _chance) || !event.getAttacker().getInstanceType().isType(_attackerType)) {
			return;
		}
		
		final var triggerSkill = _skill.getSkill();

	}
	
	@Override
	public void onExit(BuffInfo info) {
		info.getEffected().removeListenerIf(EventType.ON_CREATURE_DAMAGE_RECEIVED, listener -> listener.getOwner() == this);
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().addListener(new ConsumerEventListener(info.getEffected(), EventType.ON_CREATURE_DAMAGE_RECEIVED, (OnCreatureDamageReceived event) -> onDamageReceivedEvent(event), this));
	}
}
