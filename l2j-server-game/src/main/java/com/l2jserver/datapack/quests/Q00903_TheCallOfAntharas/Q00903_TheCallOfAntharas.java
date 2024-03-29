package com.l2jserver.datapack.quests.Q00903_TheCallOfAntharas;

import com.l2jserver.gameserver.enums.QuestType;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.util.Util;
import org.springframework.stereotype.Service;

@Service
public class Q00903_TheCallOfAntharas extends Quest {
  // NPC
  private static final int THEODRIC = 30755;
  // Monsters
  private static final int BEHEMOTH_DRAGON = 29069;
  private static final int TARASK_DRAGON = 29190;
  // Items
  private static final int TARASK_DRAGONS_LEATHER_FRAGMENT = 21991;
  private static final int BEHEMOTH_DRAGON_LEATHER = 21992;
  private static final int SCROLL_ANTHARAS_CALL = 21897;
  private static final int PORTAL_STONE = 3865;
  // Misc
  private static final int MIN_LEVEL = 83;

  public Q00903_TheCallOfAntharas() {
    super(903, Q00903_TheCallOfAntharas.class.getSimpleName(), "The Call of Antharas");
    addStartNpc(THEODRIC);
    addTalkId(THEODRIC);
    addKillId(BEHEMOTH_DRAGON, TARASK_DRAGON);
    registerQuestItems(TARASK_DRAGONS_LEATHER_FRAGMENT, BEHEMOTH_DRAGON_LEATHER);
  }

  @Override
  public void actionForEachPlayer(L2PcInstance player, L2Npc npc, boolean isSummon) {
    final QuestState st = getQuestState(player, false);
    if ((st != null) && Util.checkIfInRange(1500, npc, player, false)) {
      switch (npc.getId()) {
        case BEHEMOTH_DRAGON:
          {
            st.giveItems(BEHEMOTH_DRAGON_LEATHER, 1);
            st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
            break;
          }
        case TARASK_DRAGON:
          {
            st.giveItems(TARASK_DRAGONS_LEATHER_FRAGMENT, 1);
            st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
            break;
          }
      }

      if (st.hasQuestItems(BEHEMOTH_DRAGON_LEATHER)
          && st.hasQuestItems(TARASK_DRAGONS_LEATHER_FRAGMENT)) {
        st.setCond(2, true);
      }
    }
  }

  @Override
  public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
    final QuestState st = getQuestState(player, false);
    if (st == null) {
      return null;
    }

    String htmltext = null;
    if ((player.getLevel() >= MIN_LEVEL) && st.hasQuestItems(PORTAL_STONE)) {
      switch (event) {
        case "30755-05.htm":
          {
            htmltext = event;
            break;
          }
        case "30755-06.html":
          {
            st.startQuest();
            htmltext = event;
            break;
          }
      }
    }
    return htmltext;
  }

  @Override
  public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
    executeForEachPlayer(killer, npc, isSummon, true, false);
    return super.onKill(npc, killer, isSummon);
  }

  @Override
  public String onTalk(L2Npc npc, L2PcInstance player) {
    final QuestState st = getQuestState(player, true);
    String htmltext = getNoQuestMsg(player);
    switch (st.getState()) {
      case State.CREATED:
        {
          if (player.getLevel() < MIN_LEVEL) {
            htmltext = "30755-03.html";
          } else if (!st.hasQuestItems(PORTAL_STONE)) {
            htmltext = "30755-04.html";
          } else {
            htmltext = "30755-01.htm";
          }
          break;
        }
      case State.STARTED:
        {
          switch (st.getCond()) {
            case 1:
              {
                htmltext = "30755-07.html";
                break;
              }
            case 2:
              {
                st.giveItems(SCROLL_ANTHARAS_CALL, 1);
                st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
                st.exitQuest(QuestType.DAILY, true);
                htmltext = "30755-08.html";
                break;
              }
          }
          break;
        }
      case State.COMPLETED:
        {
          if (!st.isNowAvailable()) {
            htmltext = "30755-02.html";
          } else {
            st.setState(State.CREATED);
            if (player.getLevel() < MIN_LEVEL) {
              htmltext = "30755-03.html";
            } else if (!st.hasQuestItems(PORTAL_STONE)) {
              htmltext = "30755-04.html";
            } else {
              htmltext = "30755-01.htm";
            }
          }
          break;
        }
    }
    return htmltext;
  }
}
