
package com.l2jserver.datapack.handlers.effecthandlers.custom;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.AbnormalType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Detection effect implementation.
 * @author UnAfraid
 */
public final class Detection extends AbstractEffect {
	public Detection(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (!info.getEffector().isPlayer() || !info.getEffected().isPlayer()) {
			return;
		}
		
		final L2PcInstance player = info.getEffector().getActingPlayer();
		final L2PcInstance target = info.getEffected().getActingPlayer();
		
		if (target.isInvisible()) {
			if (player.isInPartyWith(target)) {
				return;
			}
			if (player.isInClanWith(target)) {
				return;
			}
			if (player.isInAllyWith(target)) {
				return;
			}
			// Remove Hide.
			target.getEffectList().stopSkillEffects(true, AbnormalType.HIDE);
		}
	}
}
