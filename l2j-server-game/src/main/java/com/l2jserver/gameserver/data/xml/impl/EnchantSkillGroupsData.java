package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.model.L2EnchantSkillGroup;
import com.l2jserver.gameserver.model.L2EnchantSkillGroup.EnchantSkillHolder;
import com.l2jserver.gameserver.model.L2EnchantSkillLearn;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.util.IXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

@Service
public class EnchantSkillGroupsData implements IXmlReader {

  public static final int NORMAL_ENCHANT_BOOK = 6622;
  public static final int SAFE_ENCHANT_BOOK = 9627;
  public static final int CHANGE_ENCHANT_BOOK = 9626;
  public static final int UNTRAIN_ENCHANT_BOOK = 9625;
  private static final Logger LOG = LoggerFactory.getLogger(EnchantSkillGroupsData.class);
  private final Map<Integer, L2EnchantSkillGroup> enchantSkillGroups = new HashMap<>();

  private final Map<Integer, L2EnchantSkillLearn> enchantSkillTrees = new HashMap<>();

  public static EnchantSkillGroupsData getInstance() {
    return SingletonHolder.INSTANCE;
  }

  @Override
  public void load() {
    enchantSkillGroups.clear();
    enchantSkillTrees.clear();
    parseDatapackFile("data/enchantSkillGroups.xml");
    int routes = 0;
    for (L2EnchantSkillGroup group : enchantSkillGroups.values()) {
      routes += group.getEnchantGroupDetails().size();
    }
    LOG.info("Loaded {} groups and {} routes.", enchantSkillGroups.size(), routes);
  }

  @Override
  public void parseDocument(Document doc) {
    for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
      if ("list".equalsIgnoreCase(n.getNodeName())) {
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
          if ("group".equalsIgnoreCase(d.getNodeName())) {
            NamedNodeMap attrs = d.getAttributes();
            final int id = parseInteger(attrs, "id");
            L2EnchantSkillGroup group = enchantSkillGroups.get(id);
            if (group == null) {
              group = new L2EnchantSkillGroup(id);
              enchantSkillGroups.put(id, group);
            }

            for (Node b = d.getFirstChild(); b != null; b = b.getNextSibling()) {
              if ("enchant".equalsIgnoreCase(b.getNodeName())) {
                attrs = b.getAttributes();
                StatsSet set = new StatsSet();

                for (int i = 0; i < attrs.getLength(); i++) {
                  Node att = attrs.item(i);
                  set.set(att.getNodeName(), att.getNodeValue());
                }
                group.addEnchantDetail(new EnchantSkillHolder(set));
              }
            }
          }
        }
      }
    }
  }

  public int addNewRouteForSkill(int skillId, int maxLvL, int route, int group) {
    L2EnchantSkillLearn enchantableSkill = enchantSkillTrees.get(skillId);
    if (enchantableSkill == null) {
      enchantableSkill = new L2EnchantSkillLearn(skillId, maxLvL);
      enchantSkillTrees.put(skillId, enchantableSkill);
    }
    if (enchantSkillGroups.containsKey(group)) {
      enchantableSkill.addNewEnchantRoute(route, group);

      return enchantSkillGroups.get(group).getEnchantGroupDetails().size();
    }
    LOG.error(
        "There has been an error while loading generating enchant skill Id {}, route {} and group {}!",
        skillId,
        route,
        group);
    return 0;
  }

  /**
   * Gets the skill enchantment for skill.
   *
   * @param skill the skill
   * @return the skill enchantment for skill
   */
  public L2EnchantSkillLearn getSkillEnchantmentForSkill(Skill skill) {
    // there is enchantment for this skill and we have the required level of it
    final L2EnchantSkillLearn esl = getSkillEnchantmentBySkillId(skill.getId());
    if ((esl != null) && (skill.getLevel() >= esl.getBaseLevel())) {
      return esl;
    }
    return null;
  }

  /**
   * Gets the skill enchantment by skill id.
   *
   * @param skillId the skill id
   * @return the skill enchantment by skill id
   */
  public L2EnchantSkillLearn getSkillEnchantmentBySkillId(int skillId) {
    return enchantSkillTrees.get(skillId);
  }

  /**
   * Gets the enchant skill group by id.
   *
   * @param id the id
   * @return the enchant skill group by id
   */
  public L2EnchantSkillGroup getEnchantSkillGroupById(int id) {
    return enchantSkillGroups.get(id);
  }

  /**
   * Gets the enchant skill sp cost.
   *
   * @param skill the skill
   * @return the enchant skill sp cost
   */
  public int getEnchantSkillSpCost(Skill skill) {
    final L2EnchantSkillLearn enchantSkillLearn = enchantSkillTrees.get(skill.getId());
    if (enchantSkillLearn != null) {
      final EnchantSkillHolder esh = enchantSkillLearn.getEnchantSkillHolder(skill.getLevel());
      if (esh != null) {
        return esh.getSpCost();
      }
    }
    return Integer.MAX_VALUE;
  }

  /**
   * Gets the enchant skill Adena cost.
   *
   * @param skill the skill
   * @return the enchant skill Adena cost
   */
  public int getEnchantSkillAdenaCost(Skill skill) {
    final L2EnchantSkillLearn enchantSkillLearn = enchantSkillTrees.get(skill.getId());
    if (enchantSkillLearn != null) {
      final EnchantSkillHolder esh = enchantSkillLearn.getEnchantSkillHolder(skill.getLevel());
      if (esh != null) {
        return esh.getAdenaCost();
      }
    }
    return Integer.MAX_VALUE;
  }

  /**
   * Gets the enchant skill rate.
   *
   * @param player the player
   * @param skill the skill
   * @return the enchant skill rate
   */
  public byte getEnchantSkillRate(L2PcInstance player, Skill skill) {
    final L2EnchantSkillLearn enchantSkillLearn = enchantSkillTrees.get(skill.getId());
    if (enchantSkillLearn != null) {
      final EnchantSkillHolder esh = enchantSkillLearn.getEnchantSkillHolder(skill.getLevel());
      if (esh != null) {
        return esh.getRate(player);
      }
    }
    return 0;
  }

  private static class SingletonHolder {
    protected static final EnchantSkillGroupsData INSTANCE = new EnchantSkillGroupsData();
  }
}
