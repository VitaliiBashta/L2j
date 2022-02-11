
package com.l2jserver.datapack.handlers.actionhandlers;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.handler.IActionHandler;
import com.l2jserver.gameserver.instancemanager.MercTicketManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class L2ItemInstanceAction implements IActionHandler {
	@Override
	public boolean action(L2PcInstance activeChar, L2Object target, boolean interact) {
		// this causes the validate position handler to do the pickup if the location is reached.
		// mercenary tickets can only be picked up by the castle owner.
		final int castleId = MercTicketManager.getInstance().getTicketCastleId(target.getId());
		
		if ((castleId > 0) && (!activeChar.isCastleLord(castleId) || activeChar.isInParty())) {
			if (activeChar.isInParty()) {
				activeChar.sendMessage("You cannot pickup mercenaries while in a party.");
			} else {
				activeChar.sendMessage("Only the castle lord can pickup mercenaries.");
			}
			
			activeChar.setTarget(target);
			activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		} else if (!activeChar.isFlying()) {
			activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, target);
		}
		
		return true;
	}
	
	@Override
	public InstanceType getInstanceType() {
		return InstanceType.L2ItemInstance;
	}
}