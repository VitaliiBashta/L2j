
package com.l2jserver.datapack.handlers.effecthandlers.custom;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.entity.Instance;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Open Door effect implementation.
 * @author Adry_85
 */
public final class OpenDoor extends AbstractEffect {
	private final int _chance;
	private final boolean _isItem;
	
	public OpenDoor(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_chance = params.getInt("chance", 0);
		_isItem = params.getBoolean("isItem", false);
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
		
		final L2Character effector = info.getEffector();
		L2DoorInstance door = (L2DoorInstance) info.getEffected();
		// Check if door in the different instance
		if (effector.getInstanceId() != door.getInstanceId()) {
			// Search for the instance
			final Instance inst = InstanceManager.getInstance().getInstance(effector.getInstanceId());
			if (inst == null) {
				// Instance not found
				return;
			}
			final L2DoorInstance instanceDoor = inst.getDoor(door.getId());
			if (instanceDoor != null) {
				// Door found
				door = instanceDoor;
			}
			
			// Checking instance again
			if (effector.getInstanceId() != door.getInstanceId()) {
				return;
			}
		}
		
		if ((!door.isOpenableBySkill() && !_isItem) || (door.getFort() != null)) {
			effector.sendPacket(SystemMessageId.UNABLE_TO_UNLOCK_DOOR);
			return;
		}
		
		if ((Rnd.get(100) < _chance) && !door.getOpen()) {
			door.openMe();
		} else {
			effector.sendPacket(SystemMessageId.FAILED_TO_UNLOCK_DOOR);
		}
	}
}
