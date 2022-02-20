package com.l2jserver.datapack.ai.npc.CastleMercenaryManager;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.SevenSigns;
import com.l2jserver.gameserver.model.ClanPrivilege;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MerchantInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import org.springframework.stereotype.Service;

import java.util.StringTokenizer;

@Service
public class CastleMercenaryManager extends AbstractNpcAI {
  // NPCs
  private static final int[] NPCS = {
    35102, // Greenspan
    35144, // Sanford
    35186, // Arvid
    35228, // Morrison
    35276, // Eldon
    35318, // Solinus
    35365, // Rowell
    35511, // Gompus
    35557, // Kendrew
  };
  private final SevenSigns sevenSigns;

  public CastleMercenaryManager(SevenSigns sevenSigns) {
    super(CastleMercenaryManager.class.getSimpleName(), "ai/npc");
    this.sevenSigns = sevenSigns;
    addStartNpc(NPCS);
    addTalkId(NPCS);
    addFirstTalkId(NPCS);
  }

  @Override
  public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
    String htmltext = null;
    final StringTokenizer st = new StringTokenizer(event, " ");
    switch (st.nextToken()) {
      case "limit":
        {
          final Castle castle = npc.getCastle();
          final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
          if (castle.getName().equals("aden")) {
            html.setHtml(getHtm(player.getHtmlPrefix(), "mercmanager-aden-limit.html"));
          } else if (castle.getName().equals("rune")) {
            html.setHtml(getHtm(player.getHtmlPrefix(), "mercmanager-rune-limit.html"));
          } else {
            html.setHtml(getHtm(player.getHtmlPrefix(), "mercmanager-limit.html"));
          }
          html.replace("%feud_name%", String.valueOf(1001000 + castle.getResidenceId()));
          player.sendPacket(html);
          break;
        }
      case "buy":
        {
          if (SevenSigns.getInstance().isSealValidationPeriod()) {
            final int listId = Integer.parseInt(npc.getId() + st.nextToken());
            ((L2MerchantInstance) npc)
                .showBuyWindow(
                    player, listId,
                    false); // NOTE: Not affected by Castle Taxes, baseTax is 20% (done in merchant
            // buylists)
          } else {
            htmltext = "mercmanager-ssq.html";
          }
          break;
        }
      case "main":
        {
          htmltext = onFirstTalk(npc, player);
          break;
        }
      case "mercmanager-01.html":
        {
          htmltext = event;
          break;
        }
    }
    return htmltext;
  }

  @Override
  public String onFirstTalk(L2Npc npc, L2PcInstance player) {
    final String htmltext;
    if (player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS)
        || ((player.getClanId() == npc.getCastle().getOwnerId())
            && player.hasClanPrivilege(ClanPrivilege.CS_MERCENARIES))) {
      if (npc.getCastle().getSiege().isInProgress()) {
        htmltext = "mercmanager-siege.html";
      } else {
        switch (sevenSigns.getSealOwner(SevenSigns.SEAL_STRIFE)) {
          case SevenSigns.CABAL_DUSK:
            htmltext = "mercmanager-dusk.html";
            break;
          case SevenSigns.CABAL_DAWN:
            htmltext = "mercmanager-dawn.html";
            break;
          default:
            htmltext = "mercmanager.html";
        }
      }
    } else {
      htmltext = "mercmanager-no.html";
    }
    return htmltext;
  }
}
