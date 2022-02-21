package com.l2jserver.datapack.ai.npc.WyvernManager;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.SevenSigns;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Fort;
import com.l2jserver.gameserver.model.entity.clanhall.SiegableHall;
import com.l2jserver.gameserver.util.Util;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.l2jserver.gameserver.config.Configuration.castle;

@Service
public class WyvernManager extends AbstractNpcAI {
  // Misc
  private static final int CRYSTAL_B_GRADE = 1460;
  private static final int WYVERN = 12621;
  private static final int WYVERN_FEE = 25;
  private static final int STRIDER_LVL = 55;
  private static final int[] STRIDERS = {12526, 12527, 12528, 16038, 16039, 16040, 16068, 13197};
  // NPCS
  private static final Map<Integer, ManagerType> MANAGERS = new HashMap<>();

  static {
    MANAGERS.put(35101, ManagerType.CASTLE);
    MANAGERS.put(35143, ManagerType.CASTLE);
    MANAGERS.put(35185, ManagerType.CASTLE);
    MANAGERS.put(35227, ManagerType.CASTLE);
    MANAGERS.put(35275, ManagerType.CASTLE);
    MANAGERS.put(35317, ManagerType.CASTLE);
    MANAGERS.put(35364, ManagerType.CASTLE);
    MANAGERS.put(35510, ManagerType.CASTLE);
    MANAGERS.put(35536, ManagerType.CASTLE);
    MANAGERS.put(35419, ManagerType.CLAN_HALL);
    MANAGERS.put(35638, ManagerType.CLAN_HALL);
    MANAGERS.put(36457, ManagerType.FORT);
    MANAGERS.put(36458, ManagerType.FORT);
    MANAGERS.put(36459, ManagerType.FORT);
    MANAGERS.put(36460, ManagerType.FORT);
    MANAGERS.put(36461, ManagerType.FORT);
    MANAGERS.put(36462, ManagerType.FORT);
    MANAGERS.put(36463, ManagerType.FORT);
    MANAGERS.put(36464, ManagerType.FORT);
    MANAGERS.put(36465, ManagerType.FORT);
    MANAGERS.put(36466, ManagerType.FORT);
    MANAGERS.put(36467, ManagerType.FORT);
    MANAGERS.put(36468, ManagerType.FORT);
    MANAGERS.put(36469, ManagerType.FORT);
    MANAGERS.put(36470, ManagerType.FORT);
    MANAGERS.put(36471, ManagerType.FORT);
    MANAGERS.put(36472, ManagerType.FORT);
    MANAGERS.put(36473, ManagerType.FORT);
    MANAGERS.put(36474, ManagerType.FORT);
    MANAGERS.put(36475, ManagerType.FORT);
    MANAGERS.put(36476, ManagerType.FORT);
    MANAGERS.put(36477, ManagerType.FORT);
  }

  public WyvernManager() {
    super(WyvernManager.class.getSimpleName(), "ai/npc");
    addStartNpc(MANAGERS.keySet());
    addTalkId(MANAGERS.keySet());
    addFirstTalkId(MANAGERS.keySet());
  }

