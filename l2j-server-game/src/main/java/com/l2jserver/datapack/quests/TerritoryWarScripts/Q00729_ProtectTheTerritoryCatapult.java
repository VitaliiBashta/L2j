
package com.l2jserver.datapack.quests.TerritoryWarScripts;

/**
 * Protect the Territory Catapult! (729)
 * @author Gigiikun
 */
public final class Q00729_ProtectTheTerritoryCatapult extends TerritoryWarSuperClass {
	public Q00729_ProtectTheTerritoryCatapult() {
		super(729, Q00729_ProtectTheTerritoryCatapult.class.getSimpleName(), "Protect the Territory Catapult");
		NPC_IDS = new int[] {
			36499,
			36500,
			36501,
			36502,
			36503,
			36504,
			36505,
			36506,
			36507
		};
		addAttackId(NPC_IDS);
	}
	
	@Override
	public int getTerritoryIdForThisNPCId(int npcId) {
		return npcId - 36418;
	}
}
