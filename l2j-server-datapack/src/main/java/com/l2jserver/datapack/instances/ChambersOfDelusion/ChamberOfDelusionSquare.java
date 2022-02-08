
package com.l2jserver.datapack.instances.ChambersOfDelusion;

import com.l2jserver.gameserver.model.Location;

/**
 * Chamber of Delusion Square.
 * @author GKR
 */
public final class ChamberOfDelusionSquare extends Chamber {
	// NPC's
	private static final int ENTRANCE_GATEKEEPER = 32662;
	private static final int ROOM_GATEKEEPER_FIRST = 32684;
	private static final int ROOM_GATEKEEPER_LAST = 32692;
	private static final int AENKINEL = 25694;
	private static final int BOX = 18820;
	
	// Misc
	private static final Location[] ENTER_POINTS = new Location[] {
		new Location(-122368, -153388, -6688),
		new Location(-122368, -152524, -6688),
		new Location(-120480, -155116, -6688),
		new Location(-120480, -154236, -6688),
		new Location(-121440, -151212, -6688),
		new Location(-120464, -152908, -6688),
		new Location(-122368, -154700, -6688),
		new Location(-121440, -152908, -6688),
		new Location(-121440, -154572, -6688), // Raid room
	};
	private static final int INSTANCEID = 131;
	private static final String INSTANCE_TEMPLATE = "ChamberOfDelusionSquare.xml";
	
	public ChamberOfDelusionSquare() {
		super(ChamberOfDelusionSquare.class.getSimpleName(), "instances", INSTANCEID, INSTANCE_TEMPLATE, ENTRANCE_GATEKEEPER, ROOM_GATEKEEPER_FIRST, ROOM_GATEKEEPER_LAST, AENKINEL, BOX);
		ROOM_ENTER_POINTS = ENTER_POINTS;
	}
}