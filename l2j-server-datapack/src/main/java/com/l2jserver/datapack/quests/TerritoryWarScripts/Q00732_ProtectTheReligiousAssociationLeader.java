
package com.l2jserver.datapack.quests.TerritoryWarScripts;

/**
 * Protect the Religious Association Leader (732)
 * @author Gigiikun
 */
public final class Q00732_ProtectTheReligiousAssociationLeader extends TerritoryWarSuperClass {
	public Q00732_ProtectTheReligiousAssociationLeader() {
		super(732, Q00732_ProtectTheReligiousAssociationLeader.class.getSimpleName(), "Protect the Religious Association Leader");
		NPC_IDS = new int[] {
			36510,
			36516,
			36522,
			36528,
			36534,
			36540,
			36546,
			36552,
			36558
		};
		addAttackId(NPC_IDS);
	}
	
	@Override
	public int getTerritoryIdForThisNPCId(int npcId) {
		return 81 + ((npcId - 36510) / 6);
	}
}
