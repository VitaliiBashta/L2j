package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.model.ActionKey;
import com.l2jserver.gameserver.util.IXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UIData extends IXmlReader {
	
	private static final Logger LOG = LoggerFactory.getLogger(UIData.class);
	
	private final Map<Integer, List<ActionKey>> _storedKeys = new HashMap<>();
	
	private final Map<Integer, List<Integer>> _storedCategories = new HashMap<>();
	

	/**
	 * Add a category to the stored categories.
	 * @param map the map to store the category
	 * @param cat the category
	 * @param cmd the command
	 */
	public static void addCategory(Map<Integer, List<Integer>> map, int cat, int cmd) {
		map.computeIfAbsent(cat, k -> new ArrayList<>()).add(cmd);
	}
	
	/**
	 * Create and insert an Action Key into the stored keys.
	 * @param map the map to store the key
	 * @param cat the category
	 * @param akey the action key
	 */
	public static void addKey(Map<Integer, List<ActionKey>> map, int cat, ActionKey akey) {
		map.computeIfAbsent(cat, k -> new ArrayList<>()).add(akey);
	}
	
	public static UIData getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	@Override
	public void load() {
		_storedKeys.clear();
		_storedCategories.clear();
		parseDatapackFile("data/ui/ui_en.xml");
		LOG.info("Loaded {} keys {} categories.", _storedKeys.size(), _storedCategories.size());
	}
	
	@Override
	public void parseDocument(Document doc) {
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
			if ("list".equalsIgnoreCase(n.getNodeName())) {
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
					if ("category".equalsIgnoreCase(d.getNodeName())) {
						parseCategory(d);
					}
				}
			}
		}
	}
	
	private void parseCategory(Node n) {
		final int cat = parseInteger(n.getAttributes(), "id");
		for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
			if ("commands".equalsIgnoreCase(d.getNodeName())) {
				parseCommands(cat, d);
			} else if ("keys".equalsIgnoreCase(d.getNodeName())) {
				parseKeys(cat, d);
			}
		}
	}
	
	private void parseCommands(int cat, Node d) {
		for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
			if ("cmd".equalsIgnoreCase(c.getNodeName())) {
				addCategory(_storedCategories, cat, Integer.parseInt(c.getTextContent()));
			}
		}
	}
	
	private void parseKeys(int cat, Node d) {
		for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
			if ("key".equalsIgnoreCase(c.getNodeName())) {
				final ActionKey akey = new ActionKey(cat);
				for (int i = 0; i < c.getAttributes().getLength(); i++) {
					final Node att = c.getAttributes().item(i);
					final int val = Integer.parseInt(att.getNodeValue());
					switch (att.getNodeName()) {
						case "cmd" -> akey.setCommandId(val);
						case "key" -> akey.setKeyId(val);
						case "toggleKey1" -> akey.setToggleKey1(val);
						case "toggleKey2" -> akey.setToggleKey2(val);
						case "showType" -> akey.setShowStatus(val);
					}
				}
				addKey(_storedKeys, cat, akey);
			}
		}
	}
	
	/**
	 * @return the categories
	 */
	public Map<Integer, List<Integer>> getCategories() {
		return _storedCategories;
	}
	
	/**
	 * @return the keys
	 */
	public Map<Integer, List<ActionKey>> getKeys() {
		return _storedKeys;
	}
	
	private static class SingletonHolder {
		protected static final UIData INSTANCE = new UIData();
	}
}
