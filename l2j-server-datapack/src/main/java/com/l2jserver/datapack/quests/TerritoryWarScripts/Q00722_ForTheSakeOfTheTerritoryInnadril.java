
package com.l2jserver.datapack.quests.TerritoryWarScripts;

import com.l2jserver.gameserver.network.NpcStringId;

/**
 * For the Sake of the Territory - Innadril (722)
 * @author Gigiikun
 */
public final class Q00722_ForTheSakeOfTheTerritoryInnadril extends TerritoryWarSuperClass {
	public Q00722_ForTheSakeOfTheTerritoryInnadril() {
		super(722, Q00722_ForTheSakeOfTheTerritoryInnadril.class.getSimpleName(), "For the Sake of the Territory - Innadril");
		CATAPULT_ID = 36504;
		TERRITORY_ID = 86;
		LEADER_IDS = new int[] {
			36538,
			36540,
			36543,
			36596
		};
		GUARD_IDS = new int[] {
			36539,
			36541,
			36542
		};
		npcString = new NpcStringId[] {
			NpcStringId.THE_CATAPULT_OF_INNADRIL_HAS_BEEN_DESTROYED
		};
		registerKillIds();
	}
}
