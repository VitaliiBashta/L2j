
package com.l2jserver.datapack.quests.TerritoryWarScripts;

import com.l2jserver.gameserver.network.NpcStringId;

/**
 * Deny Blessings (737)
 * @author Gigiikun
 */
public final class Q00737_DenyBlessings extends TerritoryWarSuperClass {
	public Q00737_DenyBlessings() {
		super(737, Q00737_DenyBlessings.class.getSimpleName(), "Deny Blessings");
		CLASS_IDS = new int[] {
			43,
			112,
			30,
			105,
			16,
			97,
			17,
			98,
			52,
			116
		};
		RANDOM_MIN = 3;
		RANDOM_MAX = 8;
		npcString = new NpcStringId[] {
			NpcStringId.YOU_HAVE_DEFEATED_S2_OF_S1_HEALERS_AND_BUFFERS,
			NpcStringId.YOU_WEAKENED_THE_ENEMYS_ATTACK
		};
	}
}
