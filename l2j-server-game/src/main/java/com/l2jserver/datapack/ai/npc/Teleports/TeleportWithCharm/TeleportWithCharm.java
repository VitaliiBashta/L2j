package com.l2jserver.datapack.ai.npc.Teleports.TeleportWithCharm;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class TeleportWithCharm extends AbstractNpcAI {
  // NPCs
  private static final int WHIRPY = 30540;
  private static final int TAMIL = 30576;
  // Items
  private static final int ORC_GATEKEEPER_CHARM = 1658;
  private static final int DWARF_GATEKEEPER_TOKEN = 1659;
  // Locations
  private static final Location ORC_TELEPORT = new Location(-80826, 149775, -3043);
  private static final Location DWARF_TELEPORT = new Location(-80826, 149775, -3043);

  public TeleportWithCharm() {
    super(TeleportWithCharm.class.getSimpleName(), "ai/npc/Teleports");
    addStartNpc(WHIRPY, TAMIL);
    addTalkId(WHIRPY, TAMIL);
  }

  @Override
  public String onTalk(L2Npc npc, L2PcInstance player) {
    switch (npc.getId()) {
      case WHIRPY:
        {
          if (hasQuestItems(player, DWARF_GATEKEEPER_TOKEN)) {
            takeItems(player, DWARF_GATEKEEPER_TOKEN, 1);
            player.teleToLocation(DWARF_TELEPORT);
          } else {
            return "30540-01.htm";
          }
          break;
        }
      case TAMIL:
        {
          if (hasQuestItems(player, ORC_GATEKEEPER_CHARM)) {
            takeItems(player, ORC_GATEKEEPER_CHARM, 1);
            player.teleToLocation(ORC_TELEPORT);
          } else {
            return "30576-01.htm";
          }
          break;
        }
    }
    return super.onTalk(npc, player);
  }
}
