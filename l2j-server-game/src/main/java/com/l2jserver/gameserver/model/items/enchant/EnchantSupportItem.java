package com.l2jserver.gameserver.model.items.enchant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.type.EtcItemType;

public final class EnchantSupportItem extends AbstractEnchantItem {
  private final boolean _isWeapon;

  public EnchantSupportItem(StatsSet set, L2Item l2Item) {
    super(set, l2Item);
    _isWeapon = getItem().getItemType() == EtcItemType.SCRL_INC_ENCHANT_PROP_WP;
  }

  @Override
  public boolean isWeapon() {
    return _isWeapon;
  }
}
