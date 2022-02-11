
package com.l2jserver.datapack.handlers.itemhandlers;

import com.l2jserver.gameserver.SevenSigns;
import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.instancemanager.MercTicketManager;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import org.springframework.stereotype.Service;

@Service
public class MercTicket implements IItemHandler {
	/**
	 * handler for using mercenary tickets. Things to do: 1) Check constraints: 1.a) Tickets may only be used in a castle 1.b) Only specific tickets may be used in each castle (different tickets for each castle) 1.c) only the owner of that castle may use them 1.d) tickets cannot be used during siege
	 * 1.e) Check if max number of tickets has been reached 1.f) Check if max number of tickets from this ticket's TYPE has been reached 2) If allowed, call the MercTicketManager to add the item and spawn in the world 3) Remove the item from the person's inventory
	 */
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
		if (!playable.isPlayer()) {
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		
		int itemId = item.getId();
		L2PcInstance activeChar = (L2PcInstance) playable;
		Castle castle = CastleManager.getInstance().getCastle(activeChar);
		int castleId = -1;
		if (castle != null) {
			castleId = castle.getResidenceId();
		}
		
		// add check that certain tickets can only be placed in certain castles
		if (MercTicketManager.getInstance().getTicketCastleId(itemId) != castleId) {
			activeChar.sendPacket(SystemMessageId.MERCENARIES_CANNOT_BE_POSITIONED_HERE);
			return false;
		} else if (!activeChar.isCastleLord(castleId)) {
			activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_AUTHORITY_TO_POSITION_MERCENARIES);
			return false;
		} else if ((castle != null) && castle.getSiege().isInProgress()) {
			activeChar.sendPacket(SystemMessageId.THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE);
			return false;
		}
		
		// Checking Seven Signs Quest Period
		if (SevenSigns.getInstance().getCurrentPeriod() != SevenSigns.PERIOD_SEAL_VALIDATION) {
			// _log.warning("Someone has tried to spawn a guardian during Quest Event Period of The Seven Signs.");
			activeChar.sendPacket(SystemMessageId.THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE);
			return false;
		}
		// Checking the Seal of Strife status
		switch (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE)) {
			case SevenSigns.CABAL_NULL: {
				if (SevenSigns.getInstance().checkIsDawnPostingTicket(itemId)) {
					// _log.warning("Someone has tried to spawn a Dawn Mercenary though the Seal of Strife is not controlled by anyone.");
					activeChar.sendPacket(SystemMessageId.THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE);
					return false;
				}
				break;
			}
			case SevenSigns.CABAL_DUSK: {
				if (!SevenSigns.getInstance().checkIsRookiePostingTicket(itemId)) {
					// _log.warning("Someone has tried to spawn a non-Rookie Mercenary though the Seal of Strife is controlled by Revolutionaries of Dusk.");
					activeChar.sendPacket(SystemMessageId.THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE);
					return false;
				}
				break;
			}
			case SevenSigns.CABAL_DAWN: {
				break;
			}
		}
		
		if (MercTicketManager.getInstance().isAtCasleLimit(item.getId())) {
			activeChar.sendPacket(SystemMessageId.THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE);
			return false;
		} else if (MercTicketManager.getInstance().isAtTypeLimit(item.getId())) {
			activeChar.sendPacket(SystemMessageId.THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE);
			return false;
		} else if (MercTicketManager.getInstance().isTooCloseToAnotherTicket(activeChar.getX(), activeChar.getY(), activeChar.getZ())) {
			activeChar.sendPacket(SystemMessageId.POSITIONING_CANNOT_BE_DONE_BECAUSE_DISTANCE_BETWEEN_MERCENARIES_TOO_SHORT);
			return false;
		}
		
		MercTicketManager.getInstance().addTicket(item.getId(), activeChar);
		activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false); // Remove item from char's inventory
		activeChar.sendPacket(SystemMessageId.PLACE_CURRENT_LOCATION_DIRECTION);
		return true;
	}
}
