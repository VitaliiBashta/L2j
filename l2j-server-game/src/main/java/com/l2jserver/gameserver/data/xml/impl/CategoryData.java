package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.enums.CategoryType;
import com.l2jserver.gameserver.util.IXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class CategoryData extends IXmlReader {

  private static final Logger LOG = LoggerFactory.getLogger(CategoryData.class);

  private final Map<CategoryType, Set<Integer>> _categories = new HashMap<>();

  public static CategoryData getInstance() {
    return SingletonHolder.INSTANCE;
  }

  @Override
  public void load() {
    _categories.clear();
    parseDatapackFile("data/categoryData.xml");
    LOG.info("Loaded {} Categories.", _categories.size());
  }

  @Override
  public void parseDocument(Document doc) {
    for (Node node = doc.getFirstChild(); node != null; node = node.getNextSibling()) {
      if ("list".equalsIgnoreCase(node.getNodeName())) {
        for (Node list_node = node.getFirstChild();
            list_node != null;
            list_node = list_node.getNextSibling()) {
          if ("category".equalsIgnoreCase(list_node.getNodeName())) {
            final NamedNodeMap attrs = list_node.getAttributes();
            final CategoryType categoryType =
                CategoryType.findByName(attrs.getNamedItem("name").getNodeValue());
            if (categoryType == null) {
              LOG.warn(
                  "Can't find category by name :{}", attrs.getNamedItem("name").getNodeValue());
              continue;
            }

            final Set<Integer> ids = new HashSet<>();
            for (Node category_node = list_node.getFirstChild();
                category_node != null;
                category_node = category_node.getNextSibling()) {
              if ("id".equalsIgnoreCase(category_node.getNodeName())) {
                ids.add(Integer.parseInt(category_node.getTextContent()));
              }
            }
            _categories.put(categoryType, ids);
          }
        }
      }
    }
  }

  /**
   * Checks if ID is in category.
   *
   * @param type The category type
   * @param id The id to be checked
   * @return {@code true} if id is in category, {@code false} if id is not in category or category
   *     was not found
   */
  public boolean isInCategory(CategoryType type, int id) {
    final Set<Integer> category = getCategoryByType(type);
    if (category == null) {
      LOG.warn("Can not find category type {}!", type);
      return false;
    }
    return category.contains(id);
  }

  /**
   * Gets the category by category type.
   *
   * @param type The category type
   * @return A {@code Set} containing all the IDs in category if category is found, {@code null} if
   *     category was not found
   */
  public Set<Integer> getCategoryByType(CategoryType type) {
    return _categories.get(type);
  }

  private static class SingletonHolder {
    protected static final CategoryData INSTANCE = new CategoryData();
  }
}
