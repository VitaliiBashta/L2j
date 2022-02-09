
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExVoteSystemInfo;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.network.serverpackets.UserInfo;

/**
 * Give Recommendation effect implementation.
 * @author NosBit
 */
public final class GiveRecommendation extends AbstractEffect {
	private final int _amount;
	
	public GiveRecommendation(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_amount = params.getInt("amount", 0);
		if (_amount == 0) {
			_log.warning(getClass().getSimpleName() + ": amount parameter is missing or set to 0. id:" + set.getInt("id", -1));
		}
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		L2PcInstance target = info.getEffected() instanceof L2PcInstance ? (L2PcInstance) info.getEffected() : null;
		if (target != null) {
			int recommendationsGiven = _amount;
			
			if ((target.getRecomHave() + _amount) >= 255) {
				recommendationsGiven = 255 - target.getRecomHave();
			}
			
			if (recommendationsGiven > 0) {
				target.setRecomHave(target.getRecomHave() + recommendationsGiven);
				
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_OBTAINED_S1_RECOMMENDATIONS);
				sm.addInt(recommendationsGiven);
				target.sendPacket(sm);
				target.sendPacket(new UserInfo(target));
				target.sendPacket(new ExVoteSystemInfo(target));
			} else {
				L2PcInstance player = info.getEffector() instanceof L2PcInstance ? (L2PcInstance) info.getEffector() : null;
				if (player != null) {
					player.sendPacket(SystemMessageId.NOTHING_HAPPENED);
				}
			}
		}
	}
}
