package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.options.EnchantOptions;
import com.l2jserver.gameserver.util.IXmlReader;
import com.l2jserver.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

@Service
public class EnchantItemOptionsData extends IXmlReader {

	private static final Logger LOG = LoggerFactory.getLogger(EnchantItemOptionsData.class);

  private final Map<Integer, Map<Integer, EnchantOptions>> data = new HashMap<>();

	@Override
	public synchronized void load() {
    data.clear();
		parseDatapackFile("data/enchantItemOptions.xml");
	}
	
	@Override
	public void parseDocument(Document doc) {
		int counter = 0;
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
			if ("list".equalsIgnoreCase(n.getNodeName())) {
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
					if ("item".equalsIgnoreCase(d.getNodeName())) {
						int itemId = parseInteger(d.getAttributes(), "id");
            if (!data.containsKey(itemId)) {
              data.put(itemId, new HashMap<>());
						}
						for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
							if ("options".equalsIgnoreCase(cd.getNodeName())) {
								final EnchantOptions op = new EnchantOptions(parseInteger(cd.getAttributes(), "level"));
                data.get(itemId).put(op.getLevel(), op);

								for (byte i = 0; i < 3; i++) {
									final Node att = cd.getAttributes().getNamedItem("option" + (i + 1));
									if ((att != null) && Util.isDigit(att.getNodeValue())) {
										op.setOption(i, parseInteger(att));
									}
								}
								counter++;
							}
						}
					}
				}
			}
		}
    LOG.info("Loaded {} items and {} options.", data.size(), counter);
	}
	
	public EnchantOptions getOptions(int itemId, int enchantLevel) {
    if (!data.containsKey(itemId) || !data.get(itemId).containsKey(enchantLevel)) {
			return null;
		}
    return data.get(itemId).get(enchantLevel);
	}
	
	public EnchantOptions getOptions(L2ItemInstance item) {
		return item != null ? getOptions(item.getId(), item.getEnchantLevel()) : null;
	}
	
	public static EnchantItemOptionsData getInstance() {
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder {
		protected static final EnchantItemOptionsData _instance = new EnchantItemOptionsData();
	}
}
