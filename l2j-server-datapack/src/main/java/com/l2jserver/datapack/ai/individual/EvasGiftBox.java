
package com.l2jserver.datapack.ai.individual;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.ItemHolder;

/**
 * Eva's Gift Box AI.
 * @author St3eT
 */
public final class EvasGiftBox extends AbstractNpcAI {
	// NPC
	private static final int BOX = 32342; // Eva's Gift Box
	// Skill
	private static final int BUFF = 1073; // Kiss of Eva
	// Items
	private static final ItemHolder CORAL = new ItemHolder(9692, 1); // Red Coral
	private static final ItemHolder CRYSTAL = new ItemHolder(9693, 1); // Crystal Fragment
	
	public EvasGiftBox() {
		super(EvasGiftBox.class.getSimpleName(), "ai/individual");
		addKillId(BOX);
		addSpawnId(BOX);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		if (killer.isAffectedBySkill(BUFF)) {
			if (getRandomBoolean()) {
				npc.dropItem(killer, CRYSTAL);
			}
			
			if (getRandom(100) < 33) {
				npc.dropItem(killer, CORAL);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		npc.setIsNoRndWalk(true);
		((L2Attackable) npc).setOnKillDelay(0);
		return super.onSpawn(npc);
	}
}