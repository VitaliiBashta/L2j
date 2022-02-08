
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Hide effect implementation.
 * @author ZaKaX, nBd
 */
public final class Hide extends AbstractEffect {
	public Hide(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public void onExit(BuffInfo info) {
		if (info.getEffected().isPlayer()) {
			L2PcInstance activeChar = info.getEffected().getActingPlayer();
			if (!activeChar.inObserverMode()) {
				activeChar.setInvisible(false);
			}
		}
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffected().isPlayer()) {
			L2PcInstance activeChar = info.getEffected().getActingPlayer();
			activeChar.setInvisible(true);
			
			if ((activeChar.getAI().getNextIntention() != null) && (activeChar.getAI().getNextIntention().getCtrlIntention() == CtrlIntention.AI_INTENTION_ATTACK)) {
				activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			}
			
			for (L2Character target : activeChar.getKnownList().getKnownCharacters()) {
				if ((target != null) && (target.getTarget() == activeChar)) {
					target.setTarget(null);
					target.abortAttack();
					target.abortCast();
					target.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				}
			}
		}
	}
}