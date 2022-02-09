
package com.l2jserver.datapack.instances.ChambersOfDelusion;

import com.l2jserver.gameserver.model.Location;

/**
 * Chamber of Delusion South.
 * @author GKR
 */
public final class ChamberOfDelusionSouth extends Chamber {
	// NPC's
	private static final int ENTRANCE_GATEKEEPER = 32660;
	private static final int ROOM_GATEKEEPER_FIRST = 32674;
	private static final int ROOM_GATEKEEPER_LAST = 32678;
	private static final int AENKINEL = 25692;
	private static final int BOX = 18838;
	
	// Misc
	private static final Location[] ENTER_POINTS = new Location[] {
		new Location(-122368, -207820, -6720),
		new Location(-122368, -206940, -6720),
		new Location(-122368, -209116, -6720),
		new Location(-121456, -207356, -6720),
		new Location(-121440, -209004, -6720), // Raid room
	};
	private static final int INSTANCEID = 129; // this is the client number
	private static final String INSTANCE_TEMPLATE = "ChamberOfDelusionSouth.xml";
	
	public ChamberOfDelusionSouth() {
		super(ChamberOfDelusionSouth.class.getSimpleName(), "instances", INSTANCEID, INSTANCE_TEMPLATE, ENTRANCE_GATEKEEPER, ROOM_GATEKEEPER_FIRST, ROOM_GATEKEEPER_LAST, AENKINEL, BOX);
		ROOM_ENTER_POINTS = ENTER_POINTS;
	}
}