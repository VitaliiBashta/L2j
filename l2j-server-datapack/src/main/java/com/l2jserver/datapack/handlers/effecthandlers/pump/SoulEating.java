
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.character.playable.OnPlayableExpChanged;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.stats.Stats;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExSpawnEmitter;

/**
 * Soul Eating effect implementation.
 * @author UnAfraid
 */
public final class SoulEating extends AbstractEffect {
	private final int _expNeeded;
	
	public SoulEating(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		_expNeeded = params.getInt("expNeeded");
	}
	
	@Override
	public void onExit(BuffInfo info) {
		if (info.getEffected().isPlayer()) {
			info.getEffected().removeListenerIf(EventType.ON_PLAYABLE_EXP_CHANGED, listener -> listener.getOwner() == this);
		}
	}
	
	public void onExperienceReceived(L2Playable playable, long exp) {
		// TODO: Verify logic.
		if (playable.isPlayer() && (exp >= _expNeeded)) {
			final L2PcInstance player = playable.getActingPlayer();
			final int maxSouls = (int) player.calcStat(Stats.MAX_SOULS, 0, null, null);
			if (player.getChargedSouls() >= maxSouls) {
				playable.sendPacket(SystemMessageId.SOUL_CANNOT_BE_ABSORBED_ANYMORE);
				return;
			}
			
			player.increaseSouls(1);
			
			if ((player.getTarget() != null) && player.getTarget().isNpc()) {
				final L2Npc npc = (L2Npc) playable.getTarget();
				player.broadcastPacket(new ExSpawnEmitter(player, npc), 500);
			}
		}
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffected().isPlayer()) {
			info.getEffected().addListener(new ConsumerEventListener(info.getEffected(), EventType.ON_PLAYABLE_EXP_CHANGED, (OnPlayableExpChanged event) -> onExperienceReceived(event.getActiveChar(), (event.getNewExp() - event.getOldExp())), this));
		}
	}
}
