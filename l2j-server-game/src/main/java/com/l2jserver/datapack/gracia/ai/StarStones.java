
package com.l2jserver.datapack.gracia.ai;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.SystemMessageId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StarStones extends AbstractNpcAI {
	// @formatter:off
	private static final int[] MOBS =
	{
		18684, 18685, 18686, 18687, 18688, 18689, 18690, 18691, 18692
	};
	// @formatter:on
	
	private static final int COLLECTION_RATE = 1;
	
	public StarStones() {
		super(StarStones.class.getSimpleName(), "gracia/AI");
		addSkillSeeId(MOBS);
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, List<L2Object> targets, boolean isSummon) {
		if (skill.getId() == 932) {
			int itemId = 0;
			
			switch (npc.getId()) {
				case 18684:
				case 18685:
				case 18686:
					// give Red item
					itemId = 14009;
					break;
				case 18687:
				case 18688:
				case 18689:
					// give Blue item
					itemId = 14010;
					break;
				case 18690:
				case 18691:
				case 18692:
					// give Green item
					itemId = 14011;
					break;
				default:
					// unknown npc!
					return super.onSkillSee(npc, caster, skill, targets, isSummon);
			}
			if (getRandom(100) < 33) {
				caster.sendPacket(SystemMessageId.THE_COLLECTION_HAS_SUCCEEDED);
				caster.addItem("StarStone", itemId, getRandom(COLLECTION_RATE + 1, 2 * COLLECTION_RATE), null, true);
			} else if (((skill.getLevel() == 1) && (getRandom(100) < 15)) || ((skill.getLevel() == 2) && (getRandom(100) < 50)) || ((skill.getLevel() == 3) && (getRandom(100) < 75))) {
				caster.sendPacket(SystemMessageId.THE_COLLECTION_HAS_SUCCEEDED);
				caster.addItem("StarStone", itemId, getRandom(1, COLLECTION_RATE), null, true);
			} else {
				caster.sendPacket(SystemMessageId.THE_COLLECTION_HAS_FAILED);
			}
			npc.deleteMe();
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
}
