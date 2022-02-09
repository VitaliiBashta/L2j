
package com.l2jserver.datapack.handlers.actionhandlers;

import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.handler.IActionHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerSummonTalk;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.PetStatusShow;

public class L2SummonAction implements IActionHandler {
	@Override
	public boolean action(L2PcInstance activeChar, L2Object target, boolean interact) {
		// Aggression target lock effect
		if (activeChar.isLockedTarget() && (activeChar.getLockedTarget() != target)) {
			activeChar.sendPacket(SystemMessageId.FAILED_CHANGE_TARGET);
			return false;
		}
		
		if ((activeChar == ((L2Summon) target).getOwner()) && (activeChar.getTarget() == target)) {
			activeChar.sendPacket(new PetStatusShow((L2Summon) target));
			activeChar.updateNotMoveUntil();
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			
			// Notify to scripts
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSummonTalk((L2Summon) target), (L2Summon) target);
		} else if (activeChar.getTarget() != target) {
			activeChar.setTarget(target);
		} else if (interact) {
			if (target.isAutoAttackable(activeChar)) {
				if (GeoData.getInstance().canSeeTarget(activeChar, target)) {
					activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
					activeChar.onActionRequest();
				}
			} else {
				// This Action Failed packet avoids activeChar getting stuck when clicking three or more times
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				if (((L2Summon) target).isInsideRadius(activeChar, 150, false, false)) {
					activeChar.updateNotMoveUntil();
				} else {
					if (GeoData.getInstance().canSeeTarget(activeChar, target)) {
						activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, target);
					}
				}
			}
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType() {
		return InstanceType.L2Summon;
	}
}
