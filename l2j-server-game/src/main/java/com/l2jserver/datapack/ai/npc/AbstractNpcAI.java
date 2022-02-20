package com.l2jserver.datapack.ai.npc;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.MinionHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;
import com.l2jserver.gameserver.network.serverpackets.SocialAction;
import com.l2jserver.gameserver.util.Broadcast;

public abstract class AbstractNpcAI extends Quest {
  protected AbstractNpcAI(String name, String descr) {
    super(-1, name, descr);
  }

  /** Simple on first talk event handler. */
  @Override
  public String onFirstTalk(L2Npc npc, L2PcInstance player) {
    return npc.getId() + ".html";
  }

  /**
   * Registers the following events to the current script:<br>
   *
   * <ul>
   *   <li>ON_ATTACK
   *   <li>ON_KILL
   *   <li>ON_SPAWN
   *   <li>ON_SPELL_FINISHED
   *   <li>ON_SKILL_SEE
   *   <li>ON_FACTION_CALL
   *   <li>ON_AGGR_RANGE_ENTER
   * </ul>
   *
   */
  public void registerMobs(int... mobs) {
    addAttackId(mobs);
    addKillId(mobs);
    addSpawnId(mobs);
    addSpellFinishedId(mobs);
    addSkillSeeId(mobs);
    addAggroRangeEnterId(mobs);
    addFactionCallId(mobs);
  }

  /** Broadcasts NpcSay packet to all known players with custom string. */
  protected void broadcastNpcSay(L2Npc npc, int type, String text) {
    Broadcast.toKnownPlayers(
        npc, new NpcSay(npc.getObjectId(), type, npc.getTemplate().getDisplayId(), text));
  }

  /** Broadcasts NpcSay packet to all known players with npc string id. */
  protected void broadcastNpcSay(L2Npc npc, int type, NpcStringId stringId) {
    Broadcast.toKnownPlayers(
        npc, new NpcSay(npc.getObjectId(), type, npc.getTemplate().getDisplayId(), stringId));
  }

  /** Broadcasts NpcSay packet to all known players with npc string id. */
  protected void broadcastNpcSay(L2Npc npc, int type, NpcStringId stringId, String... parameters) {
    final NpcSay say =
        new NpcSay(npc.getObjectId(), type, npc.getTemplate().getDisplayId(), stringId);
    if (parameters != null) {
      for (String parameter : parameters) {
        say.addStringParameter(parameter);
      }
    }
    Broadcast.toKnownPlayers(npc, say);
  }

  /** Broadcasts NpcSay packet to all known players with custom string in specific radius. */
  protected void broadcastNpcSay(L2Npc npc, int type, String text, int radius) {
    Broadcast.toKnownPlayersInRadius(
        npc, new NpcSay(npc.getObjectId(), type, npc.getTemplate().getDisplayId(), text), radius);
  }

  /** Broadcasts NpcSay packet to all known players with npc string id in specific radius. */
  protected void broadcastNpcSay(L2Npc npc, int type, NpcStringId stringId, int radius) {
    Broadcast.toKnownPlayersInRadius(
        npc,
        new NpcSay(npc.getObjectId(), type, npc.getTemplate().getDisplayId(), stringId),
        radius);
  }

  /** Broadcasts SocialAction packet to self and known players. */
  protected void broadcastSocialAction(L2Character character, int actionId) {
    Broadcast.toSelfAndKnownPlayers(character, new SocialAction(character.getObjectId(), actionId));
  }

  /** Broadcasts SocialAction packet to self and known players in specific radius. */
  protected void broadcastSocialAction(L2Character character, int actionId, int radius) {
    Broadcast.toSelfAndKnownPlayersInRadius(
        character, new SocialAction(character.getObjectId(), actionId), radius);
  }

  public void spawnMinions(final L2Npc npc, final String spawnName) {
    for (MinionHolder is : npc.getTemplate().getParameters().getMinionList(spawnName)) {
      addMinion((L2MonsterInstance) npc, is.getId());
    }
  }
}
