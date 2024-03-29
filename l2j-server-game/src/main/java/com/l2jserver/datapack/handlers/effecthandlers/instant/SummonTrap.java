
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2TrapInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Summon Trap effect implementation.
 * @author Zoey76
 */
public final class SummonTrap extends AbstractEffect {
	private final int _despawnTime;
	private final int _npcId;
	
	public SummonTrap(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_despawnTime = params.getInt("despawnTime", 0);
		_npcId = params.getInt("npcId", 0);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if ((info.getEffected() == null) || !info.getEffected().isPlayer() || info.getEffected().isAlikeDead() || info.getEffected().getActingPlayer().inObserverMode()) {
			return;
		}
		
		if (_npcId <= 0) {
			_log.warning(SummonTrap.class.getSimpleName() + ": Invalid NPC ID:" + _npcId + " in skill ID: " + info.getSkill().getId());
			return;
		}
		
		final L2PcInstance player = info.getEffected().getActingPlayer();
		if (player.inObserverMode() || player.isMounted()) {
			return;
		}
		
		// Unsummon previous trap
		if (player.getTrap() != null) {
			player.getTrap().unSummon();
		}
		
		final L2NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(_npcId);
		if (npcTemplate == null) {
			_log.warning(SummonTrap.class.getSimpleName() + ": Spawn of the non-existing Trap ID: " + _npcId + " in skill ID:" + info.getSkill().getId());
			return;
		}
		
		final L2TrapInstance trap = new L2TrapInstance(npcTemplate, player, _despawnTime);
		trap.setCurrentHp(trap.getMaxHp());
		trap.setCurrentMp(trap.getMaxMp());
		trap.setIsInvul(true);
		trap.setHeading(player.getHeading());
		trap.spawnMe(player.getX(), player.getY(), player.getZ());
		player.setTrap(trap);
	}
}
