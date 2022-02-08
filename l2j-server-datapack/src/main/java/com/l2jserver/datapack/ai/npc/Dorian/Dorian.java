
package com.l2jserver.datapack.ai.npc.Dorian;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.quests.Q00024_InhabitantsOfTheForestOfTheDead.Q00024_InhabitantsOfTheForestOfTheDead;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/**
 * Dorian (Raid Fighter) - Quest AI
 * @author malyelfik
 */
public final class Dorian extends AbstractNpcAI {
	// NPC
	private static final int DORIAN = 25332;
	// Items
	private static final int SILVER_CROSS = 7153;
	private static final int BROKEN_SILVER_CROSS = 7154;
	
	public Dorian() {
		super(Dorian.class.getSimpleName(), "ai/npc");
		addSeeCreatureId(DORIAN);
	}
	
	@Override
	public String onSeeCreature(L2Npc npc, L2Character creature, boolean isSummon) {
		if (creature.isPlayer()) {
			final L2PcInstance pl = creature.getActingPlayer();
			final QuestState qs = pl.getQuestState(Q00024_InhabitantsOfTheForestOfTheDead.class.getSimpleName());
			if ((qs != null) && qs.isCond(3)) {
				takeItems(pl, SILVER_CROSS, -1);
				giveItems(pl, BROKEN_SILVER_CROSS, 1);
				qs.setCond(4, true);
				broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.THAT_SIGN);
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
}