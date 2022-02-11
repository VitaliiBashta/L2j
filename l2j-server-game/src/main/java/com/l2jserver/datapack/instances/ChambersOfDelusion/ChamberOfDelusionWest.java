
package com.l2jserver.datapack.instances.ChambersOfDelusion;

import com.l2jserver.gameserver.model.Location;
import org.springframework.stereotype.Service;

@Service
public class ChamberOfDelusionWest extends Chamber {
	// NPC's
	private static final int ENTRANCE_GATEKEEPER = 32659;
	private static final int ROOM_GATEKEEPER_FIRST = 32669;
	private static final int ROOM_GATEKEEPER_LAST = 32673;
	private static final int AENKINEL = 25691;
	private static final int BOX = 18838;
	
	// Misc
	private static final Location[] ENTER_POINTS = new Location[] {
		new Location(-108960, -218892, -6720),
		new Location(-108976, -218028, -6720),
		new Location(-108960, -220204, -6720),
		new Location(-108032, -218428, -6720),
		new Location(-108032, -220140, -6720), // Raid room
	};
	private static final int INSTANCEID = 128; // this is the client number
	private static final String INSTANCE_TEMPLATE = "ChamberOfDelusionWest.xml";
	
	public ChamberOfDelusionWest() {
		super(ChamberOfDelusionWest.class.getSimpleName(), "instances", INSTANCEID, INSTANCE_TEMPLATE, ENTRANCE_GATEKEEPER, ROOM_GATEKEEPER_FIRST, ROOM_GATEKEEPER_LAST, AENKINEL, BOX);
		ROOM_ENTER_POINTS = ENTER_POINTS;
	}
}