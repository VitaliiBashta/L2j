package com.l2jserver.gameserver.model;

import com.l2jserver.gameserver.data.xml.impl.EnchantSkillGroupsData;
import com.l2jserver.gameserver.model.L2EnchantSkillGroup.EnchantSkillHolder;

import java.util.Set;
import java.util.TreeMap;

public class L2EnchantSkillLearn {
  private final int id;
  private final int baseLvl;
  private final TreeMap<Integer, Integer> enchantRoutes = new TreeMap<>();

  public L2EnchantSkillLearn(int id, int baseLvl) {
    this.id = id;
    this.baseLvl = baseLvl;
  }

  public static int getEnchantRoute(int level) {
    return (int) Math.floor(level / 100.0);
  }

  public static int getEnchantIndex(int level) {
    return (level % 100) - 1;
  }

  public static int getEnchantType(int level) {
    return ((level - 1) / 100) - 1;
  }

  public void addNewEnchantRoute(int route, int group) {
    enchantRoutes.put(route, group);
  }

  public int getId() {
    return id;
  }

  public int getBaseLevel() {
    return baseLvl;
  }

  public L2EnchantSkillGroup getFirstRouteGroup() {
    return EnchantSkillGroupsData.getInstance()
        .getEnchantSkillGroupById(enchantRoutes.firstEntry().getValue());
  }

  public Set<Integer> getAllRoutes() {
    return enchantRoutes.keySet();
  }

  public int getMinSkillLevel(int level) {
    if ((level % 100) == 1) {
      return baseLvl;
    }
    return level - 1;
  }

  public boolean isMaxEnchant(int level) {
    int enchantType = getEnchantRoute(level);
    if ((enchantType < 1) || !enchantRoutes.containsKey(enchantType)) {
      return false;
    }
    int index = getEnchantIndex(level);

    return (index + 1)
        >= EnchantSkillGroupsData.getInstance()
            .getEnchantSkillGroupById(enchantRoutes.get(enchantType))
            .getEnchantGroupDetails()
            .size();
  }

  public EnchantSkillHolder getEnchantSkillHolder(int level) {
    int enchantType = getEnchantRoute(level);
    if ((enchantType < 1) || !enchantRoutes.containsKey(enchantType)) {
      return null;
    }
    int index = getEnchantIndex(level);
    L2EnchantSkillGroup group =
        EnchantSkillGroupsData.getInstance()
            .getEnchantSkillGroupById(enchantRoutes.get(enchantType));

    if (index < 0) {
      return group.getEnchantGroupDetails().get(0);
    } else if (index >= group.getEnchantGroupDetails().size()) {
      return group
          .getEnchantGroupDetails()
          .get(
              EnchantSkillGroupsData.getInstance()
                      .getEnchantSkillGroupById(enchantRoutes.get(enchantType))
                      .getEnchantGroupDetails()
                      .size()
                  - 1);
    }
    return group.getEnchantGroupDetails().get(index);
  }
}
