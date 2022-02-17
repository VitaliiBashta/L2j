package com.l2jserver.gameserver.network.clientpackets;

import com.l2jserver.gameserver.SevenSignsFestival;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.L2Event;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.taskmanager.AttackStanceTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.l2jserver.gameserver.config.Configuration.general;

public final class Logout extends L2GameClientPacket {
	
	private static final Logger LOG_ACCOUNTING = LoggerFactory.getLogger("accounting");
	
	private static final String _C__00_LOGOUT = "[C] 00 Logout";
  private final AttackStanceTaskManager attackStanceTaskManager;

  public Logout(AttackStanceTaskManager attackStanceTaskManager) {
    this.attackStanceTaskManager = attackStanceTaskManager;
  }

	@Override
	protected void readImpl() {
		
	}
	
	@Override
	protected void runImpl() {
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null) {
			return;
		}
		
		if ((player.getActiveEnchantItemId() != L2PcInstance.ID_NONE) || (player.getActiveEnchantAttrItemId() != L2PcInstance.ID_NONE)) {
			if (general().debug()) {
				_log.fine("Player " + player.getName() + " tried to logout while enchanting.");
			}
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isLocked()) {
			_log.warning("Player " + player.getName() + " tried to logout during class change.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

    // Don't allow leaving if player is fighting
    if (attackStanceTaskManager.getInstance().hasAttackStanceTask(player)) {
			if (player.isGM() && general().gmRestartFighting()) {
				return;
			}
			
			if (general().debug()) {
				_log.fine("Player " + player.getName() + " tried to logout while fighting.");
			}
			
			player.sendPacket(SystemMessageId.CANT_LOGOUT_WHILE_FIGHTING);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (L2Event.isParticipant(player)) {
			player.sendMessage("A superior power doesn't allow you to leave the event.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Prevent player from logging out if they are a festival participant
		// and it is in progress, otherwise notify party members that the player
		// is not longer a participant.
		if (player.isFestivalParticipant()) {
			if (SevenSignsFestival.getInstance().isFestivalInitialized()) {
				player.sendMessage("You cannot log out while you are a participant in a Festival.");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (player.isInParty()) {
				player.getParty().broadcastPacket(SystemMessage.sendString(player.getName() + " has been removed from the upcoming Festival."));
			}
		}
		
		// Remove player from Boss Zone
		player.removeFromBossZone();
		
		LOG_ACCOUNTING.info("Client {} logged out.", getClient());
		
		player.logout();
	}
	
	@Override
	public String getType() {
		return _C__00_LOGOUT;
	}
}