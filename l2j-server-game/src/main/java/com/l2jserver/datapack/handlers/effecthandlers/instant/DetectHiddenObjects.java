
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Detect Hidden Objects effect implementation.
 * @author UnAfraid
 */
public final class DetectHiddenObjects extends AbstractEffect {
	public DetectHiddenObjects(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (!info.getEffected().isDoor()) {
			return;
		}
		
		final L2DoorInstance door = (L2DoorInstance) info.getEffected();
		if (door.getTemplate().isStealth()) {
			door.setMeshIndex(1);
			door.setTargetable(door.getTemplate().getOpenType() != 0);
			door.broadcastStatusUpdate();
		}
	}
}
