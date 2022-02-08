
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.stats.Formulas;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Physical Attack effect implementation.
 * @author Adry_85
 */
public final class PhysicalAttack extends AbstractEffect {
	private final double _power;
	private final int _criticalChance;
	private final boolean _ignoreShieldDefence;
	
	public PhysicalAttack(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getDouble("power", 0);
		_criticalChance = params.getInt("criticalChance", 0);
		_ignoreShieldDefence = params.getBoolean("ignoreShieldDefence", false);
	}
	
	@Override
	public boolean calcSuccess(BuffInfo info) {
		return !Formulas.calcPhysicalSkillEvasion(info.getEffector(), info.getEffected(), info.getSkill());
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.PHYSICAL_ATTACK;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		L2Character target = info.getEffected();
		L2Character activeChar = info.getEffector();
		Skill skill = info.getSkill();
		
		if (activeChar.isAlikeDead()) {
			return;
		}
		
		if (target.isPlayer() && target.getActingPlayer().isFakeDeath()) {
			target.stopFakeDeath(true);
		}
		
		double damage = 0;
		byte shield = 0;
		boolean ss = skill.isPhysical() && activeChar.isChargedShot(ShotType.SOULSHOTS);
		
		if (!_ignoreShieldDefence) {
			shield = Formulas.calcShldUse(activeChar, target, skill, true);
		}
		
		// Physical damage critical rate is only affected by STR.
		boolean crit = false;
		if (_criticalChance > 0) {
			crit = Formulas.calcSkillCrit(activeChar, target, _criticalChance);
		}
		
		damage = Formulas.calcSkillPhysDam(activeChar, target, skill, shield, false, ss, _power);
		
		if (crit) {
			damage *= 2;
		}
		
		if (damage > 0) {
			activeChar.sendDamageMessage(target, (int) damage, false, crit, false);
			target.reduceCurrentHp(damage, activeChar, skill);
			target.notifyDamageReceived(damage, activeChar, skill, crit, false, false);
			
			// Check if damage should be reflected
			Formulas.calcDamageReflected(activeChar, target, skill, crit);
		} else {
			activeChar.sendPacket(SystemMessageId.ATTACK_FAILED);
		}
	}
}