
package com.l2jserver.datapack.quests.TerritoryWarScripts;

import com.l2jserver.gameserver.network.NpcStringId;

/**
 * For the Sake of the Territory - Goddard (723)
 * @author Gigiikun
 */
public final class Q00723_ForTheSakeOfTheTerritoryGoddard extends TerritoryWarSuperClass {
	public Q00723_ForTheSakeOfTheTerritoryGoddard() {
		super(723, Q00723_ForTheSakeOfTheTerritoryGoddard.class.getSimpleName(), "For the Sake of the Territory - Goddard");
		CATAPULT_ID = 36505;
		TERRITORY_ID = 87;
		LEADER_IDS = new int[] {
			36544,
			36546,
			36549,
			36597
		};
		GUARD_IDS = new int[] {
			36545,
			36547,
			36548
		};
		npcString = new NpcStringId[] {
			NpcStringId.THE_CATAPULT_OF_GODDARD_HAS_BEEN_DESTROYED
		};
		registerKillIds();
	}
}
