
package com.l2jserver.datapack.gracia.ai.npc.ZealotOfShilen;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class ZealotOfShilen extends AbstractNpcAI {
	// NPCs
	private static final int ZEALOT = 18782;
	private static final int[] GUARDS = {
		32628,
		32629
	};
	
	public ZealotOfShilen() {
		super(ZealotOfShilen.class.getSimpleName(), "gracia/AI/NPC");
		addSpawnId(ZEALOT);
		addSpawnId(GUARDS);
		addFirstTalkId(GUARDS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (npc == null) {
			return null;
		}
		
		startQuestTimer("WATCHING", 10000, npc, null, true);
		if (event.equalsIgnoreCase("WATCHING") && !npc.isAttackingNow()) {
			for (L2Character character : npc.getKnownList().getKnownCharacters()) {
				if (character.isMonster() && !character.isDead() && !((L2Attackable) character).isDecayed()) {
					npc.setRunning();
					((L2Attackable) npc).addDamageHate(character, 0, 999);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, character, null);
				}
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		return (npc.isAttackingNow()) ? "32628-01.html" : npc.getId() + ".html";
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		if (npc.getId() == ZEALOT) {
			npc.setIsNoRndWalk(true);
		} else {
			npc.setIsInvul(true);
			((L2Attackable) npc).setCanReturnToSpawnPoint(false);
			startQuestTimer("WATCHING", 10000, npc, null, true);
		}
		return super.onSpawn(npc);
	}
}
