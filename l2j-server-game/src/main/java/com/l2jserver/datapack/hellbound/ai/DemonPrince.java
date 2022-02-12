
package com.l2jserver.datapack.hellbound.ai;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DemonPrince extends AbstractNpcAI {
	// NPCs
	private static final int DEMON_PRINCE = 25540;
	private static final int FIEND = 25541;
	// Skills
	private static final SkillHolder UD = new SkillHolder(5044, 2);
	private static final SkillHolder[] AOE = {
		new SkillHolder(5376, 4),
		new SkillHolder(5376, 5),
		new SkillHolder(5376, 6),
	};
	
	private static final Map<Integer, Boolean> ATTACK_STATE = new ConcurrentHashMap<>();
	
	public DemonPrince() {
		super(DemonPrince.class.getSimpleName(), "hellbound/AI");
		addAttackId(DEMON_PRINCE);
		addKillId(DEMON_PRINCE);
		addSpawnId(FIEND);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equalsIgnoreCase("cast") && (npc != null) && (npc.getId() == FIEND) && !npc.isDead()) {
			npc.doCast(AOE[getRandom(AOE.length)]);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, Skill skill) {
		if (!npc.isDead()) {
			if (!ATTACK_STATE.containsKey(npc.getObjectId()) && (npc.getCurrentHp() < (npc.getMaxHp() * 0.5))) {
				npc.doCast(UD);
				spawnMinions(npc);
				ATTACK_STATE.put(npc.getObjectId(), false);
			} else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.1)) && ATTACK_STATE.containsKey(npc.getObjectId()) && (ATTACK_STATE.get(npc.getObjectId()) == false)) {
				npc.doCast(UD);
				spawnMinions(npc);
				ATTACK_STATE.put(npc.getObjectId(), true);
			}
			
			if (getRandom(1000) < 10) {
				spawnMinions(npc);
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		ATTACK_STATE.remove(npc.getObjectId());
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		if (npc.getId() == FIEND) {
			startQuestTimer("cast", 15000, npc, null);
		}
		return super.onSpawn(npc);
	}
	
	private void spawnMinions(L2Npc master) {
		if ((master != null) && !master.isDead()) {
			final int instanceId = master.getInstanceId();
			final int x = master.getX();
			final int y = master.getY();
			final int z = master.getZ();
			addSpawn(FIEND, x + 200, y, z, 0, false, 0, false, instanceId);
			addSpawn(FIEND, x - 200, y, z, 0, false, 0, false, instanceId);
			addSpawn(FIEND, x - 100, y - 140, z, 0, false, 0, false, instanceId);
			addSpawn(FIEND, x - 100, y + 140, z, 0, false, 0, false, instanceId);
			addSpawn(FIEND, x + 100, y - 140, z, 0, false, 0, false, instanceId);
			addSpawn(FIEND, x + 100, y + 140, z, 0, false, 0, false, instanceId);
		}
	}
}