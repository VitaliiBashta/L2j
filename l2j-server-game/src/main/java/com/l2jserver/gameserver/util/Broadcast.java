package com.l2jserver.gameserver.util;

import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public final class Broadcast {
  private static final Logger LOG = LogManager.getLogger(Broadcast.class.getName());

  private Broadcast() {}

  /**
   * Send a packet to all L2PcInstance in the _KnownPlayers of the L2Character.<br>
   * <B><U> Concept</U> :</B><br>
   * L2PcInstance in the detection area of the L2Character are identified in <B>_knownPlayers</B>.
   * <br>
   * In order to inform other players of state modification on the L2Character, server just need to
   * go through _knownPlayers to send Server->Client Packet<br>
   * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this
   * L2Character (to do this use method toSelfAndKnownPlayers)</B></FONT><br>
   *
   */
  public static void toKnownPlayers(L2Character character, L2GameServerPacket mov) {
    Map<Integer, L2PcInstance> knownPlayers = character.getKnownList().getKnownPlayers();
    for (L2PcInstance player : knownPlayers.values()) {
      if (player == null) {
        continue;
      }
      try {
        player.sendPacket(mov);
        if ((mov instanceof CharInfo) && (character instanceof L2PcInstance)) {
          int relation = ((L2PcInstance) character).getRelation(player);
          Integer oldrelation =
              character.getKnownList().getKnownRelations().get(player.getObjectId());
          if ((oldrelation != null) && (oldrelation != relation)) {
            player.sendPacket(
                new RelationChanged(
                    (L2PcInstance) character, relation, character.isAutoAttackable(player)));
            if (character.hasSummon()) {
              player.sendPacket(
                  new RelationChanged(
                      character.getSummon(), relation, character.isAutoAttackable(player)));
            }
          }
        }
      } catch (NullPointerException e) {
        LOG.warn(e.getMessage(), e);
      }
    }
  }

  /**
   * Send a packet to all L2PcInstance in the _KnownPlayers (in the specified radius) of the
   * L2Character.<br>
   * <B><U> Concept</U> :</B><br>
   * L2PcInstance in the detection area of the L2Character are identified in <B>_knownPlayers</B>.
   * <br>
   * In order to inform other players of state modification on the L2Character, server just needs to
   * go through _knownPlayers to send Server->Client Packet and check the distance between the
   * targets.<br>
   * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this
   * L2Character (to do this use method toSelfAndKnownPlayers)</B></FONT><br>
   */
  public static void toKnownPlayersInRadius(
      L2Character character, L2GameServerPacket mov, int radius) {
    if (radius < 0) {
      radius = 1500;
    }

    for (L2PcInstance player : character.getKnownList().getKnownPlayers().values()) {
      if (character.isInsideRadius(player, radius, false, false)) {
        player.sendPacket(mov);
      }
    }
  }

  /**
   * Send a packet to all L2PcInstance in the _KnownPlayers of the L2Character and to the specified
   * character.<br>
   * <B><U> Concept</U> :</B><br>
   * L2PcInstance in the detection area of the L2Character are identified in <B>_knownPlayers</B>.
   * <br>
   * In order to inform other players of state modification on the L2Character, server just need to
   * go through _knownPlayers to send Server->Client Packet<br>
   *
   */
  public static void toSelfAndKnownPlayers(L2Character character, L2GameServerPacket mov) {
    if (character instanceof L2PcInstance) {
      character.sendPacket(mov);
    }

    toKnownPlayers(character, mov);
  }

  // To improve performance we are comparing values of radius^2 instead of calculating sqrt all the
  // time
  public static void toSelfAndKnownPlayersInRadius(
      L2Character character, L2GameServerPacket mov, int radius) {
    if (radius < 0) {
      radius = 600;
    }

    if (character instanceof L2PcInstance) {
      character.sendPacket(mov);
    }

    for (L2PcInstance player : character.getKnownList().getKnownPlayers().values()) {
      if ((player != null) && Util.checkIfInRange(radius, character, player, false)) {
        player.sendPacket(mov);
      }
    }
  }

  /**
   * Send a packet to all L2PcInstance present in the world.<br>
   * <B><U> Concept</U> :</B><br>
   * In order to inform other players of state modification on the L2Character, server just need to
   * go through _allPlayers to send Server->Client Packet<br>
   * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this
   * L2Character (to do this use method toSelfAndKnownPlayers)</B></FONT><br>
   *

   */
  public static void toAllOnlinePlayers(L2GameServerPacket packet) {
    for (L2PcInstance player : L2World.getInstance().getPlayers()) {
      if (player.isOnline()) {
        player.sendPacket(packet);
      }
    }
  }

  public static void toAllOnlinePlayers(String text) {
    toAllOnlinePlayers(text, false);
  }

  public static void toAllOnlinePlayers(String text, boolean isCritical) {
    toAllOnlinePlayers(
        new CreatureSay(0, isCritical ? Say2.CRITICAL_ANNOUNCE : Say2.ANNOUNCEMENT, "", text));
  }

  public static void toPlayersInInstance(L2GameServerPacket packet, int instanceId) {
    for (L2PcInstance player : L2World.getInstance().getPlayers()) {
      if (player.isOnline() && (player.getInstanceId() == instanceId)) {
        player.sendPacket(packet);
      }
    }
  }

  public static void toAllOnlinePlayersOnScreen(String text) {
    toAllOnlinePlayers(new ExShowScreenMessage(text, 10000));
  }
}
