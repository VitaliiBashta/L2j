package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.fishing.L2FishingMonster;
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
public class FishingMonstersData implements IXmlReader {

	private static final Logger LOG = LoggerFactory.getLogger(FishingMonstersData.class);
	
	private final Map<Integer, L2FishingMonster> _fishingMonstersData = new HashMap<>();
	
	@Override
	public void load() {
		_fishingMonstersData.clear();
		parseDatapackFile("data/stats/fishing/fishingMonsters.xml");
		LOG.info("Loaded {} fishing monsters.", _fishingMonstersData.size());
	}
	
	@Override
	public void parseDocument(Document doc) {
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
			if ("list".equalsIgnoreCase(n.getNodeName())) {
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
					if ("fishingMonster".equalsIgnoreCase(d.getNodeName())) {
						
						final NamedNodeMap attrs = d.getAttributes();
						final StatsSet set = new StatsSet();
						for (int i = 0; i < attrs.getLength(); i++) {
							final Node att = attrs.item(i);
							set.set(att.getNodeName(), att.getNodeValue());
						}
						
						final L2FishingMonster fishingMonster = new L2FishingMonster(set);
						_fishingMonstersData.put(fishingMonster.getFishingMonsterId(), fishingMonster);
					}
				}
			}
		}
	}
	
	/**
	 * Gets the fishing monster.
	 * @param lvl the fisherman level
	 * @return a fishing monster given the fisherman level
	 */
	public L2FishingMonster getFishingMonster(int lvl) {
		for (L2FishingMonster fishingMonster : _fishingMonstersData.values()) {
			if ((lvl >= fishingMonster.getUserMinLevel()) && (lvl <= fishingMonster.getUserMaxLevel())) {
				return fishingMonster;
			}
		}
		return null;
	}
	
	/**
	 * Gets the fishing monster by Id.
	 * @param id the fishing monster Id
	 * @return the fishing monster by Id
	 */
	public L2FishingMonster getFishingMonsterById(int id) {
		if (_fishingMonstersData.containsKey(id)) {
			return _fishingMonstersData.get(id);
		}
		return null;
	}
	
	public static FishingMonstersData getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		protected static final FishingMonstersData INSTANCE = new FishingMonstersData();
	}
}
