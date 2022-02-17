package com.l2jserver.gameserver.network.clientpackets;

import com.l2jserver.gameserver.instancemanager.BoatManager;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.instance.L2BoatInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.type.WeaponType;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.MoveToLocationInVehicle;
import com.l2jserver.gameserver.network.serverpackets.StopMoveInVehicle;

import static com.l2jserver.gameserver.config.Configuration.character;

public class RequestMoveToLocationInVehicle extends L2GameClientPacket {
	private static final String _C__75_MOVETOLOCATIONINVEHICLE = "[C] 75 RequestMoveToLocationInVehicle";
	
	private int _boatId;
	private int _targetX;
	private int _targetY;
	private int _targetZ;
	private int _originX;
	private int _originY;
	private int _originZ;
	
	@Override
	protected void readImpl() {
		_boatId = readD(); // objectId of boat
		_targetX = readD();
		_targetY = readD();
		_targetZ = readD();
		_originX = readD();
		_originY = readD();
		_originZ = readD();
	}
	
	@Override
	protected void runImpl() {
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null) {
			return;
		}
		
		if ((character().getNpcTalkBlockingTime() > 0) && !activeChar.isGM() && (activeChar.getNotMoveUntil() > System.currentTimeMillis())) {
			activeChar.sendPacket(SystemMessageId.CANNOT_MOVE_WHILE_SPEAKING_TO_AN_NPC);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if ((_targetX == _originX) && (_targetY == _originY) && (_targetZ == _originZ)) {
			activeChar.sendPacket(new StopMoveInVehicle(activeChar, _boatId));
			return;
		}
		
		if (activeChar.isAttackingNow() && (activeChar.getActiveWeaponItem() != null) && (activeChar.getActiveWeaponItem().getItemType() == WeaponType.BOW)) {
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.isSitting() || activeChar.isMovementDisabled()) {
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.hasSummon()) {
			activeChar.sendPacket(SystemMessageId.RELEASE_PET_ON_BOAT);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.isTransformed()) {
			activeChar.sendPacket(SystemMessageId.CANT_POLYMORPH_ON_BOAT);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final L2BoatInstance boat;
		if (activeChar.isInBoat()) {
			boat = activeChar.getBoat();
			if (boat.getObjectId() != _boatId) {
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		} else {
			boat = BoatManager.getInstance().getBoat(_boatId);
			if ((boat == null) || !boat.isInsideRadius(activeChar, 300, true, false)) {
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			activeChar.setVehicle(boat);
		}
		
		final Location pos = new Location(_targetX, _targetY, _targetZ);
		final Location originPos = new Location(_originX, _originY, _originZ);
		activeChar.setInVehiclePosition(pos);
		activeChar.broadcastPacket(new MoveToLocationInVehicle(activeChar, pos, originPos));
	}
	
	@Override
	public String getType() {
		return _C__75_MOVETOLOCATIONINVEHICLE;
	}
}