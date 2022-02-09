
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.serverpackets.FlyToLocation;
import com.l2jserver.gameserver.network.serverpackets.FlyToLocation.FlyType;
import com.l2jserver.gameserver.network.serverpackets.ValidateLocation;

/**
 * Fly Self effect implementation.
 */
public final class FlySelf extends AbstractEffect {
	private final int _flyRadius;
	
	public FlySelf(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_flyRadius = params.getInt("flyRadius", 0);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffected().isMovementDisabled()) {
			return;
		}
		
		L2Character target = info.getEffected();
		L2Character activeChar = info.getEffector();
		
		// Get current position of the L2Character
		final int curX = activeChar.getX();
		final int curY = activeChar.getY();
		final int curZ = activeChar.getZ();
		
		// Calculate distance (dx,dy) between current position and destination
		double dx = target.getX() - curX;
		double dy = target.getY() - curY;
		double dz = target.getZ() - curZ;
		double distance = Math.hypot(dx, dy);
		if (distance > 2000) {
			_log.info("EffectEnemyCharge was going to use invalid coordinates for characters, getEffector: " + curX + "," + curY + " and getEffected: " + target.getX() + "," + target.getY());
			return;
		}
		
		int offset = Math.max((int) distance - _flyRadius, 30);
		
		// approximation for moving closer when z coordinates are different
		// TODO: handle Z axis movement better
		offset -= Math.abs(dz);
		if (offset < 5) {
			offset = 5;
		}
		
		// If no distance
		if ((distance < 1) || ((distance - offset) <= 0)) {
			return;
		}
		
		// Calculate movement angles needed
		double sin = dy / distance;
		double cos = dx / distance;
		
		// Calculate the new destination with offset included
		int x = curX + (int) ((distance - offset) * cos);
		int y = curY + (int) ((distance - offset) * sin);
		int z = target.getZ();
		
		final Location destination = GeoData.getInstance().moveCheck(curX, curY, curZ, x, y, z, activeChar.getInstanceId());
		
		activeChar.broadcastPacket(new FlyToLocation(activeChar, destination, FlyType.CHARGE));
		
		// maybe is need force set X,Y,Z
		activeChar.setXYZ(destination);
		activeChar.broadcastPacket(new ValidateLocation(activeChar));
	}
}
