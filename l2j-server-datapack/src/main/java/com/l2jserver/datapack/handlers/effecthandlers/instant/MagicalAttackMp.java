
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
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Magical Attack MP effect.
 * @author Adry_85
 */
public final class MagicalAttackMp extends AbstractEffect {
	private final double _power;
	
	public MagicalAttackMp(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getDouble("power", 0);
	}
	
	@Override
	public boolean calcSuccess(BuffInfo info) {
		if (info.getEffected().isInvul() || info.getEffected().isMpBlocked()) {
			return false;
		}
		if (!Formulas.calcMagicAffected(info.getEffector(), info.getEffected(), info.getSkill())) {
			if (info.getEffector().isPlayer()) {
				info.getEffector().sendPacket(SystemMessageId.ATTACK_FAILED);
			}
			if (info.getEffected().isPlayer()) {
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_RESISTED_C2_DRAIN2);
				sm.addCharName(info.getEffected());
				sm.addCharName(info.getEffector());
				info.getEffected().sendPacket(sm);
			}
			return false;
		}
		return true;
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
		
		boolean sps = skill.useSpiritShot() && activeChar.isChargedShot(ShotType.SPIRITSHOTS);
		boolean bss = skill.useSpiritShot() && activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		final byte shld = Formulas.calcShldUse(activeChar, target, skill);
		final boolean mcrit = Formulas.calcMCrit(activeChar.getMCriticalHit(target, skill));
		double damage = Formulas.calcManaDam(activeChar, target, skill, shld, sps, bss, mcrit, _power);
		double mp = (damage > target.getCurrentMp() ? target.getCurrentMp() : damage);
		
		if (damage > 0) {
			target.stopEffectsOnDamage(true);
			target.setCurrentMp(target.getCurrentMp() - mp);
		}
	}
}