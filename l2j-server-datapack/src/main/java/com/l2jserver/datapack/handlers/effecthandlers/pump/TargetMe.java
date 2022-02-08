
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Target Me effect implementation.
 * @author -Nemesiss-
 */
public final class TargetMe extends AbstractEffect {
	public TargetMe(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public void onExit(BuffInfo info) {
		if (info.getEffected().isPlayable()) {
			((L2Playable) info.getEffected()).setLockedTarget(null);
		}
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffected().isPlayable()) {
			if (info.getEffected().getTarget() != info.getEffector()) {
				L2PcInstance effector = info.getEffector().getActingPlayer();
				// If effector is null, then its not a player, but NPC. If its not null, then it should check if the skill is pvp skill.
				if ((effector == null) || effector.checkPvpSkill(info.getEffected(), info.getSkill())) {
					// Target is different
					info.getEffected().setTarget(info.getEffector());
				}
			}
			
			((L2Playable) info.getEffected()).setLockedTarget(info.getEffector());
		}
	}
}
