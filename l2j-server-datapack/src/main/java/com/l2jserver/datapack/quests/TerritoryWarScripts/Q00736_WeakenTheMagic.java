
package com.l2jserver.datapack.quests.TerritoryWarScripts;

import com.l2jserver.gameserver.network.NpcStringId;

/**
 * Weaken the magic! (736)
 * @author Gigiikun
 */
public final class Q00736_WeakenTheMagic extends TerritoryWarSuperClass {
	public Q00736_WeakenTheMagic() {
		super(736, Q00736_WeakenTheMagic.class.getSimpleName(), "Weaken the magic");
		CLASS_IDS = new int[] {
			40,
			110,
			27,
			103,
			13,
			95,
			12,
			94,
			41,
			111,
			28,
			104,
			14,
			96
		};
		RANDOM_MIN = 10;
		RANDOM_MAX = 15;
		npcString = new NpcStringId[] {
			NpcStringId.YOU_HAVE_DEFEATED_S2_OF_S1_ENEMIES,
			NpcStringId.YOU_WEAKENED_THE_ENEMYS_MAGIC
		};
	}
}
