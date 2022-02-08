
package com.l2jserver.datapack.quests.TerritoryWarScripts;

/**
 * Protect the Economic Association Leader (733)
 * @author Gigiikun
 */
public final class Q00733_ProtectTheEconomicAssociationLeader extends TerritoryWarSuperClass {
	public Q00733_ProtectTheEconomicAssociationLeader() {
		super(733, Q00733_ProtectTheEconomicAssociationLeader.class.getSimpleName(), "Protect the Economic Association Leader");
		NPC_IDS = new int[] {
			36513,
			36519,
			36525,
			36531,
			36537,
			36543,
			36549,
			36555,
			36561
		};
		addAttackId(NPC_IDS);
	}
	
	@Override
	public int getTerritoryIdForThisNPCId(int npcId) {
		return 81 + ((npcId - 36513) / 6);
	}
}
