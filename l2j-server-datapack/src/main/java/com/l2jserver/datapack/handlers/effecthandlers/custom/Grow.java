
package com.l2jserver.datapack.handlers.effecthandlers.custom;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Grow effect implementation.
 */
public final class Grow extends AbstractEffect {
	public Grow(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.BUFF;
	}
	
	@Override
	public void onExit(BuffInfo info) {
		if (info.getEffected().isNpc()) {
			L2Npc npc = (L2Npc) info.getEffected();
			npc.setCollisionRadius(npc.getTemplate().getfCollisionRadius());
		}
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffected().isNpc()) {
			L2Npc npc = (L2Npc) info.getEffected();
			npc.setCollisionRadius(npc.getTemplate().getCollisionRadiusGrown());
		}
	}
}
