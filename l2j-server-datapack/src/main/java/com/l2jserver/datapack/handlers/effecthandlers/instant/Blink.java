
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.serverpackets.FlyToLocation;
import com.l2jserver.gameserver.network.serverpackets.FlyToLocation.FlyType;
import com.l2jserver.gameserver.network.serverpackets.ValidateLocation;
import com.l2jserver.gameserver.util.Util;

/**
 * Blink effect implementation.<br>
 * This class handles warp effects, disappear and quickly turn up in a near location.<br>
 * If geodata enabled and an object is between initial and final point, flight is stopped just before colliding with object.<br>
 * Flight course and radius are set as effect properties (flyCourse and flyRadius):
 * <ul>
 * <li>Fly Radius means the distance between starting point and final point, it must be an integer.</li>
 * <li>Fly Course means the movement direction: imagine a compass above player's head, making north player's heading. So if fly course is 180, player will go backwards (good for blink, e.g.).</li>
 * </ul>
 * By the way, if flyCourse = 360 or 0, player will be moved in in front of him. <br>
 * @author DrHouse
 */
public final class Blink extends AbstractEffect {
	private final int _flyCourse;
	private final int _flyRadius;
	
	public Blink(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_flyCourse = params.getInt("flyCourse", 0);
		_flyRadius = params.getInt("flyRadius", 0);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		final L2Character effected = info.getEffected();
		final double angle = Util.convertHeadingToDegree(effected.getHeading());
		final double radian = Math.toRadians(angle);
		final double course = Math.toRadians(_flyCourse);
		final int x1 = (int) (Math.cos(Math.PI + radian + course) * _flyRadius);
		final int y1 = (int) (Math.sin(Math.PI + radian + course) * _flyRadius);
		
		int x = effected.getX() + x1;
		int y = effected.getY() + y1;
		int z = effected.getZ();
		
		final Location destination = GeoData.getInstance().moveCheck(effected.getX(), effected.getY(), effected.getZ(), x, y, z, effected.getInstanceId());
		
		effected.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		effected.broadcastPacket(new FlyToLocation(effected, destination, FlyType.DUMMY));
		effected.abortAttack();
		effected.abortCast();
		effected.setXYZ(destination);
		effected.broadcastPacket(new ValidateLocation(effected));
	}
}
