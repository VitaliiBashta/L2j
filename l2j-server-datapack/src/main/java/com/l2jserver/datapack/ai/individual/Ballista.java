package com.l2jserver.datapack.ai.individual;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.SystemMessageId;

import java.util.List;

import static com.l2jserver.gameserver.config.Configuration.clan;

public final class Ballista extends AbstractNpcAI {
  // NPCs
  private static final int[] BALLISTA = {
    35685, // Shanty Fortress
    35723, // Southern Fortress
    35754, // Hive Fortress
    35792, // Valley Fortress
    35823, // Ivory Fortress
    35854, // Narsell Fortress
    35892, // Bayou Fortress
    35923, // White Sands Fortress
    35961, // Borderland Fortress
    35999, // Swamp Fortress
    36030, // Archaic Fortress
    36068, // Floran Fortress
    36106, // Cloud Mountain)
    36137, // Tanor Fortress
    36168, // Dragonspine Fortress
    36206, // Antharas's Fortress
    36244, // Western Fortress
    36282, // Hunter's Fortress
    36313, // Aaru Fortress
    36351, // Demon Fortress
    36389, // Monastic Fortress
  };
  // Skill
  private static final SkillHolder BOMB = new SkillHolder(2342, 1); // Ballista Bomb
  // Misc
  private static final int MIN_CLAN_LV = 5;

  public Ballista() {
    super(Ballista.class.getSimpleName(), "ai/individual");
    addSkillSeeId(BALLISTA);
    addSpawnId(BALLISTA);
  }

  @Override
  public String onSkillSee(
      L2Npc npc, L2PcInstance caster, Skill skill, List<L2Object> targets, boolean isSummon) {
    if ((skill != null)
        && (caster.getTarget() == npc)
        && (getRandom(100) < 40)
        && (skill == BOMB.getSkill())) {
      if (npc.getFort().getSiege().isInProgress()) {
        if ((caster.getClan() != null) && (caster.getClan().getLevel() >= MIN_CLAN_LV)) {
          caster.getClan().addReputationScore(clan().getKillBallistaPoints(), true);
          caster.sendPacket(SystemMessageId.BALLISTA_DESTROYED_CLAN_REPU_INCREASED);
        }
      }
      npc.doDie(caster);
    }
    return super.onSkillSee(npc, caster, skill, targets, isSummon);
  }

  @Override
  public String onSpawn(L2Npc npc) {
    npc.disableCoreAI(true);
    npc.setIsMortal(false);
    return super.onSpawn(npc);
  }
}
