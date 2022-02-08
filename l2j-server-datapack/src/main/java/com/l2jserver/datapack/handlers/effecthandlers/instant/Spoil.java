
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.ai.CtrlEvent;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.stats.Formulas;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Spoil effect implementation.
 * @author _drunk_, Ahmed, Zoey76
 */
public final class Spoil extends AbstractEffect {
	public Spoil(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean calcSuccess(BuffInfo info) {
		return Formulas.calcMagicSuccess(info.getEffector(), info.getEffected(), info.getSkill());
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (!info.getEffected().isMonster() || info.getEffected().isDead()) {
			info.getEffector().sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		
		final L2MonsterInstance target = (L2MonsterInstance) info.getEffected();
		if (target.isSpoiled()) {
			info.getEffector().sendPacket(SystemMessageId.ALREADY_SPOILED);
			return;
		}
		
		target.setSpoilerObjectId(info.getEffector().getObjectId());
		info.getEffector().sendPacket(SystemMessageId.SPOIL_SUCCESS);
		target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, info.getEffector());
	}
}
