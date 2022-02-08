
package com.l2jserver.datapack.quests.TerritoryWarScripts;

import com.l2jserver.gameserver.network.NpcStringId;

/**
 * For the Sake of the Territory - Gludio (717)
 * @author Gigiikun
 */
public final class Q00717_ForTheSakeOfTheTerritoryGludio extends TerritoryWarSuperClass {
	public Q00717_ForTheSakeOfTheTerritoryGludio() {
		super(717, Q00717_ForTheSakeOfTheTerritoryGludio.class.getSimpleName(), "For the Sake of the Territory - Gludio");
		CATAPULT_ID = 36499;
		TERRITORY_ID = 81;
		LEADER_IDS = new int[] {
			36508,
			36510,
			36513,
			36591
		};
		GUARD_IDS = new int[] {
			36509,
			36511,
			36512
		};
		npcString = new NpcStringId[] {
			NpcStringId.THE_CATAPULT_OF_GLUDIO_HAS_BEEN_DESTROYED
		};
		registerKillIds();
	}
}
