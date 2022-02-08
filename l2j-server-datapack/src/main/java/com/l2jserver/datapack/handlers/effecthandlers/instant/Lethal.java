
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.stats.Formulas;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Lethal effect implementation.
 * @author Adry_85
 */
public final class Lethal extends AbstractEffect {
	private final double _fullLethal;
	private final double _halfLethal;
	
	public Lethal(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_fullLethal = params.getDouble("fullLethal", .0);
		_halfLethal = params.getDouble("halfLethal", .0);
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
		if (activeChar.isPlayer() && !activeChar.getAccessLevel().canGiveDamage()) {
			return;
		}
		
		if (skill.getMagicLevel() < (target.getLevel() - 6)) {
			return;
		}
		
		if (!target.isLethalable() || target.isInvul()) {
			return;
		}
		
		double chanceMultiplier = Formulas.calcAttributeBonus(activeChar, target, skill) * Formulas.calcGeneralTraitBonus(activeChar, target, skill.getTraitType(), false);
		// Lethal Strike
		if (Rnd.get(100) < (_fullLethal * chanceMultiplier)) {
			// for Players CP and HP is set to 1.
			if (target.isPlayer()) {
				target.notifyDamageReceived(target.getCurrentHp() - 1, activeChar, skill, true, false, false);
				target.setCurrentCp(1);
				target.setCurrentHp(1);
				target.sendPacket(SystemMessageId.LETHAL_STRIKE);
			}
			// for Monsters HP is set to 1.
			else if (target.isMonster() || target.isSummon()) {
				target.notifyDamageReceived(target.getCurrentHp() - 1, activeChar, skill, true, false, false);
				target.setCurrentHp(1);
			}
			activeChar.sendPacket(SystemMessageId.LETHAL_STRIKE_SUCCESSFUL);
		}
		// Half-Kill
		else if (Rnd.get(100) < (_halfLethal * chanceMultiplier)) {
			// for Players CP is set to 1.
			if (target.isPlayer()) {
				target.setCurrentCp(1);
				target.sendPacket(SystemMessageId.HALF_KILL);
				target.sendPacket(SystemMessageId.CP_DISAPPEARS_WHEN_HIT_WITH_A_HALF_KILL_SKILL);
			}
			// for Monsters HP is set to 50%.
			else if (target.isMonster() || target.isSummon()) {
				target.notifyDamageReceived(target.getCurrentHp() * 0.5, activeChar, skill, true, false, false);
				target.setCurrentHp(target.getCurrentHp() * 0.5);
			}
			activeChar.sendPacket(SystemMessageId.HALF_KILL);
		}
	}
}