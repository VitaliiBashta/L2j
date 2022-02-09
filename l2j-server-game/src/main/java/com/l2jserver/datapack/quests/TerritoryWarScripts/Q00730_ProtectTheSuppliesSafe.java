
package com.l2jserver.datapack.quests.TerritoryWarScripts;

/**
 * Protect the Supplies Safe (730)
 * @author Gigiikun
 */
public final class Q00730_ProtectTheSuppliesSafe extends TerritoryWarSuperClass {
	public Q00730_ProtectTheSuppliesSafe() {
		super(730, Q00730_ProtectTheSuppliesSafe.class.getSimpleName(), "Protect the Supplies Safe");
		NPC_IDS = new int[] {
			36591,
			36592,
			36593,
			36594,
			36595,
			36596,
			36597,
			36598,
			36599
		};
		addAttackId(NPC_IDS);
	}
	
	@Override
	public int getTerritoryIdForThisNPCId(int npcId) {
		return npcId - 36510;
	}
}
