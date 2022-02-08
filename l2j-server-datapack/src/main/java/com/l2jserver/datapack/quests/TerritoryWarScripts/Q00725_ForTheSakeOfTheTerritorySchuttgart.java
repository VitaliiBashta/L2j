
package com.l2jserver.datapack.quests.TerritoryWarScripts;

import com.l2jserver.gameserver.network.NpcStringId;

/**
 * For the Sake of the Territory - Schuttgart (725)
 * @author Gigiikun
 */
public final class Q00725_ForTheSakeOfTheTerritorySchuttgart extends TerritoryWarSuperClass {
	public Q00725_ForTheSakeOfTheTerritorySchuttgart() {
		super(725, Q00725_ForTheSakeOfTheTerritorySchuttgart.class.getSimpleName(), "For the Sake of the Territory - Schuttgart");
		CATAPULT_ID = 36507;
		TERRITORY_ID = 89;
		LEADER_IDS = new int[] {
			36556,
			36558,
			36561,
			36599
		};
		GUARD_IDS = new int[] {
			36557,
			36559,
			36560
		};
		npcString = new NpcStringId[] {
			NpcStringId.THE_CATAPULT_OF_SCHUTTGART_HAS_BEEN_DESTROYED
		};
		registerKillIds();
	}
}
