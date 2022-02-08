
package com.l2jserver.datapack.instances.SecretAreaInTheKeucereusFortress1;

import com.l2jserver.datapack.instances.AbstractInstance;
import com.l2jserver.datapack.quests.Q10270_BirthOfTheSeed.Q10270_BirthOfTheSeed;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.instancezone.InstanceWorld;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Secret Area in the Keucereus Fortress instance zone.
 * @author Adry_85
 * @since 2.6.0.0
 */
public final class SecretAreaInTheKeucereusFortress1 extends AbstractInstance {
	protected class SAKF1World extends InstanceWorld {
		
	}
	
	// NPC
	private static final int GINBY = 32566;
	// Location
	private static final Location START_LOC = new Location(-23530, -8963, -5413);
	// Misc
	private static final int TEMPLATE_ID = 117;
	
	public SecretAreaInTheKeucereusFortress1() {
		super(SecretAreaInTheKeucereusFortress1.class.getSimpleName());
		addStartNpc(GINBY);
		addTalkId(GINBY);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = player.getQuestState(Q10270_BirthOfTheSeed.class.getSimpleName());
		if ((st != null) && (st.getMemoState() >= 5) && (st.getMemoState() < 20)) {
			enterInstance(player, new SAKF1World(), "SecretAreaInTheKeucereusFortress.xml", TEMPLATE_ID);
			if (st.isMemoState(5)) {
				st.setMemoState(10);
			}
			return "32566-01.html";
		}
		return super.onTalk(npc, player);
	}
	
	@Override
	public void onEnterInstance(L2PcInstance player, InstanceWorld world, boolean firstEntrance) {
		if (firstEntrance) {
			world.addAllowed(player.getObjectId());
		}
		teleportPlayer(player, START_LOC, world.getInstanceId(), false);
	}
}
