
package com.l2jserver.datapack.instances.JiniaGuildHideout3;

import com.l2jserver.datapack.instances.AbstractInstance;
import com.l2jserver.datapack.quests.Q10286_ReunionWithSirra.Q10286_ReunionWithSirra;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.instancezone.InstanceWorld;
import com.l2jserver.gameserver.model.quest.QuestState;
import org.springframework.stereotype.Service;

@Service
public final class JiniaGuildHideout3 extends AbstractInstance {
	protected class JGH3World extends InstanceWorld {
		
	}
	
	// NPC
	private static final int RAFFORTY = 32020;
	// Location
	private static final Location START_LOC = new Location(-23530, -8963, -5413, 0, 0);
	// Misc
	private static final int TEMPLATE_ID = 145;
	
	public JiniaGuildHideout3() {
		super(JiniaGuildHideout3.class.getSimpleName());
		addStartNpc(RAFFORTY);
		addTalkId(RAFFORTY);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker) {
		final QuestState qs = talker.getQuestState(Q10286_ReunionWithSirra.class.getSimpleName());
		if ((qs != null) && qs.isMemoState(1)) {
			enterInstance(talker, new JGH3World(), "JiniaGuildHideout3.xml", TEMPLATE_ID);
			qs.setCond(2, true);
		}
		return super.onTalk(npc, talker);
	}
	
	@Override
	public void onEnterInstance(L2PcInstance player, InstanceWorld world, boolean firstEntrance) {
		if (firstEntrance) {
			world.addAllowed(player.getObjectId());
		}
		teleportPlayer(player, START_LOC, world.getInstanceId(), false);
	}
}
