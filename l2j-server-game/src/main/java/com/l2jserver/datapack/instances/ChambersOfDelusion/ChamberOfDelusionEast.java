
package com.l2jserver.datapack.instances.ChambersOfDelusion;

import com.l2jserver.gameserver.model.Location;
import org.springframework.stereotype.Service;

@Service
public class ChamberOfDelusionEast extends Chamber {
	// NPCs
	private static final int ENTRANCE_GATEKEEPER = 32658;
	private static final int ROOM_GATEKEEPER_FIRST = 32664;
	private static final int ROOM_GATEKEEPER_LAST = 32668;
	private static final int AENKINEL = 25690;
	private static final int BOX = 18838;
	
	// Misc
	private static final Location[] ENTER_POINTS = new Location[] {
		new Location(-122368, -218972, -6720),
		new Location(-122352, -218044, -6720),
		new Location(-122368, -220220, -6720),
		new Location(-121440, -218444, -6720),
		new Location(-121424, -220124, -6720), // Raid room
	};
	private static final int INSTANCEID = 127;
	private static final String INSTANCE_TEMPLATE = "ChamberOfDelusionEast.xml";
	
	public ChamberOfDelusionEast() {
		super(ChamberOfDelusionEast.class.getSimpleName(), "instances", INSTANCEID, INSTANCE_TEMPLATE, ENTRANCE_GATEKEEPER, ROOM_GATEKEEPER_FIRST, ROOM_GATEKEEPER_LAST, AENKINEL, BOX);
		ROOM_ENTER_POINTS = ENTER_POINTS;
	}
}