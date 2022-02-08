
package com.l2jserver.datapack.ai.individual;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;

/**
 * Grove Robber's AI.<br>
 * <ul>
 * <li>Grove Robber Summoner</li>
 * <li>Grove Robber Megician</li>
 * </ul>
 * @author Zealar
 */
public final class GraveRobbers extends AbstractNpcAI {
	private static final int GRAVE_ROBBER_SUMMONER = 22678;
	private static final int GRAVE_ROBBER_MEGICIAN = 22679;
	
	public GraveRobbers() {
		super(GraveRobbers.class.getSimpleName(), "ai/individual");
		addSpawnId(GRAVE_ROBBER_SUMMONER, GRAVE_ROBBER_MEGICIAN);
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		spawnMinions(npc, "Privates" + getRandom(1, 2));
		return super.onSpawn(npc);
	}
}