  @Override
  public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
    String htmltext = null;
    switch (event) {
      case "Return":
        {
          if (!isOwnerClan(npc, player)) {
            htmltext = "wyvernmanager-02.html";
          } else if (castle().allowRideWyvernAlways()) {
            htmltext = replaceAll(npc, player.getHtmlPrefix());
          } else if ((MANAGERS.get(npc.getId()) == ManagerType.CASTLE)
              && SevenSigns.getInstance().isSealValidationPeriod()
              && (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE)
                  == SevenSigns.CABAL_DUSK)) {
            htmltext = "wyvernmanager-dusk.html";
          } else {
            htmltext = replaceAll(npc, player.getHtmlPrefix());
          }
          break;
        }
      case "Help":
        {
          htmltext =
              MANAGERS.get(npc.getId()) == ManagerType.CASTLE
                  ? replacePart(player.getHtmlPrefix(), "wyvernmanager-03.html")
                  : replacePart(player.getHtmlPrefix(), "wyvernmanager-03b.html");
          break;
        }
      case "RideWyvern":
        {
          if (!castle().allowRideWyvernAlways()) {
            if (!castle().allowRideWyvernDuringSiege() && (isInSiege(npc) || player.isInSiege())) {
              player.sendMessage("You cannot summon wyvern while in siege.");
              return null;
            }
            if ((MANAGERS.get(npc.getId()) == ManagerType.CASTLE)
                && SevenSigns.getInstance().isSealValidationPeriod()
                && ((SevenSigns.getInstance()).getSealOwner(SevenSigns.SEAL_STRIFE)
                    == SevenSigns.CABAL_DUSK)) {
              htmltext = "wyvernmanager-dusk.html";
            } else {
              htmltext = mountWyvern(npc, player);
            }
          } else {
            htmltext = mountWyvern(npc, player);
          }
          break;
        }
    }
    return htmltext;
  }

  private String mountWyvern(L2Npc npc, L2PcInstance player) {
    if (player.isMounted()
        && (player.getMountLevel() >= STRIDER_LVL)
        && Util.contains(STRIDERS, player.getMountNpcId())) {
      if (isOwnerClan(npc, player) && (getQuestItemsCount(player, CRYSTAL_B_GRADE) >= WYVERN_FEE)) {
        takeItems(player, CRYSTAL_B_GRADE, WYVERN_FEE);
        player.dismount();
        player.mount(WYVERN, 0, true);
        return "wyvernmanager-04.html";
      }
      return replacePart(player.getHtmlPrefix(), "wyvernmanager-06.html");
    }
    return replacePart(player.getHtmlPrefix(), "wyvernmanager-05.html");
  }

  private boolean isOwnerClan(L2Npc npc, L2PcInstance player) {
    if (!player.isClanLeader()) {
      return false;
    }
    switch (MANAGERS.get(npc.getId())) {
      case CASTLE:
        {
          if (npc.getCastle() != null) {
            return player.getClanId() == npc.getCastle().getOwnerId();
          }
          return false;
        }
      case CLAN_HALL:
        {
          if (npc.getConquerableHall() != null) {
            return player.getClanId() == npc.getConquerableHall().getOwnerId();
          }
          return false;
        }
      case FORT:
        {
          final Fort fort = npc.getFort();
          if ((fort != null) && (fort.getOwnerClan() != null)) {
            return player.getClanId() == npc.getFort().getOwnerClan().getId();
          }
          return false;
        }
      default:
        {
          return false;
        }
    }
  }

  private boolean isInSiege(L2Npc npc) {
    switch (MANAGERS.get(npc.getId())) {
      case CASTLE:
        {
          return npc.getCastle().getZone().isActive();
        }
      case CLAN_HALL:
        {
          SiegableHall hall = npc.getConquerableHall();
          return (hall != null) ? hall.isInSiege() : npc.getCastle().getSiege().isInProgress();
        }
      case FORT:
        {
          return npc.getFort().getZone().isActive();
        }
      default:
        {
          return false;
        }
    }
  }

  private String replaceAll(L2Npc npc, String htmlPrefix) {
    return replacePart(htmlPrefix, "wyvernmanager-01.html")
        .replace("%residence_name%", getResidenceName(npc));
  }

  private String getResidenceName(L2Npc npc) {
    switch (MANAGERS.get(npc.getId())) {
      case CASTLE:
        {
          return npc.getCastle().getName();
        }
      case CLAN_HALL:
        {
          return npc.getConquerableHall().getName();
        }
      case FORT:
        {
          return npc.getFort().getName();
        }
      default:
        {
          return null;
        }
    }
  }

  private String replacePart(String htmlPrefix, String htmlFile) {
    return getHtm(htmlPrefix, htmlFile)
        .replace("%wyvern_fee%", String.valueOf(WYVERN_FEE))
        .replace("%strider_level%", String.valueOf(STRIDER_LVL));
  }

  @Override
  public String onFirstTalk(L2Npc npc, L2PcInstance player) {
    String htmltext = null;
    if (!isOwnerClan(npc, player)) {
      htmltext = "wyvernmanager-02.html";
    } else {
      if (castle().allowRideWyvernAlways()) {
        htmltext = replaceAll(npc, player.getHtmlPrefix());
      } else {
        if ((MANAGERS.get(npc.getId()) == ManagerType.CASTLE)
            && SevenSigns.getInstance().isSealValidationPeriod()
            && (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE)
                == SevenSigns.CABAL_DUSK)) {
          htmltext = "wyvernmanager-dusk.html";
        } else {
          htmltext = replaceAll(npc, player.getHtmlPrefix());
        }
      }
    }
    return htmltext;
  }

  private enum ManagerType {
    CASTLE,
    CLAN_HALL,
    FORT,
  }
}
