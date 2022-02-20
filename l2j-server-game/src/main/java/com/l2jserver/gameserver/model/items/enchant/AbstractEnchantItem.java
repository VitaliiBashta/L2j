package com.l2jserver.gameserver.model.items.enchant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.CrystalType;
import com.l2jserver.gameserver.model.items.type.EtcItemType;
import com.l2jserver.gameserver.model.items.type.ItemType;
import com.l2jserver.gameserver.model.items.type.ItemType2;

import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractEnchantItem {
  protected static final Logger _log = Logger.getLogger(AbstractEnchantItem.class.getName());

  private static final List<ItemType> ENCHANT_TYPES =
      List.of(
          EtcItemType.ANCIENT_CRYSTAL_ENCHANT_AM,
          EtcItemType.ANCIENT_CRYSTAL_ENCHANT_WP,
          EtcItemType.BLESS_SCRL_ENCHANT_AM,
          EtcItemType.BLESS_SCRL_ENCHANT_WP,
          EtcItemType.SCRL_ENCHANT_AM,
          EtcItemType.SCRL_ENCHANT_WP,
          EtcItemType.SCRL_INC_ENCHANT_PROP_AM,
          EtcItemType.SCRL_INC_ENCHANT_PROP_WP);

  private final int _id;
  private final CrystalType _grade;
  private final int _maxEnchantLevel;
  private final double _bonusRate;
  private final L2Item l2Item;

  public AbstractEnchantItem(StatsSet set, L2Item l2Item) {
    _id = set.getInt("id");
    this.l2Item = l2Item;
    if (getItem() == null) {
      throw new NullPointerException();
    } else if (!ENCHANT_TYPES.contains(getItem().getItemType())) {
      throw new IllegalAccessError();
    }
    _grade = set.getEnum("targetGrade", CrystalType.class, CrystalType.NONE);
    _maxEnchantLevel = set.getInt("maxEnchant", 65535);
    _bonusRate = set.getDouble("bonusRate", 0);
  }

  /** @return {@link L2Item} current item/scroll */
  public final L2Item getItem() {
    return l2Item;
  }

  /** @return id of current item */
  public final int getId() {
    return _id;
  }

  /** @return bonus chance that would be added */
  public final double getBonusRate() {
    return _bonusRate;
  }

  /** @return grade of the item/scroll. */
  public final CrystalType getGrade() {
    return _grade;
  }

  /** @return the maximum enchant level that this scroll/item can be used with */
  public int getMaxEnchantLevel() {
    return _maxEnchantLevel;
  }

  /**
   * @param itemToEnchant the item to be enchanted
   * @param supportItem
   * @return {@code true} if this support item can be used with the item to be enchanted, {@code
   *     false} otherwise
   */
  public boolean isValid(L2ItemInstance itemToEnchant, EnchantSupportItem supportItem) {
    if (itemToEnchant == null) {
      return false;
    } else if (itemToEnchant.isEnchantable() == 0) {
      return false;
    } else if (!isValidItemType(itemToEnchant.getItem().getType2())) {
      return false;
    } else if ((_maxEnchantLevel != 0) && (itemToEnchant.getEnchantLevel() >= _maxEnchantLevel)) {
      return false;
    }
    return _grade == itemToEnchant.getItem().getItemGradeSPlus();
  }

  /**
   * @param type2
   * @return {@code true} if current type2 is valid to be enchanted, {@code false} otherwise
   */
  private boolean isValidItemType(ItemType2 type2) {
    if (type2 == ItemType2.WEAPON) {
      return isWeapon();
    } else if ((type2 == ItemType2.SHIELD_ARMOR) || (type2 == ItemType2.ACCESSORY)) {
      return !isWeapon();
    }
    return false;
  }

  /** @return {@code true} if scroll is for weapon, {@code false} for armor */
  public abstract boolean isWeapon();
}
