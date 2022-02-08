
package com.l2jserver.datapack.quests.TerritoryWarScripts;

import com.l2jserver.gameserver.network.NpcStringId;

/**
 * For the Sake of the Territory - Rune (724)
 * @author Gigiikun
 */
public final class Q00724_ForTheSakeOfTheTerritoryRune extends TerritoryWarSuperClass {
	public Q00724_ForTheSakeOfTheTerritoryRune() {
		super(724, Q00724_ForTheSakeOfTheTerritoryRune.class.getSimpleName(), "For the Sake of the Territory - Rune");
		CATAPULT_ID = 36506;
		TERRITORY_ID = 88;
		LEADER_IDS = new int[] {
			36550,
			36552,
			36555,
			36598
		};
		GUARD_IDS = new int[] {
			36551,
			36553,
			36554
		};
		npcString = new NpcStringId[] {
			NpcStringId.THE_CATAPULT_OF_RUNE_HAS_BEEN_DESTROYED
		};
		registerKillIds();
	}
}
