package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.items.enchant.EnchantScroll;
import com.l2jserver.gameserver.model.items.enchant.EnchantSupportItem;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
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
public class EnchantItemData extends IXmlReader {

	private static final Logger LOG = LoggerFactory.getLogger(EnchantItemData.class);
	
	private final Map<Integer, EnchantScroll> _scrolls = new HashMap<>();
	
	private final Map<Integer, EnchantSupportItem> _supports = new HashMap<>();
  private final ItemTable itemTable;

  public EnchantItemData(ItemTable itemTable) {
    this.itemTable = itemTable;
	}
	
	@Override
	public synchronized void load() {
		_scrolls.clear();
		_supports.clear();
		parseDatapackFile("data/enchantItemData.xml");
		LOG.info("Loaded {} enchant scrolls.", _scrolls.size());
		LOG.info("Loaded {} support items.", _supports.size());
	}
	
	@Override
	public void parseDocument(Document doc) {
		StatsSet set;
		Node att;
		NamedNodeMap attrs;
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
			if ("list".equalsIgnoreCase(n.getNodeName())) {
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
					if ("enchant".equalsIgnoreCase(d.getNodeName())) {
						attrs = d.getAttributes();
						set = new StatsSet();
						for (int i = 0; i < attrs.getLength(); i++) {
							att = attrs.item(i);
							set.set(att.getNodeName(), att.getNodeValue());
						}
						
						try {
              final EnchantScroll item =
                  new EnchantScroll(set, itemTable.getTemplate(set.getInt("id")));
							for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
								if ("item".equalsIgnoreCase(cd.getNodeName())) {
									item.addItem(parseInteger(cd.getAttributes(), "id"));
								}
							}
							_scrolls.put(item.getId(), item);
						} catch (NullPointerException e) {
							LOG.warn("Nonexistent enchant scroll: {} defined in enchant data!", set.getString("id"));
						} catch (IllegalAccessError e) {
							LOG.warn("Wrong enchant scroll item type: {} defined in enchant data!", set.getString("id"));
						}
					} else if ("support".equalsIgnoreCase(d.getNodeName())) {
						attrs = d.getAttributes();
						set = new StatsSet();
						for (int i = 0; i < attrs.getLength(); i++) {
							att = attrs.item(i);
							set.set(att.getNodeName(), att.getNodeValue());
						}
						
						try {
              var item = new EnchantSupportItem(set, itemTable.getTemplate(set.getInt("id")));
							_supports.put(item.getId(), item);
						} catch (NullPointerException e) {
							LOG.warn("Nonexistent enchant support item: {} defined in enchant data!", set.getString("id"));
						} catch (IllegalAccessError e) {
							LOG.warn("Wrong enchant support item type: {} defined in enchant data!", set.getString("id"));
						}
					}
				}
			}
		}
	}
	
	/**
	 * Gets the enchant scroll.
	 * @param scroll the scroll
	 * @return enchant template for scroll
	 */
	public final EnchantScroll getEnchantScroll(L2ItemInstance scroll) {
		return _scrolls.get(scroll.getId());
	}
	
	/**
	 * Gets the support item.
	 * @param item the item
	 * @return enchant template for support item
	 */
	public final EnchantSupportItem getSupportItem(L2ItemInstance item) {
		return _supports.get(item.getId());
	}
	
	/**
	 * Gets the single instance of EnchantItemData.
	 * @return single instance of EnchantItemData
	 */
	public static EnchantItemData getInstance() {
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder {
    protected static final EnchantItemData _instance = new EnchantItemData(null);
	}
}
