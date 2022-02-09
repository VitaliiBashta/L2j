
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.CrystalType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.stats.Formulas;
import com.l2jserver.gameserver.model.stats.Stats;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Heal effect implementation.
 * @author UnAfraid
 */
public final class Heal extends AbstractEffect {
	private final double _power;
	
	public Heal(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getDouble("power", 0);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.HP;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		final L2Character target = info.getEffected();
		final L2Character activeChar = info.getEffector();
		final Skill skill = info.getSkill();
		if ((target == null) || target.isDead() || target.isDoor() || target.isInvul()) {
			return;
		}
		
		double amount = _power;
		double staticShotBonus = 0;
		int mAtkMul = 1;
		boolean sps = skill.isMagic() && activeChar.isChargedShot(ShotType.SPIRITSHOTS);
		boolean bss = skill.isMagic() && activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		
		if (((sps || bss) && (activeChar.isPlayer() && activeChar.getActingPlayer().isMageClass())) || activeChar.isSummon()) {
			staticShotBonus = skill.getMpConsume2(); // static bonus for spiritshots
			mAtkMul = bss ? 4 : 2;
			staticShotBonus *= bss ? 2.4 : 1.0;
		} else if ((sps || bss) && activeChar.isNpc()) {
			staticShotBonus = 2.4 * skill.getMpConsume2(); // always blessed spiritshots
			mAtkMul = 4;
		} else {
			// no static bonus
			// grade dynamic bonus
			final L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
			if (weaponInst != null) {
				mAtkMul = weaponInst.getItem().getItemGrade() == CrystalType.S84 ? 4 : weaponInst.getItem().getItemGrade() == CrystalType.S80 ? 2 : 1;
			}
			// shot dynamic bonus
			mAtkMul = bss ? mAtkMul * 4 : mAtkMul + 1;
		}
		
		if (!skill.isStatic()) {
			amount += staticShotBonus + Math.sqrt(mAtkMul * activeChar.getMAtk(activeChar, null));
			amount = target.calcStat(Stats.HEAL_EFFECT, amount, null, null);
			// Heal critic, since CT2.3 Gracia Final
			if (skill.isMagic() && Formulas.calcMCrit(activeChar.getMCriticalHit(target, skill))) {
				amount *= 3;
			}
		}
		
		// Prevents overheal and negative amount
		amount = Math.max(Math.min(amount, target.getMaxRecoverableHp() - target.getCurrentHp()), 0);
		if (amount != 0) {
			target.setCurrentHp(amount + target.getCurrentHp());
		}
		
		if (target.isPlayer()) {
			if (skill.getId() == 4051) {
				target.sendPacket(SystemMessageId.REJUVENATING_HP);
			} else {
				if (activeChar.isPlayer() && (activeChar != target)) {
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HP_HAS_BEEN_RESTORED_BY_C1);
					sm.addString(activeChar.getName());
					sm.addInt((int) amount);
					target.sendPacket(sm);
				} else {
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HP_HAS_BEEN_RESTORED);
					sm.addInt((int) amount);
					target.sendPacket(sm);
				}
			}
		}
	}
}
