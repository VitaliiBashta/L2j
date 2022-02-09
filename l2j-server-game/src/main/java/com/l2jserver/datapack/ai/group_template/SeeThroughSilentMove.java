
package com.l2jserver.datapack.ai.group_template;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;

/**
 * See Through Silent Move AI.
 * @author Gigiikun
 */
public class SeeThroughSilentMove extends AbstractNpcAI {
	//@formatter:off
	private static final int[] MONSTERS =
	{
		18001, 18002, 22199, 22215, 22216, 22217, 22327, 22746, 22747, 22748,
		22749, 22750, 22751, 22752, 22753, 22754, 22755, 22756, 22757, 22758,
		22759, 22760, 22761, 22762, 22763, 22764, 22765, 22794, 22795, 22796,
		22797, 22798, 22799, 22800, 22843, 22857, 25725, 25726, 25727, 29009,
		29010, 29011, 29012, 29013
	};
	//@formatter:on
	
	public SeeThroughSilentMove() {
		super(SeeThroughSilentMove.class.getSimpleName(), "ai/group_template");
		addSpawnId(MONSTERS);
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		if (npc.isAttackable()) {
			((L2Attackable) npc).setSeeThroughSilentMove(true);
		}
		return super.onSpawn(npc);
	}
}
