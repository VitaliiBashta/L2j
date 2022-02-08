
package com.l2jserver.datapack.ai.group_template;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.util.Util;

/**
 * AI for mobs in Plains of Dion (near Floran Village).
 * @author Gladicek
 */
public final class PlainsOfDion extends AbstractNpcAI {
	private static final int DELU_LIZARDMEN[] = {
		21104, // Delu Lizardman Supplier
		21105, // Delu Lizardman Special Agent
		21107, // Delu Lizardman Commander
	};
	
	private static final NpcStringId[] MONSTERS_MSG = {
		NpcStringId.S1_HOW_DARE_YOU_INTERRUPT_OUR_FIGHT_HEY_GUYS_HELP,
		NpcStringId.S1_HEY_WERE_HAVING_A_DUEL_HERE,
		NpcStringId.THE_DUEL_IS_OVER_ATTACK,
		NpcStringId.FOUL_KILL_THE_COWARD,
		NpcStringId.HOW_DARE_YOU_INTERRUPT_A_SACRED_DUEL_YOU_MUST_BE_TAUGHT_A_LESSON
	};
	
	private static final NpcStringId[] MONSTERS_ASSIST_MSG = {
		NpcStringId.DIE_YOU_COWARD,
		NpcStringId.KILL_THE_COWARD,
		NpcStringId.WHAT_ARE_YOU_LOOKING_AT
	};
	
	public PlainsOfDion() {
		super(PlainsOfDion.class.getSimpleName(), "ai/group_template");
		addAttackId(DELU_LIZARDMEN);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isSummon) {
		if (npc.isScriptValue(0)) {
			final int i = getRandom(5);
			if (i < 2) {
				broadcastNpcSay(npc, Say2.NPC_ALL, MONSTERS_MSG[i], player.getName());
			} else {
				broadcastNpcSay(npc, Say2.NPC_ALL, MONSTERS_MSG[i]);
			}
			
			for (L2Character obj : npc.getKnownList().getKnownCharactersInRadius(npc.getTemplate().getClanHelpRange())) {
				if (obj.isMonster() && Util.contains(DELU_LIZARDMEN, obj.getId()) && !obj.isAttackingNow() && !obj.isDead() && GeoData.getInstance().canSeeTarget(npc, obj)) {
					final L2Npc monster = (L2Npc) obj;
					addAttackDesire(monster, player);
					broadcastNpcSay(monster, Say2.NPC_ALL, MONSTERS_ASSIST_MSG[getRandom(3)]);
				}
			}
			npc.setScriptValue(1);
		}
		return super.onAttack(npc, player, damage, isSummon);
	}
}