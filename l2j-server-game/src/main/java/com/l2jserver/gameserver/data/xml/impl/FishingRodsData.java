package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.fishing.L2FishingRod;
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
public final class FishingRodsData extends IXmlReader {

	private static final Logger LOG = LoggerFactory.getLogger(FishingRodsData.class);

  private final Map<Integer, L2FishingRod> fishingRods = new HashMap<>();

	@Override
	public void load() {
    fishingRods.clear();
		parseDatapackFile("data/stats/fishing/fishingRods.xml");
    LOG.info("Loaded {} fishing rods.", fishingRods.size());
	}
	
	@Override
	public void parseDocument(Document doc) {
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
			if ("list".equalsIgnoreCase(n.getNodeName())) {
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
					if ("fishingRod".equalsIgnoreCase(d.getNodeName())) {
						final NamedNodeMap attrs = d.getAttributes();
						final StatsSet set = new StatsSet();
						for (int i = 0; i < attrs.getLength(); i++) {
							final Node att = attrs.item(i);
							set.set(att.getNodeName(), att.getNodeValue());
						}
						
						final L2FishingRod fishingRod = new L2FishingRod(set);
            fishingRods.put(fishingRod.getFishingRodItemId(), fishingRod);
					}
				}
			}
		}
	}
	
	/**
	 * Gets the fishing rod.
	 * @param itemId the item id
	 * @return A fishing Rod by Item Id
	 */
	public L2FishingRod getFishingRod(int itemId) {
    return fishingRods.get(itemId);
	}
	
	public static FishingRodsData getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		protected static final FishingRodsData INSTANCE = new FishingRodsData();
	}
}
