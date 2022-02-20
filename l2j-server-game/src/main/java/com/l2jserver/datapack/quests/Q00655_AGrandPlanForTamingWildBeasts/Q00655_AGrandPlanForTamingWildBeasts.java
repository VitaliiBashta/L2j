package com.l2jserver.datapack.quests.Q00655_AGrandPlanForTamingWildBeasts;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.instancemanager.ClanHallSiegeManager;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.clanhall.ClanHallSiegeEngine;
import com.l2jserver.gameserver.model.entity.clanhall.SiegableHall;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.util.Util;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Service
public class Q00655_AGrandPlanForTamingWildBeasts extends Quest {
  // NPCs
  private static final int MESSENGER = 35627;
  // Items
  private static final int CRYSTAL_OF_PURITY = 8084;
  private static final int TRAINER_LICENSE = 8293;
  // Misc
  private static final int REQUIRED_CRYSTAL_COUNT = 10;
  private static final int REQUIRED_CLAN_LEVEL = 4;
  private static final int MINUTES_TO_SIEGE = 3600;
  private static final String PATH_TO_HTML =
      "com/l2jserver/datapack/conquerablehalls/flagwar/WildBeastReserve/messenger_initial.htm";

  public Q00655_AGrandPlanForTamingWildBeasts() {
    super(
        655,
        Q00655_AGrandPlanForTamingWildBeasts.class.getSimpleName(),
        "A Grand Plan for Taming Wild Beasts");
    addStartNpc(MESSENGER);
    addTalkId(MESSENGER);
    registerQuestItems(CRYSTAL_OF_PURITY, TRAINER_LICENSE);
  }

  @Override
  public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
    final QuestState qs = getQuestState(player, false);
    if (qs == null) {
      return null;
    }

    String htmltext = null;
    final L2Clan clan = player.getClan();
    final long minutesToSiege = getMinutesToSiege();
    switch (event) {
      case "35627-06.html":
        {
          if (qs.isCreated()) {
            if ((clan != null)
                && (clan.getLevel() >= REQUIRED_CLAN_LEVEL)
                && (clan.getFortId() == 0) //
                && player.isClanLeader()
                && (minutesToSiege > 0)
                && (minutesToSiege < MINUTES_TO_SIEGE)) {
              qs.startQuest();
              htmltext = event;
            }
          }
          break;
        }
      case "35627-06a.html":
        {
          htmltext = event;
          break;
        }
      case "35627-11.html":
        {
          if ((minutesToSiege > 0) && (minutesToSiege < MINUTES_TO_SIEGE)) {
            htmltext = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), PATH_TO_HTML);
          } else {
            htmltext = getHtm(player.getHtmlPrefix(), event);
            htmltext = htmltext.replace("%next_siege%", getSiegeDate());
          }
          break;
        }
    }
    return htmltext;
  }

  @Override
  public String onTalk(L2Npc npc, L2PcInstance talker) {
    final QuestState qs = getQuestState(talker, true);
    String htmltext = getNoQuestMsg(talker);
    final long minutesToSiege = getMinutesToSiege();
    if (qs.isCreated()) {
      final L2Clan clan = talker.getClan();
      if (clan == null) {
        return htmltext;
      }

      if ((minutesToSiege > 0) && (minutesToSiege < MINUTES_TO_SIEGE)) {
        if (talker.isClanLeader()) {
          if (clan.getFortId() == 0) {
            if (clan.getLevel() >= REQUIRED_CLAN_LEVEL) {
              htmltext = "35627-01.html";
            } else {
              htmltext = "35627-03.html";
            }
          } else {
            htmltext = "35627-04.html";
          }
        } else {
          if ((clan.getFortId() == ClanHallSiegeEngine.BEAST_FARM)
              && (minutesToSiege > 0)
              && (minutesToSiege < MINUTES_TO_SIEGE)) {
            htmltext = HtmCache.getInstance().getHtm(talker.getHtmlPrefix(), PATH_TO_HTML);
          } else {
            htmltext = "35627-05.html";
          }
        }
      } else {
        htmltext = getHtm(talker.getHtmlPrefix(), "35627-02.html");
        htmltext = htmltext.replace("%next_siege%", getSiegeDate());
      }
    } else {
      if ((minutesToSiege < 0) || (minutesToSiege > MINUTES_TO_SIEGE)) {
        takeItems(talker, TRAINER_LICENSE, -1);
        takeItems(talker, CRYSTAL_OF_PURITY, -1);
        qs.exitQuest(true, true);
        htmltext = "35627-07.html";
      } else {
        if (hasQuestItems(talker, TRAINER_LICENSE)) {
          htmltext = "35627-09.html";
        } else {
          if (getQuestItemsCount(talker, CRYSTAL_OF_PURITY) < REQUIRED_CRYSTAL_COUNT) {
            htmltext = "35627-08.html";
          } else {
            giveItems(talker, TRAINER_LICENSE, 1);
            takeItems(talker, CRYSTAL_OF_PURITY, -1);
            qs.setCond(3, true);
            htmltext = "35627-10.html";
          }
        }
      }
    }
    return htmltext;
  }

  /**
   * Gets the Wild Beast Reserve's siege date.
   *
   * @return the siege date
   */
  private static String getSiegeDate() {
    final SiegableHall hall =
        ClanHallSiegeManager.getInstance().getSiegableHall(ClanHallSiegeEngine.BEAST_FARM);
    if (hall != null) {
      final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      return sdf.format(hall.getSiegeDate().getTime());
    }
    return "Error in date.";
  }

  /**
   * Gets the minutes to next siege.
   *
   * @return minutes to next siege
   */
  private static long getMinutesToSiege() {
    final SiegableHall hall =
        ClanHallSiegeManager.getInstance().getSiegableHall(ClanHallSiegeEngine.BEAST_FARM);
    if (hall != null) {
      return (hall.getNextSiegeTime() - Calendar.getInstance().getTimeInMillis()) / 3600;
    }
    return -1;
  }

  /**
   * Rewards the clan leader with a Crystal of Purity after player tame a wild beast.
   *
   * @param player the player
   * @param npc the wild beast
   */
  public void reward(L2PcInstance player, L2Npc npc) {
    final L2Clan clan = player.getClan();
    final L2PcInstance clanLeader = clan != null ? clan.getLeader().getPlayerInstance() : null;
    if (clanLeader != null) {
      final QuestState qs655 =
          clanLeader.getQuestState(Q00655_AGrandPlanForTamingWildBeasts.class.getSimpleName());
      if (qs655 != null) {
        if ((getQuestItemsCount(clanLeader, CRYSTAL_OF_PURITY) < REQUIRED_CRYSTAL_COUNT)
            && Util.checkIfInRange(2000, clanLeader, npc, true)) {
          if (clanLeader.getLevel() >= REQUIRED_CLAN_LEVEL) {
            giveItems(clanLeader, CRYSTAL_OF_PURITY, 1);
          }

          if (getQuestItemsCount(clanLeader, CRYSTAL_OF_PURITY) >= 9) {
            qs655.setCond(2, true);
          } else {
            playSound(clanLeader, Sound.ITEMSOUND_QUEST_ITEMGET);
          }
        }
      }
    }
  }
}
