
package com.l2jserver.datapack.quests.TerritoryWarScripts;

import com.l2jserver.gameserver.network.NpcStringId;

/**
 * Destroy Key Targets (738)
 * @author Gigiikun
 */
public final class Q00738_DestroyKeyTargets extends TerritoryWarSuperClass {
	public Q00738_DestroyKeyTargets() {
		super(738, Q00738_DestroyKeyTargets.class.getSimpleName(), "Destroy Key Targets");
		CLASS_IDS = new int[] {
			51,
			115,
			57,
			118
		};
		RANDOM_MIN = 3;
		RANDOM_MAX = 8;
		npcString = new NpcStringId[] {
			NpcStringId.YOU_HAVE_DEFEATED_S2_OF_S1_WARSMITHS_AND_OVERLORDS,
			NpcStringId.YOU_DESTROYED_THE_ENEMYS_PROFESSIONALS
		};
	}
}
