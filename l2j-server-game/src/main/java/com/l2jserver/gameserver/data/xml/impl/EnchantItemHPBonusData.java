package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.enums.StatFunction;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.CrystalType;
import com.l2jserver.gameserver.model.stats.Stats;
import com.l2jserver.gameserver.model.stats.functions.FuncTemplate;
import com.l2jserver.gameserver.util.IXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.*;

@Service
public class EnchantItemHPBonusData extends IXmlReader {

  private static final Logger LOG = LoggerFactory.getLogger(EnchantItemHPBonusData.class);

  private static final float FULL_ARMOR_MODIFIER = 1.5f;

  private final Map<CrystalType, List<Integer>> armorHPBonuses = new EnumMap<>(CrystalType.class);
  private final ItemTable itemTable;

  protected EnchantItemHPBonusData(ItemTable itemTable) {
    this.itemTable = itemTable;
  }

  @Override
  public void load() {
    armorHPBonuses.clear();
    parseDatapackFile("data/stats/enchantHPBonus.xml");
    LOG.info("Loaded {} Enchant HP Bonuses.", armorHPBonuses.size());
  }

  @Override
  public void parseDocument(Document doc) {
    for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
      if ("list".equalsIgnoreCase(n.getNodeName())) {
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
          if ("enchantHP".equalsIgnoreCase(d.getNodeName())) {
            final List<Integer> bonuses = new ArrayList<>(12);
            for (Node e = d.getFirstChild(); e != null; e = e.getNextSibling()) {
              if ("bonus".equalsIgnoreCase(e.getNodeName())) {
                bonuses.add(Integer.parseInt(e.getTextContent()));
              }
            }
            armorHPBonuses.put(parseEnum(d.getAttributes(), CrystalType.class, "grade"), bonuses);
          }
        }
      }
    }

    if (armorHPBonuses.isEmpty()) {
      return;
    }
    // Armors
    final Collection<Integer> armorIds = itemTable.getAllArmorsId();
    for (Integer itemId : armorIds) {
      L2Item item = itemTable.getTemplate(itemId);
      if ((item != null) && (item.getCrystalType() != CrystalType.NONE)) {
        switch (item.getBodyPart()) {
          case L2Item.SLOT_CHEST:
          case L2Item.SLOT_FEET:
          case L2Item.SLOT_GLOVES:
          case L2Item.SLOT_HEAD:
          case L2Item.SLOT_LEGS:
          case L2Item.SLOT_BACK:
          case L2Item.SLOT_FULL_ARMOR:
          case L2Item.SLOT_UNDERWEAR:
          case L2Item.SLOT_L_HAND:
          case L2Item.SLOT_BELT:
            item.attach(
                new FuncTemplate(
                    null, null, StatFunction.ENCHANTHP.getName(), -1, Stats.MAX_HP, 0));
            break;
          default:
            break;
        }
      }
    }
  }

  /**
   * Gets the HP bonus.
   *
   * @param item the item
   * @return the HP bonus
   */
  public final int getHPBonus(L2ItemInstance item) {
    final List<Integer> values = armorHPBonuses.get(item.getItem().getItemGradeSPlus());
    if ((values == null) || values.isEmpty() || (item.getOlyEnchantLevel() <= 0)) {
      return 0;
    }

    final int bonus = values.get(Math.min(item.getOlyEnchantLevel(), values.size()) - 1);
    if (item.getItem().getBodyPart() == L2Item.SLOT_FULL_ARMOR) {
      return (int) (bonus * FULL_ARMOR_MODIFIER);
    }
    return bonus;
  }
}
