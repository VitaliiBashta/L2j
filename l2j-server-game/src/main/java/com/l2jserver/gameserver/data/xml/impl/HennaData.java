package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.items.L2Henna;
import com.l2jserver.gameserver.util.IXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public  class HennaData implements IXmlReader {
	private static final Logger LOG = LoggerFactory.getLogger(HennaData.class);
	
	private final Map<Integer, L2Henna> hennaList = new HashMap<>();
	
	public static HennaData getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	@Override
	public void load() {
		parseDatapackFile("data/stats/hennaList.xml");
		LOG.info("Loaded {} Henna data.", hennaList.size());
	}
	
	@Override
	public void parseDocument(Document doc) {
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
			if ("list".equals(n.getNodeName())) {
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
					if ("henna".equals(d.getNodeName())) {
						parseHenna(d);
					}
				}
			}
		}
	}
	
	/**
	 * Parses the henna.
	 * @param d the d
	 */
	private void parseHenna(Node d) {
		final StatsSet set = new StatsSet();
		final List<ClassId> wearClassIds = new ArrayList<>();
		NamedNodeMap attrs = d.getAttributes();
		Node attr;
		for (int i = 0; i < attrs.getLength(); i++) {
			attr = attrs.item(i);
			set.set(attr.getNodeName(), attr.getNodeValue());
		}

		for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
			final String name = c.getNodeName();
			attrs = c.getAttributes();
			switch (name) {
				case "stats" -> {
					for (int i = 0; i < attrs.getLength(); i++) {
						attr = attrs.item(i);
						set.set(attr.getNodeName(), attr.getNodeValue());
					}
				}
				case "wear" -> {
					attr = attrs.getNamedItem("count");
					set.set("wear_count", attr.getNodeValue());
					attr = attrs.getNamedItem("fee");
					set.set("wear_fee", attr.getNodeValue());
				}
				case "cancel" -> {
					attr = attrs.getNamedItem("count");
					set.set("cancel_count", attr.getNodeValue());
					attr = attrs.getNamedItem("fee");
					set.set("cancel_fee", attr.getNodeValue());
				}
				case "classId" -> wearClassIds.add(ClassId.getClassId(Integer.parseInt(c.getTextContent())));
			}
		}
		final L2Henna henna = new L2Henna(set);
		henna.setWearClassIds(wearClassIds);
		hennaList.put(henna.getDyeId(), henna);
	}
	
	/**
	 * Gets the henna.
	 * @param id of the dye.
	 * @return the dye with that id.
	 */
	public L2Henna getHenna(int id) {
		return hennaList.get(id);
	}
	
	/**
	 * Gets the henna list.
	 * @param classId the player's class Id.
	 * @return the list with all the allowed dyes.
	 */
	public List<L2Henna> getHennaList(ClassId classId) {
		final List<L2Henna> list = new ArrayList<>();
		for (L2Henna henna : hennaList.values()) {
			if (henna.isAllowedClass(classId)) {
				list.add(henna);
			}
		}
		return list;
	}
	
	private static class SingletonHolder {
		protected static final HennaData INSTANCE = new HennaData();
	}
}