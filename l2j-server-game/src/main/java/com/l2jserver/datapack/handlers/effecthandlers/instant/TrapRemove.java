
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.enums.TrapAction;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2TrapInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.trap.OnTrapAction;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Trap Remove effect implementation.
 * @author UnAfraid
 */
public final class TrapRemove extends AbstractEffect {
	private final int _power;
	
	public TrapRemove(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		if (params.isEmpty()) {
			throw new IllegalArgumentException(getClass().getSimpleName() + ": effect without power!");
		}
		
		_power = params.getInt("power");
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		final L2Character target = info.getEffected();
		if (!target.isTrap()) {
			return;
		}
		
		if (target.isAlikeDead()) {
			return;
		}
		
		final L2TrapInstance trap = (L2TrapInstance) target;
		if (!trap.canBeSeen(info.getEffector())) {
			if (info.getEffector().isPlayer()) {
				info.getEffector().sendPacket(SystemMessageId.INCORRECT_TARGET);
			}
			return;
		}
		
		if (trap.getLevel() > _power) {
			return;
		}
		
		// Notify to scripts
		EventDispatcher.getInstance().notifyEventAsync(new OnTrapAction(trap, info.getEffector(), TrapAction.TRAP_DISARMED), trap);
		
		trap.unSummon();
		if (info.getEffector().isPlayer()) {
			info.getEffector().sendPacket(SystemMessageId.A_TRAP_DEVICE_HAS_BEEN_STOPPED);
		}
	}
}
