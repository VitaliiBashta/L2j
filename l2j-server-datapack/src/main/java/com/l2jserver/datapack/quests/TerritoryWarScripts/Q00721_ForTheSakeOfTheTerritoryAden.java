
package com.l2jserver.datapack.quests.TerritoryWarScripts;

import com.l2jserver.gameserver.network.NpcStringId;

/**
 * For the Sake of the Territory - Aden (721)
 * @author Gigiikun
 */
public final class Q00721_ForTheSakeOfTheTerritoryAden extends TerritoryWarSuperClass {
	public Q00721_ForTheSakeOfTheTerritoryAden() {
		super(721, Q00721_ForTheSakeOfTheTerritoryAden.class.getSimpleName(), "For the Sake of the Territory - Aden");
		CATAPULT_ID = 36503;
		TERRITORY_ID = 85;
		LEADER_IDS = new int[] {
			36532,
			36534,
			36537,
			36595
		};
		GUARD_IDS = new int[] {
			36533,
			36535,
			36536
		};
		npcString = new NpcStringId[] {
			NpcStringId.THE_CATAPULT_OF_ADEN_HAS_BEEN_DESTROYED
		};
		registerKillIds();
	}
}
