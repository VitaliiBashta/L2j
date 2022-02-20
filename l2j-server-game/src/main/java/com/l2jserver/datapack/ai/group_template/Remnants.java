package com.l2jserver.datapack.ai.group_template;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Remnants extends AbstractNpcAI {
  private static final int[] NPCS = {18463, 18464, 18465};
  private static final int SKILL_HOLY_WATER = 2358;

  // TODO: Find retail strings.
  // private static final String MSG = "The holy water affects Remnants Ghost. You have freed his
  // soul.";
  // private static final String MSG_DEREK = "The holy water affects Derek. You have freed his
  // soul.";
  public Remnants() {
    super(Remnants.class.getSimpleName(), "ai/group_template");
    addSpawnId(NPCS);
    addSkillSeeId(NPCS);
    // Do not override onKill for Derek here. Let's make global Hellbound manipulations in Engine
    // where it is possible.
  }

  @Override
  public String onSkillSee(
      L2Npc npc, L2PcInstance caster, Skill skill, List<L2Object> targets, boolean isSummon) {
    if (skill.getId() == SKILL_HOLY_WATER) {
      // Lower, than 2%
      if (!npc.isDead()
          && (targets.size() > 0)
          && (targets.get(0) == npc)
          && npc.getCurrentHp() < (npc.getMaxHp() * 0.02)) {
        npc.doDie(caster);
        // @formatter:off
        /*if (npc.getNpcId() == DEREK)
        {
        	caster.sendMessage(MSG_DEREK);
        }
        else
        {
        	caster.sendMessage(MSG);
        }*/
        // @formatter:on
      }
    }
    return super.onSkillSee(npc, caster, skill, targets, isSummon);
  }

  @Override
  public String onSpawn(L2Npc npc) {
    npc.setIsMortal(false);
    return super.onSpawn(npc);
  }
}
