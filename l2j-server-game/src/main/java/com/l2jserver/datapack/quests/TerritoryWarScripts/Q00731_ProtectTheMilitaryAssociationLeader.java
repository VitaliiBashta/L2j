
package com.l2jserver.datapack.quests.TerritoryWarScripts;

/**
 * Protect the Military Association Leader (731)
 * @author Gigiikun
 */
public final class Q00731_ProtectTheMilitaryAssociationLeader extends TerritoryWarSuperClass {
	public Q00731_ProtectTheMilitaryAssociationLeader() {
		super(731, Q00731_ProtectTheMilitaryAssociationLeader.class.getSimpleName(), "Protect the Military Association Leader");
		NPC_IDS = new int[] {
			36508,
			36514,
			36520,
			36526,
			36532,
			36538,
			36544,
			36550,
			36556
		};
		addAttackId(NPC_IDS);
	}
	
	@Override
	public int getTerritoryIdForThisNPCId(int npcId) {
		return 81 + ((npcId - 36508) / 6);
	}
}
