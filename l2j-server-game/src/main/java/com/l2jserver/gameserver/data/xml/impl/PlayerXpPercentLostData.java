package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.util.IXmlReader;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Arrays;

import static com.l2jserver.gameserver.config.Configuration.character;

@Service
public class PlayerXpPercentLostData implements IXmlReader {

	private final double[] _playerXpPercentLost = new double[character().getMaxPlayerLevel() + 1];
	
	protected PlayerXpPercentLostData() {
		Arrays.fill(_playerXpPercentLost, 1.);
		load();
	}
	
	@Override
	public void load() {
		parseDatapackFile("data/stats/chars/playerXpPercentLost.xml");
	}
	
	@Override
	public void parseDocument(Document doc) {
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
			if ("list".equalsIgnoreCase(n.getNodeName())) {
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
					if ("xpLost".equalsIgnoreCase(d.getNodeName())) {
						NamedNodeMap attrs = d.getAttributes();
						_playerXpPercentLost[parseInteger(attrs, "level")] = parseDouble(attrs, "val");
					}
				}
			}
		}
	}
	
	public double getXpPercent(final int level) {
		if (level > character().getMaxPlayerLevel()) {
			LOG.warn("Require to high level inside PlayerXpPercentLostData ({})", level);
			return _playerXpPercentLost[character().getMaxPlayerLevel()];
		}
		return _playerXpPercentLost[level];
	}
	
	/**
	 * Gets the single instance of PlayerXpPercentLostData.
	 * @return single instance of PlayerXpPercentLostData.
	 */
	public static PlayerXpPercentLostData getInstance() {
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder {
		protected static final PlayerXpPercentLostData _instance = new PlayerXpPercentLostData();
	}
}
