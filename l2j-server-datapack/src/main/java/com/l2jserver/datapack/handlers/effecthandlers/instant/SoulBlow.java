
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.stats.Formulas;

/**
 * Soul Blow effect implementation.
 * @author Adry_85
 */
public final class SoulBlow extends AbstractEffect {
	private final double _power;
	private final int _blowChance;
	
	public SoulBlow(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getDouble("power", 0);
		_blowChance = params.getInt("blowChance", 0);
	}
	
	/**
	 * If is not evaded and blow lands.
	 */
	@Override
	public boolean calcSuccess(BuffInfo info) {
		return !Formulas.calcPhysicalSkillEvasion(info.getEffector(), info.getEffected(), info.getSkill()) && Formulas.calcBlowSuccess(info.getEffector(), info.getEffected(), info.getSkill(), _blowChance);
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
		
		if (activeChar.isAlikeDead()) {
			return;
		}
		
		boolean ss = info.getSkill().useSoulShot() && activeChar.isChargedShot(ShotType.SOULSHOTS);
		byte shld = Formulas.calcShldUse(activeChar, target, info.getSkill());
		double damage = Formulas.calcBlowDamage(activeChar, target, info.getSkill(), shld, ss, _power);
		if ((info.getSkill().getMaxSoulConsumeCount() > 0) && activeChar.isPlayer()) {
			// Souls Formula (each soul increase +4%)
			damage *= 1 + (info.getCharges() * 0.04);
		}
		
		target.reduceCurrentHp(damage, activeChar, info.getSkill());
		target.notifyDamageReceived(damage, activeChar, info.getSkill(), false, false, false);
		
		// Manage attack or cast break of the target (calculating rate, sending message...)
		if (!target.isRaid() && Formulas.calcAtkBreak(target, damage)) {
			target.breakAttack();
			target.breakCast();
		}
		
		if (activeChar.isPlayer()) {
			L2PcInstance activePlayer = activeChar.getActingPlayer();
			activePlayer.sendDamageMessage(target, (int) damage, false, true, false);
		}
		// Check if damage should be reflected
		Formulas.calcDamageReflected(activeChar, target, info.getSkill(), true);
	}
}