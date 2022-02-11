
package com.l2jserver.datapack.hellbound.ai;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.util.MinionList;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class Ranku extends AbstractNpcAI {
	// NPCs
	private static final int RANKU = 25542;
	private static final int MINION = 32305;
	private static final int MINION_2 = 25543;
	// Misc
	private static final Set<Integer> MY_TRACKING_SET = ConcurrentHashMap.newKeySet();
	
	public Ranku() {
		super(Ranku.class.getSimpleName(), "hellbound/AI");
		addAttackId(RANKU);
		addKillId(RANKU, MINION);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equalsIgnoreCase("checkup") && (npc.getId() == RANKU) && !npc.isDead()) {
			for (L2MonsterInstance minion : ((L2MonsterInstance) npc).getMinionList().getSpawnedMinions()) {
				if ((minion != null) && !minion.isDead() && MY_TRACKING_SET.contains(minion.getObjectId())) {
					final L2PcInstance[] players = minion.getKnownList().getKnownPlayers().values().toArray(new L2PcInstance[minion.getKnownList().getKnownPlayers().size()]);
					final L2PcInstance killer = players[getRandom(players.length)];
					minion.reduceCurrentHp(minion.getMaxHp() / 100, killer, null);
				}
			}
			startQuestTimer("checkup", 1000, npc, null);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, Skill skill) {
		if (npc.getId() == RANKU) {
			for (L2MonsterInstance minion : ((L2MonsterInstance) npc).getMinionList().getSpawnedMinions()) {
				if ((minion != null) && !minion.isDead() && !MY_TRACKING_SET.contains(minion.getObjectId())) {
					broadcastNpcSay(minion, Say2.NPC_ALL, NpcStringId.DONT_KILL_ME_PLEASE_SOMETHINGS_STRANGLING_ME);
					startQuestTimer("checkup", 1000, npc, null);
					MY_TRACKING_SET.add(minion.getObjectId());
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		if (npc.getId() == MINION) {
			if (MY_TRACKING_SET.contains(npc.getObjectId())) {
				MY_TRACKING_SET.remove(npc.getObjectId());
			}
			
			final L2MonsterInstance master = ((L2MonsterInstance) npc).getLeader();
			if ((master != null) && !master.isDead()) {
				L2MonsterInstance minion2 = MinionList.spawnMinion(master, MINION_2);
				minion2.teleToLocation(npc.getLocation());
			}
		} else if (npc.getId() == RANKU) {
			for (L2MonsterInstance minion : ((L2MonsterInstance) npc).getMinionList().getSpawnedMinions()) {
				if (MY_TRACKING_SET.contains(minion.getObjectId())) {
					MY_TRACKING_SET.remove(minion.getObjectId());
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}