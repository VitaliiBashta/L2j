
package com.l2jserver.datapack.ai.group_template;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;

/**
 * Sandstorms AI.
 * @author Ectis
 */
public class Sandstorms extends AbstractNpcAI {
	// NPCs
	private static final int SANDSTORM = 32350;
	// Skills
	private static final SkillHolder GUST = new SkillHolder(5435);
	
	public Sandstorms() {
		super(Sandstorms.class.getSimpleName(), "ai/group_template");
		addAggroRangeEnterId(SANDSTORM);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon) {
		npc.setTarget(player);
		npc.doCast(GUST);
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
}
