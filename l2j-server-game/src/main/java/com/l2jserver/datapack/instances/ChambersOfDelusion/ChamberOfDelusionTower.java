
package com.l2jserver.datapack.instances.ChambersOfDelusion;

import com.l2jserver.gameserver.model.Location;
import org.springframework.stereotype.Service;

@Service
public class ChamberOfDelusionTower extends Chamber {
	// NPC's
	private static final int ENTRANCE_GATEKEEPER = 32663;
	private static final int ROOM_GATEKEEPER_FIRST = 32693;
	private static final int ROOM_GATEKEEPER_LAST = 32701;
	private static final int AENKINEL = 25695;
	private static final int BOX = 18823;
	
	// Misc
	private static final Location[] ENTER_POINTS = new Location[] {
		new Location(-108976, -153372, -6688),
		new Location(-108960, -152524, -6688),
		new Location(-107088, -155052, -6688),
		new Location(-107104, -154236, -6688),
		new Location(-108048, -151244, -6688),
		new Location(-107088, -152956, -6688),
		new Location(-108992, -154604, -6688),
		new Location(-108032, -152892, -6688),
		new Location(-108048, -154572, -6688), // Raid room
	};
	private static final int INSTANCEID = 132; // this is the client number
	private static final String INSTANCE_TEMPLATE = "ChamberOfDelusionTower.xml";
	
	public ChamberOfDelusionTower() {
		super(ChamberOfDelusionTower.class.getSimpleName(), "instances", INSTANCEID, INSTANCE_TEMPLATE, ENTRANCE_GATEKEEPER, ROOM_GATEKEEPER_FIRST, ROOM_GATEKEEPER_LAST, AENKINEL, BOX);
		ROOM_ENTER_POINTS = ENTER_POINTS;
	}
}