
package com.l2jserver.datapack.ai.group_template;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.ValidateLocation;

/**
 * Lair of Antharas AI.
 * @author St3eT, UnAfraid
 */
public final class LairOfAntharas extends AbstractNpcAI {
	// NPC
	final private static int DRAGON_KNIGHT = 22844;
	final private static int DRAGON_KNIGHT2 = 22845;
	final private static int ELITE_DRAGON_KNIGHT = 22846;
	
	final private static int DRAGON_GUARD = 22852;
	final private static int DRAGON_MAGE = 22853;
	// Misc
	final private static int KNIGHT_CHANCE = 30;
	
	public LairOfAntharas() {
		super(LairOfAntharas.class.getSimpleName(), "ai/group_template");
		addKillId(DRAGON_KNIGHT, DRAGON_KNIGHT2, DRAGON_GUARD, DRAGON_MAGE);
		addSpawnId(DRAGON_KNIGHT, DRAGON_KNIGHT2, DRAGON_GUARD, DRAGON_MAGE);
		addMoveFinishedId(DRAGON_GUARD, DRAGON_MAGE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equals("CHECK_HOME") && (npc != null) && !npc.isDead()) {
			if ((npc.calculateDistance(npc.getSpawn().getLocation(), false, false) > 10) && !npc.isInCombat()) {
				((L2Attackable) npc).returnHome();
			} else if ((npc.getHeading() != npc.getSpawn().getHeading()) && !npc.isInCombat()) {
				npc.setHeading(npc.getSpawn().getHeading());
				npc.broadcastPacket(new ValidateLocation(npc));
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		switch (npc.getId()) {
			case DRAGON_KNIGHT: {
				if (getRandom(100) > KNIGHT_CHANCE) {
					final L2Npc newKnight = addSpawn(DRAGON_KNIGHT2, npc, false, 0, true);
					npc.deleteMe();
					broadcastNpcSay(newKnight, Say2.NPC_SHOUT, NpcStringId.THOSE_WHO_SET_FOOT_IN_THIS_PLACE_SHALL_NOT_LEAVE_ALIVE);
					addAttackDesire(newKnight, killer);
				}
				break;
			}
			case DRAGON_KNIGHT2: {
				if (getRandom(100) > KNIGHT_CHANCE) {
					final L2Npc eliteKnight = addSpawn(ELITE_DRAGON_KNIGHT, npc, false, 0, true);
					npc.deleteMe();
					broadcastNpcSay(eliteKnight, Say2.NPC_SHOUT, NpcStringId.IF_YOU_WISH_TO_SEE_HELL_I_WILL_GRANT_YOU_YOUR_WISH);
					addAttackDesire(eliteKnight, killer);
				}
				break;
			}
			case DRAGON_GUARD:
			case DRAGON_MAGE: {
				cancelQuestTimer("CHECK_HOME", npc, null);
				break;
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		final L2Attackable mob = (L2Attackable) npc;
		mob.setOnKillDelay(0);
		if ((npc.getId() == DRAGON_GUARD) || (npc.getId() == DRAGON_MAGE)) {
			mob.setIsNoRndWalk(true);
			startQuestTimer("CHECK_HOME", 10000, npc, null, true);
		}
		return super.onSpawn(npc);
	}
}