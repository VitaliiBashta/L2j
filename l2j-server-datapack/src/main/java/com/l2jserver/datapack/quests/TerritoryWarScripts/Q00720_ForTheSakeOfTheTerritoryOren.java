
package com.l2jserver.datapack.quests.TerritoryWarScripts;

import com.l2jserver.gameserver.network.NpcStringId;

/**
 * For the Sake of the Territory - Oren (720)
 * @author Gigiikun
 */
public final class Q00720_ForTheSakeOfTheTerritoryOren extends TerritoryWarSuperClass {
	public Q00720_ForTheSakeOfTheTerritoryOren() {
		super(720, Q00720_ForTheSakeOfTheTerritoryOren.class.getSimpleName(), "For the Sake of the Territory - Oren");
		CATAPULT_ID = 36502;
		TERRITORY_ID = 84;
		LEADER_IDS = new int[] {
			36526,
			36528,
			36531,
			36594
		};
		GUARD_IDS = new int[] {
			36527,
			36529,
			36530
		};
		npcString = new NpcStringId[] {
			NpcStringId.THE_CATAPULT_OF_OREN_HAS_BEEN_DESTROYED
		};
		registerKillIds();
	}
}
