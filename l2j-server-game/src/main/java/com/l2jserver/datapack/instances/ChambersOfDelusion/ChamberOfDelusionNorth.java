
package com.l2jserver.datapack.instances.ChambersOfDelusion;

import com.l2jserver.gameserver.model.Location;
import org.springframework.stereotype.Service;

@Service
public class ChamberOfDelusionNorth extends Chamber {
	// NPC's
	private static final int ENTRANCE_GATEKEEPER = 32661;
	private static final int ROOM_GATEKEEPER_FIRST = 32679;
	private static final int ROOM_GATEKEEPER_LAST = 32683;
	private static final int AENKINEL = 25693;
	private static final int BOX = 18838;
	
	// Misc
	private static final Location[] ENTER_POINTS = new Location[] {
		new Location(-108976, -207772, -6720),
		new Location(-108976, -206972, -6720),
		new Location(-108960, -209164, -6720),
		new Location(-108048, -207340, -6720),
		new Location(-108048, -209020, -6720), // Raid room
	};
	private static final int INSTANCEID = 130; // this is the client number
	private static final String INSTANCE_TEMPLATE = "ChamberOfDelusionNorth.xml";
	
	public ChamberOfDelusionNorth() {
		super(ChamberOfDelusionNorth.class.getSimpleName(), "instances", INSTANCEID, INSTANCE_TEMPLATE, ENTRANCE_GATEKEEPER, ROOM_GATEKEEPER_FIRST, ROOM_GATEKEEPER_LAST, AENKINEL, BOX);
		ROOM_ENTER_POINTS = ENTER_POINTS;
	}
}