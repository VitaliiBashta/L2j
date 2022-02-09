
package com.l2jserver.datapack.quests.TerritoryWarScripts;

import com.l2jserver.gameserver.network.NpcStringId;

/**
 * For the Sake of the Territory - Giran (719)
 * @author Gigiikun
 */
public final class Q00719_ForTheSakeOfTheTerritoryGiran extends TerritoryWarSuperClass {
	public Q00719_ForTheSakeOfTheTerritoryGiran() {
		super(719, Q00719_ForTheSakeOfTheTerritoryGiran.class.getSimpleName(), "For the Sake of the Territory - Giran");
		CATAPULT_ID = 36501;
		TERRITORY_ID = 83;
		LEADER_IDS = new int[] {
			36520,
			36522,
			36525,
			36593
		};
		GUARD_IDS = new int[] {
			36521,
			36523,
			36524
		};
		npcString = new NpcStringId[] {
			NpcStringId.THE_CATAPULT_OF_GIRAN_HAS_BEEN_DESTROYED
		};
		registerKillIds();
	}
}
