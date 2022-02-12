
package com.l2jserver.datapack.hellbound.ai;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class NaiaLock extends AbstractNpcAI {
	// NPCs
	private static final int LOCK = 18491;
	
	public NaiaLock() {
		super(NaiaLock.class.getSimpleName(), "hellbound/AI");
		addKillId(LOCK);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		((L2MonsterInstance) npc).getMinionList().onMasterDie(true);
		return super.onKill(npc, killer, isSummon);
	}
}