
package com.l2jserver.datapack.quests.TerritoryWarScripts;

import com.l2jserver.gameserver.network.NpcStringId;

/**
 * For the Sake of the Territory - Dion (718)
 * @author Gigiikun
 */
public final class Q00718_ForTheSakeOfTheTerritoryDion extends TerritoryWarSuperClass {
	public Q00718_ForTheSakeOfTheTerritoryDion() {
		super(718, Q00718_ForTheSakeOfTheTerritoryDion.class.getSimpleName(), "For the Sake of the Territory - Dion");
		CATAPULT_ID = 36500;
		TERRITORY_ID = 82;
		LEADER_IDS = new int[] {
			36514,
			36516,
			36519,
			36592
		};
		GUARD_IDS = new int[] {
			36515,
			36517,
			36518
		};
		npcString = new NpcStringId[] {
			NpcStringId.THE_CATAPULT_OF_DION_HAS_BEEN_DESTROYED
		};
		registerKillIds();
	}
}
