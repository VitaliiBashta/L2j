
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Crystal Grade Modify effect implementation.
 * @author Zoey76
 */
public final class CrystalGradeModify extends AbstractEffect {
	private final int _grade;
	
	public CrystalGradeModify(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_grade = params.getInt("grade", 0);
	}
	
	@Override
	public boolean canStart(BuffInfo info) {
		return info.getEffected().isPlayer();
	}
	
	@Override
	public void onExit(BuffInfo info) {
		final L2PcInstance player = info.getEffected().getActingPlayer();
		if (player != null) {
			player.setExpertisePenaltyBonus(0);
			player.refreshExpertisePenalty();
		}
	}
	
	@Override
	public void onStart(BuffInfo info) {
		final L2PcInstance player = info.getEffected().getActingPlayer();
		if (player != null) {
			player.setExpertisePenaltyBonus(_grade);
			player.refreshExpertisePenalty();
		}
	}
}