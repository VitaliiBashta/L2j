
package com.l2jserver.datapack.ai.npc.NpcBuffers;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author UnAfraid
 */
public final class NpcBuffers extends AbstractNpcAI {
	private final NpcBuffersData _npcBuffers = new NpcBuffersData();
	
	public NpcBuffers() {
		super(NpcBuffers.class.getSimpleName(), "ai/npc");
		
		for (int npcId : _npcBuffers.getNpcBufferIds()) {
			// TODO: Cleanup once npc rework is finished and default html is configurable.
			addFirstTalkId(npcId);
			addSpawnId(npcId);
		}
	}
	
	// TODO: Cleanup once npc rework is finished and default html is configurable.
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		final NpcBufferData data = _npcBuffers.getNpcBuffer(npc.getId());
		for (NpcBufferSkillData skill : data.getSkills()) {
			ThreadPoolManager.getInstance().scheduleAi(new NpcBufferAI(npc, skill), skill.getInitialDelay());
		}
		return super.onSpawn(npc);
	}
}
