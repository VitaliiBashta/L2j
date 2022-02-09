
package com.l2jserver.datapack.ai.npc.Sirra;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.instancezone.InstanceWorld;

/**
 * Sirra AI.
 * @author St3eT
 */
public final class Sirra extends AbstractNpcAI {
	// NPC
	private static final int SIRRA = 32762;
	// Misc
	private static final int FREYA_INSTID = 139;
	private static final int FREYA_HARD_INSTID = 144;
	
	public Sirra() {
		super(Sirra.class.getSimpleName(), "ai/npc");
		addFirstTalkId(SIRRA);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		final InstanceWorld world = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		
		if ((world != null) && (world.getTemplateId() == FREYA_INSTID)) {
			return (world.isStatus(0)) ? "32762-easy.html" : "32762-easyfight.html";
		} else if ((world != null) && (world.getTemplateId() == FREYA_HARD_INSTID)) {
			return (world.isStatus(0)) ? "32762-hard.html" : "32762-hardfight.html";
		}
		return "32762.html";
	}
}