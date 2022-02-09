
package com.l2jserver.datapack.ai.group_template;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.interfaces.ILocational;
import com.l2jserver.gameserver.util.Util;

/**
 * Flee Monsters AI.
 * @author Pandragon, NosBit
 */
public final class FleeMonsters extends AbstractNpcAI {
	// NPCs
	private static final int[] MOBS = {
		18150, // Victim
		18151, // Victim
		18152, // Victim
		18153, // Victim
		18154, // Victim
		18155, // Victim
		18156, // Victim
		18157, // Victim
		20002, // Rabbit
		20432, // Elpy
		22228, // Grey Elpy
		25604, // Mutated Elpy
	};
	// Misc
	private static final int FLEE_DISTANCE = 500;
	
	public FleeMonsters() {
		super(FleeMonsters.class.getSimpleName(), "ai/group_template");
		addAttackId(MOBS);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
		npc.disableCoreAI(true);
		npc.setRunning();
		
		final L2Summon summon = isSummon ? attacker.getSummon() : null;
		final ILocational attackerLoc = summon == null ? attacker : summon;
		final double radians = Math.toRadians(Util.calculateAngleFrom(attackerLoc, npc));
		final int posX = (int) (npc.getX() + (FLEE_DISTANCE * Math.cos(radians)));
		final int posY = (int) (npc.getY() + (FLEE_DISTANCE * Math.sin(radians)));
		final int posZ = npc.getZ();
		
		final Location destination = GeoData.getInstance().moveCheck(npc.getX(), npc.getY(), npc.getZ(), posX, posY, posZ, attacker.getInstanceId());
		npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, destination);
		return super.onAttack(npc, attacker, damage, isSummon);
	}
}
