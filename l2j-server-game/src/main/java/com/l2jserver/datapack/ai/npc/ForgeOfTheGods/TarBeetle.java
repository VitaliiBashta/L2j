
package com.l2jserver.datapack.ai.npc.ForgeOfTheGods;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.Skill;
import org.springframework.stereotype.Service;

@Service
public class TarBeetle extends AbstractNpcAI {
	// NPC
	private static final int TAR_BEETLE = 18804;
	// Skills
	private static final int TAR_SPITE = 6142;
	private static SkillHolder[] SKILLS = {
		new SkillHolder(TAR_SPITE, 1),
		new SkillHolder(TAR_SPITE, 2),
		new SkillHolder(TAR_SPITE, 3)
	};
	
	private static final TarBeetleSpawn spawn = new TarBeetleSpawn();
	
	public TarBeetle() {
		super(TarBeetle.class.getSimpleName(), "ai/npc");
		addAggroRangeEnterId(TAR_BEETLE);
		addSpellFinishedId(TAR_BEETLE);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon) {
		if (npc.getScriptValue() > 0) {
			final BuffInfo info = player.getEffectList().getBuffInfoBySkillId(TAR_SPITE);
			final int level = (info != null) ? info.getSkill().getAbnormalLvl() : 0;
			if (level < 3) {
				final Skill skill = SKILLS[level].getSkill();
				if (!npc.isSkillDisabled(skill)) {
					npc.setTarget(player);
					npc.doCast(skill);
				}
			}
		}
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, Skill skill) {
		if ((skill != null) && (skill.getId() == TAR_SPITE)) {
			final int val = npc.getScriptValue() - 1;
			if ((val <= 0) || (SKILLS[0].getSkill().getMpConsume2() > npc.getCurrentMp())) {
				spawn.removeBeetle(npc);
			} else {
				npc.setScriptValue(val);
			}
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public boolean unload() {
		spawn.unload();
		return super.unload();
	}
}