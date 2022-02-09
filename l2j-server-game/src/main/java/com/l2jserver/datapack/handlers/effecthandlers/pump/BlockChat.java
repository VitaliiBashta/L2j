
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.instancemanager.PunishmentManager;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.punishment.PunishmentAffect;
import com.l2jserver.gameserver.model.punishment.PunishmentTask;
import com.l2jserver.gameserver.model.punishment.PunishmentType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Block Chat effect implementation.
 * @author BiggBoss
 */
public final class BlockChat extends AbstractEffect {
	public BlockChat(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean canStart(BuffInfo info) {
		return (info.getEffected() != null) && info.getEffected().isPlayer();
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.CHAT_BLOCK;
	}
	
	@Override
	public void onExit(BuffInfo info) {
		PunishmentManager.getInstance().stopPunishment(info.getEffected().getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN);
	}
	
	@Override
	public void onStart(BuffInfo info) {
		PunishmentManager.getInstance().startPunishment(new PunishmentTask(0, info.getEffected().getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN, 0, "Chat banned bot report", "system", true));
	}
}
