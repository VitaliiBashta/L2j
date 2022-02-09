
package com.l2jserver.datapack.quests.TerritoryWarScripts;

import com.l2jserver.gameserver.network.NpcStringId;

/**
 * Pierce through a Shield! (734)
 * @author Gigiikun
 */
public final class Q00734_PierceThroughAShield extends TerritoryWarSuperClass {
	public Q00734_PierceThroughAShield() {
		super(734, Q00734_PierceThroughAShield.class.getSimpleName(), "Pierce through a Shield");
		CLASS_IDS = new int[] {
			6,
			91,
			5,
			90,
			20,
			99,
			33,
			106
		};
		RANDOM_MIN = 10;
		RANDOM_MAX = 15;
		npcString = new NpcStringId[] {
			NpcStringId.YOU_HAVE_DEFEATED_S2_OF_S1_KNIGHTS,
			NpcStringId.YOU_WEAKENED_THE_ENEMYS_DEFENSE
		};
	}
}
