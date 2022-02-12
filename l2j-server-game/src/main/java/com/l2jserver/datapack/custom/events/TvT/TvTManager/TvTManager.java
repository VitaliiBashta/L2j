package com.l2jserver.datapack.custom.events.TvT.TvTManager;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.TvTEvent;
import com.l2jserver.gameserver.model.olympiad.OlympiadManager;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import org.springframework.stereotype.Service;

import static com.l2jserver.gameserver.config.Configuration.tvt;

@Service
public class TvTManager extends AbstractNpcAI implements IVoicedCommandHandler {
  private static final int MANAGER_ID = 70010;
  private static final String[] COMMANDS = {"tvt", "tvtjoin", "tvtleave"};

  public TvTManager() {
    super(TvTManager.class.getSimpleName(), "custom/events/TvT");
    addFirstTalkId(MANAGER_ID);
    addTalkId(MANAGER_ID);
    addStartNpc(MANAGER_ID);
  }

  @Override
  public String onFirstTalk(L2Npc npc, L2PcInstance player) {
    String htmltext = null;
    if (TvTEvent.isParticipating()) {
      final boolean isParticipant = TvTEvent.isPlayerParticipant(player.getObjectId());
      int[] teamsPlayerCounts = TvTEvent.getTeamsPlayerCounts();
      htmltext =
          getHtm(
              player.getHtmlPrefix(),
              (!isParticipant ? "Participation.html" : "RemoveParticipation.html"));
      htmltext = htmltext.replaceAll("%objectId%", String.valueOf(npc.getObjectId()));
      htmltext = htmltext.replaceAll("%team1name%", tvt().getTeam1Name());
      htmltext = htmltext.replaceAll("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
      htmltext = htmltext.replaceAll("%team2name%", tvt().getTeam2Name());
      htmltext = htmltext.replaceAll("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
      htmltext =
          htmltext.replaceAll(
              "%playercount%", String.valueOf(teamsPlayerCounts[0] + teamsPlayerCounts[1]));

      if (!isParticipant) {
        htmltext = htmltext.replaceAll("%fee%", TvTEvent.getParticipationFee());
      }
    } else if (TvTEvent.isStarting() || TvTEvent.isStarted()) {
      htmltext = getTvTStatus(player);
    }
    return htmltext;
  }

  private String getTvTStatus(L2PcInstance player) {
    int[] teamsPlayerCounts = TvTEvent.getTeamsPlayerCounts();
    int[] teamsPointsCounts = TvTEvent.getTeamsPoints();
    String htmltext = getHtm(player.getHtmlPrefix(), "Status.html");
    htmltext = htmltext.replaceAll("%team1name%", tvt().getTeam1Name());
    htmltext = htmltext.replaceAll("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
    htmltext = htmltext.replaceAll("%team1points%", String.valueOf(teamsPointsCounts[0]));
    htmltext = htmltext.replaceAll("%team2name%", tvt().getTeam2Name());
    htmltext = htmltext.replaceAll("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
    htmltext = htmltext.replaceAll("%team2points%", String.valueOf(teamsPointsCounts[1]));
    return htmltext;
  }

  @Override
  public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params) {
    String html = null;
    switch (command) {
      case "tvt":
        {
          if (TvTEvent.isStarting() || TvTEvent.isStarted()) {
            html = getTvTStatus(activeChar);
          } else {
            html = "The event has not started.";
          }
          break;
        }
      case "tvtjoin":
        {
          html = onAdvEvent("join", null, activeChar);
          break;
        }
      case "tvtleave":
        {
          html = onAdvEvent("remove", null, activeChar);
          break;
        }
    }

    if (html != null) {
      activeChar.sendPacket(new NpcHtmlMessage(html));
    }
    return true;
  }

  @Override
  public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
    if ((player == null) || !TvTEvent.isParticipating()) {
      return super.onAdvEvent(event, npc, player);
    }

    String htmltext = null;
    switch (event) {
      case "join":
        {
          int playerLevel = player.getLevel();
          final int team1Count = TvTEvent.getTeamsPlayerCounts()[0];
          final int team2Count = TvTEvent.getTeamsPlayerCounts()[1];
          if (player.isCursedWeaponEquipped()) {
            htmltext = getHtm(player.getHtmlPrefix(), "CursedWeaponEquipped.html");
          } else if (OlympiadManager.getInstance().isRegistered(player)) {
            htmltext = getHtm(player.getHtmlPrefix(), "Olympiad.html");
          } else if (player.getKarma() > 0) {
            htmltext = getHtm(player.getHtmlPrefix(), "Karma.html");
          } else if ((playerLevel < tvt().getMinPlayerLevel())
              || (playerLevel > tvt().getMaxPlayerLevel())) {
            htmltext = getHtm(player.getHtmlPrefix(), "Level.html");
            htmltext = htmltext.replaceAll("%min%", String.valueOf(tvt().getMinPlayerLevel()));
            htmltext = htmltext.replaceAll("%max%", String.valueOf(tvt().getMaxPlayerLevel()));
          } else if ((team1Count == tvt().getMaxPlayersInTeams())
              && (team2Count == tvt().getMaxPlayersInTeams())) {
            htmltext = getHtm(player.getHtmlPrefix(), "TeamsFull.html");
            htmltext = htmltext.replaceAll("%max%", String.valueOf(tvt().getMaxPlayersInTeams()));
          } else if (TvTEvent.needParticipationFee() && !TvTEvent.hasParticipationFee(player)) {
            htmltext = getHtm(player.getHtmlPrefix(), "ParticipationFee.html");
            htmltext = htmltext.replaceAll("%fee%", TvTEvent.getParticipationFee());
          } else if (TvTEvent.addParticipant(player)) {
            htmltext = getHtm(player.getHtmlPrefix(), "Registered.html");
          }
          break;
        }
      case "remove":
        {
          if (TvTEvent.removeParticipant(player.getObjectId())) {
            htmltext = getHtm(player.getHtmlPrefix(), "Unregistered.html");
          } else {
            player.sendMessage("You cannot unregister to this event.");
          }
          break;
        }
    }
    return htmltext;
  }

  @Override
  public String[] getVoicedCommandList() {
    return COMMANDS;
  }
}
