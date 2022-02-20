package com.l2jserver.datapack.quests.Q00649_ALooterAndARailroadMan;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.util.Util;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Q00649_ALooterAndARailroadMan extends Quest {
  // Npc
  private static final int RAILMAN_OBI = 32052;
  // Item
  private static final int THIEF_GUILD_MARK = 8099;
  // Misc
  private static final int MIN_LVL = 30;
  // Monsters
  private static final Map<Integer, Integer> MONSTERS = new HashMap<>();

  static {
    MONSTERS.put(22017, 529); // Bandit Sweeper
    MONSTERS.put(22018, 452); // Bandit Hound
    MONSTERS.put(22019, 606); // Bandit Watchman
    MONSTERS.put(22021, 615); // Bandit Undertaker
    MONSTERS.put(22022, 721); // Bandit Assassin
    MONSTERS.put(22023, 827); // Bandit Warrior
    MONSTERS.put(22024, 779); // Bandit Inspector
    MONSTERS.put(22026, 1000); // Bandit Captain
  }

  public Q00649_ALooterAndARailroadMan() {
    super(649, Q00649_ALooterAndARailroadMan.class.getSimpleName(), "A Looter and a Railroad Man");
    addStartNpc(RAILMAN_OBI);
    addTalkId(RAILMAN_OBI);
    addKillId(MONSTERS.keySet());
    registerQuestItems(THIEF_GUILD_MARK);
  }

  @Override
  public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
    final QuestState st = getQuestState(player, false);
    if (st == null) {
      return null;
    }

    String htmltext = null;
    switch (event) {
      case "32052-03.htm":
        {
          if (st.isCreated()) {
            st.startQuest();
            htmltext = event;
          }
          break;
        }
      case "32052-06.html":
        {
          if (st.isCond(2) && st.hasQuestItems(THIEF_GUILD_MARK)) {
            st.giveAdena(21698, true);
            st.exitQuest(true, true);
            htmltext = event;
          }
        }
    }
    return htmltext;
  }

  @Override
  public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
    final QuestState st = getQuestState(killer, false);
    if ((st != null)
        && st.isCond(1)
        && Util.checkIfInRange(1500, npc, killer, false)
        && (getRandom(1000) < MONSTERS.get(npc.getId()))) {
      st.giveItems(THIEF_GUILD_MARK, 1);
      if (st.getQuestItemsCount(THIEF_GUILD_MARK) == 200) {
        st.setCond(2, true);
      } else {
        st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
      }
    }
    return super.onKill(npc, killer, isSummon);
  }

  @Override
  public String onTalk(L2Npc npc, L2PcInstance player) {
    final QuestState st = getQuestState(player, true);
    String htmltext = getNoQuestMsg(player);
    switch (st.getState()) {
      case State.CREATED:
        {
          htmltext = (player.getLevel() >= MIN_LVL) ? "32052-01.htm" : "32052-02.htm";
          break;
        }
      case State.STARTED:
        {
          htmltext =
              (st.getQuestItemsCount(THIEF_GUILD_MARK) == 200) ? "32052-04.html" : "32052-05.html";
          break;
        }
    }
    return htmltext;
  }
}
