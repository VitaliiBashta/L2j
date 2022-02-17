package com.l2jserver.datapack.ai.npc.EchoCrystals;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EchoCrystals extends AbstractNpcAI {
  // NPCs
  private static final int[] NPCs = {
    31042, // Kantabilon
    31043, // Octavia
  };
  private static final Map<Integer, RewardInfo> SCORES = new HashMap<>();

  static {
    SCORES.put(4410, new RewardInfo(4411, "01", "02", "03"));
    SCORES.put(4409, new RewardInfo(4412, "04", "05", "06"));
    SCORES.put(4408, new RewardInfo(4413, "07", "08", "09"));
    SCORES.put(4420, new RewardInfo(4414, "10", "11", "12"));
    SCORES.put(4421, new RewardInfo(4415, "13", "14", "15"));
    SCORES.put(4419, new RewardInfo(4417, "16", "02", "03"));
    SCORES.put(4418, new RewardInfo(4416, "17", "02", "03"));
  }

  public EchoCrystals() {
    super(EchoCrystals.class.getSimpleName(), "ai/npc");
    addStartNpc(NPCs);
    addTalkId(NPCs);
  }

  @Override
  public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
    String htmltext = null;
    final int score = Integer.valueOf(event);
    if (SCORES.containsKey(score)) {
      if (!hasQuestItems(player, score)) {
        htmltext = npc.getId() + "-" + SCORES.get(score).getNoScoreMsg() + ".htm";
      } else if (player.getAdena() < 200) {
        htmltext = npc.getId() + "-" + SCORES.get(score).getNoAdenaMsg() + ".htm";
      } else {
        takeItems(player, Inventory.ADENA_ID, 200);
        giveItems(player, SCORES.get(score).getCrystalId(), 1);
        htmltext = npc.getId() + "-" + SCORES.get(score).getOkMsg() + ".htm";
      }
    }
    return htmltext;
  }

  private static final class RewardInfo {
    public final int _crystalId;
    public final String _okMsg;
    public final String _noAdenaMsg;
    public final String _noScoreMsg;

    public RewardInfo(int crystalId, String okMsg, String noAdenaMsg, String noScoreMsg) {
      _crystalId = crystalId;
      _okMsg = okMsg;
      _noAdenaMsg = noAdenaMsg;
      _noScoreMsg = noScoreMsg;
    }

    public int getCrystalId() {
      return _crystalId;
    }

    public String getOkMsg() {
      return _okMsg;
    }

    public String getNoAdenaMsg() {
      return _noAdenaMsg;
    }

    public String getNoScoreMsg() {
      return _noScoreMsg;
    }
  }
}
