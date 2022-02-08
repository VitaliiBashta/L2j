
package com.l2jserver.datapack.handlers.voicedcommandhandlers;

import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * @author Zoey76
 */
public class CastleVCmd implements IVoicedCommandHandler {
	private static final String[] VOICED_COMMANDS = {
		"opendoors",
		"closedoors",
		"ridewyvern"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params) {
		switch (command) {
			case "opendoors":
				if (!params.equals("castle")) {
					activeChar.sendMessage("Only Castle doors can be open.");
					return false;
				}
				
				if (!activeChar.isClanLeader()) {
					activeChar.sendPacket(SystemMessageId.ONLY_CLAN_LEADER_CAN_ISSUE_COMMANDS);
					return false;
				}
				
				final L2DoorInstance door = (L2DoorInstance) activeChar.getTarget();
				if (door == null) {
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					return false;
				}
				
				final Castle castle = CastleManager.getInstance().getCastleById(activeChar.getClan().getCastleId());
				if (castle == null) {
					activeChar.sendMessage("Your clan does not own a castle.");
					return false;
				}
				
				if (castle.getSiege().isInProgress()) {
					activeChar.sendPacket(SystemMessageId.GATES_NOT_OPENED_CLOSED_DURING_SIEGE);
					return false;
				}
				
				if (castle.checkIfInZone(door.getX(), door.getY(), door.getZ())) {
					activeChar.sendPacket(SystemMessageId.GATE_IS_OPENING);
					door.openMe();
				}
				break;
			case "closedoors":
				if (!params.equals("castle")) {
					activeChar.sendMessage("Only Castle doors can be closed.");
					return false;
				}
				if (!activeChar.isClanLeader()) {
					activeChar.sendPacket(SystemMessageId.ONLY_CLAN_LEADER_CAN_ISSUE_COMMANDS);
					return false;
				}
				final L2DoorInstance door2 = (L2DoorInstance) activeChar.getTarget();
				if (door2 == null) {
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					return false;
				}
				final Castle castle2 = CastleManager.getInstance().getCastleById(activeChar.getClan().getCastleId());
				if (castle2 == null) {
					activeChar.sendMessage("Your clan does not own a castle.");
					return false;
				}
				
				if (castle2.getSiege().isInProgress()) {
					activeChar.sendPacket(SystemMessageId.GATES_NOT_OPENED_CLOSED_DURING_SIEGE);
					return false;
				}
				
				if (castle2.checkIfInZone(door2.getX(), door2.getY(), door2.getZ())) {
					activeChar.sendMessage("The gate is being closed.");
					door2.closeMe();
				}
				break;
			case "ridewyvern":
				if (activeChar.isClanLeader() && (activeChar.getClan().getCastleId() > 0)) {
					activeChar.mount(12621, 0, true);
				}
				break;
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList() {
		return VOICED_COMMANDS;
	}
}
