package com.l2jserver.gameserver.network;

import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.Elementals;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.TutorialShowHtml;

import java.util.Map.Entry;
import java.util.function.BiConsumer;

public class Debug {
  private Debug() {}

  public static void sendSkillDebug(
      L2Character attacker, L2Character target, Skill skill, StatsSet set) {
    if (!attacker.isPlayer()) {
      return;
    }

    StringBuilder sb = new StringBuilder();
    set.getSet().forEach(appendLine(sb));

    final NpcHtmlMessage msg = new NpcHtmlMessage();
    msg.setFile(attacker.getActingPlayer().getHtmlPrefix(), "data/html/admin/skilldebug.htm");
    msg.replace("%patk%", target.getPAtk(target));
    msg.replace("%matk%", target.getMAtk(target, skill));
    msg.replace("%pdef%", target.getPDef(target));
    msg.replace("%mdef%", target.getMDef(target, skill));
    msg.replace("%acc%", target.getAccuracy());
    msg.replace("%evas%", target.getEvasionRate(target));
    msg.replace("%crit%", target.getCriticalHit(target, skill));
    msg.replace("%speed%", target.getRunSpeed());
    msg.replace("%pAtkSpd%", target.getPAtkSpd());
    msg.replace("%mAtkSpd%", target.getMAtkSpd());
    msg.replace("%str%", target.getSTR());
    msg.replace("%dex%", target.getDEX());
    msg.replace("%con%", target.getCON());
    msg.replace("%int%", target.getINT());
    msg.replace("%wit%", target.getWIT());
    msg.replace("%men%", target.getMEN());
    msg.replace("%atkElemType%", Elementals.getElementName(target.getAttackElement()));
    msg.replace("%atkElemVal%", target.getAttackElementValue(target.getAttackElement()));
    msg.replace("%fireDef%", target.getDefenseElementValue((byte) 0));
    msg.replace("%waterDef%", target.getDefenseElementValue((byte) 1));
    msg.replace("%windDef%", target.getDefenseElementValue((byte) 2));
    msg.replace("%earthDef%", target.getDefenseElementValue((byte) 3));
    msg.replace("%holyDef%", target.getDefenseElementValue((byte) 4));
    msg.replace("%darkDef%", target.getDefenseElementValue((byte) 5));
    msg.replace("%skill%", skill.toString());
    msg.replace("%details%", sb.toString());
    String html = msg.getHtml();
    TutorialShowHtml tutorialShowHtml = new TutorialShowHtml(html);
    attacker.sendPacket(tutorialShowHtml);
  }

  private static BiConsumer<String, Object> appendLine(StringBuilder sb) {
    return (key, value) ->
        sb.append("<tr><td>")
            .append(key)
            .append("</td><td><font color=\"LEVEL\">")
            .append(value)
            .append("</font></td></tr>");
  }

  public static void sendItemDebug(L2PcInstance player, L2ItemInstance item, StatsSet set) {
    var sb = new StringBuilder();
    for (var entry : set.getSet().entrySet()) {
      sb.append("<tr><td>")
          .append(entry.getKey())
          .append("</td><td><font color=\"LEVEL\">")
          .append(entry.getValue())
          .append("</font></td></tr>");
    }

    final NpcHtmlMessage msg = new NpcHtmlMessage();
    msg.setFile(player.getHtmlPrefix(), "data/html/admin/itemdebug.htm");
    msg.replace("%itemName%", item.getName());
    msg.replace("%itemSlot%", getBodyPart(item.getItem().getBodyPart()));
    msg.replace("%itemType%", detectType(item));
    msg.replace("%enchantLevel%", item.getEnchantLevel());
    msg.replace("%isMagicWeapon%", item.getItem().isMagicWeapon());
    msg.replace("%item%", item.toString());
    msg.replace("%details%", sb.toString());
    player.sendPacket(new TutorialShowHtml(msg.getHtml()));
  }

  private static String detectType(L2ItemInstance item) {
    if (item.isArmor()) return "Armor";
    return item.isWeapon() ? "Weapon" : "Etc";
  }

  private static String getBodyPart(int bodyPart) {
    for (Entry<String, Integer> entry : ItemTable.SLOTS.entrySet()) {
      if ((entry.getValue() & bodyPart) == bodyPart) {
        return entry.getKey();
      }
    }
    return "Unknown";
  }
}
