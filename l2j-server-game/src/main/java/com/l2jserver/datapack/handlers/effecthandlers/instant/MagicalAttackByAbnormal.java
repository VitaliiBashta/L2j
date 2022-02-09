
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.stats.Formulas;
import com.l2jserver.gameserver.model.stats.Stats;

/**
 * Magical Attack By Abnormal effect implementation.
 * @author Adry_85
 */
public final class MagicalAttackByAbnormal extends AbstractEffect {
	private final double _power;
	
	public MagicalAttackByAbnormal(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getDouble("power", 0);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.MAGICAL_ATTACK;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffector().isAlikeDead()) {
			return;
		}
		
		final L2Character target = info.getEffected();
		final L2Character activeChar = info.getEffector();
		final Skill skill = info.getSkill();
		if (target.isPlayer() && target.getActingPlayer().isFakeDeath()) {
			target.stopFakeDeath(true);
		}
		
		boolean sps = skill.useSpiritShot() && activeChar.isChargedShot(ShotType.SPIRITSHOTS);
		boolean bss = skill.useSpiritShot() && activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		final boolean mcrit = Formulas.calcMCrit(activeChar.getMCriticalHit(target, skill));
		final byte shld = Formulas.calcShldUse(activeChar, target, skill);
		double damage = Formulas.calcMagicDam(activeChar, target, skill, shld, sps, bss, mcrit, _power);
		
		// each buff increase +30%
		damage *= (((target.getBuffCount() * 0.3) + 1.3) / 4);
		
		if (damage > 0) {
			// Manage attack or cast break of the target (calculating rate, sending message...)
			if (!target.isRaid() && Formulas.calcAtkBreak(target, damage)) {
				target.breakAttack();
				target.breakCast();
			}
			
			// Shield Deflect Magic: Reflect all damage on caster.
			if (target.getStat().calcStat(Stats.VENGEANCE_SKILL_MAGIC_DAMAGE, 0, target, skill) > Rnd.get(100)) {
				activeChar.reduceCurrentHp(damage, target, skill);
				activeChar.notifyDamageReceived(damage, target, skill, mcrit, false, true);
			} else {
				target.reduceCurrentHp(damage, activeChar, skill);
				target.notifyDamageReceived(damage, activeChar, skill, mcrit, false, false);
				activeChar.sendDamageMessage(target, (int) damage, mcrit, false, false);
			}
		}
	}
}