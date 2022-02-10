package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.templates.L2PcTemplate;
import com.l2jserver.gameserver.model.base.ClassId;
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
public class PlayerTemplateData extends IXmlReader {

	private static final Logger LOG = LoggerFactory.getLogger(PlayerTemplateData.class);
	
	private final Map<ClassId, L2PcTemplate> _playerTemplates = new HashMap<>();
	
	private int _dataCount = 0;
	

	@Override
	public void load() {
		_playerTemplates.clear();
		parseDatapackDirectory("data/stats/chars/baseStats");
		LOG.info("Loaded {} character templates.", _playerTemplates.size());
		LOG.info("Loaded {} level up gain records.", _dataCount);
	}
	
	@Override
	public void parseDocument(Document doc) {
		NamedNodeMap attrs;
		int classId = 0;
		
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
			if ("list".equalsIgnoreCase(n.getNodeName())) {
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
					if ("classId".equalsIgnoreCase(d.getNodeName())) {
						classId = Integer.parseInt(d.getTextContent());
					} else if ("staticData".equalsIgnoreCase(d.getNodeName())) {
						StatsSet set = new StatsSet();
						set.set("classId", classId);
						for (Node nd = d.getFirstChild(); nd != null; nd = nd.getNextSibling()) {
							// Skip odd nodes
							if (nd.getNodeName().equals("#text")) {
								continue;
							}
							
							if (nd.getChildNodes().getLength() > 1) {
								for (Node cnd = nd.getFirstChild(); cnd != null; cnd = cnd.getNextSibling()) {
									// use L2CharTemplate(superclass) fields for male collision height and collision radius
									if (nd.getNodeName().equalsIgnoreCase("collisionMale")) {
										if (cnd.getNodeName().equalsIgnoreCase("radius")) {
											set.set("collisionRadius", cnd.getTextContent());
										} else if (cnd.getNodeName().equalsIgnoreCase("height")) {
											set.set("collisionHeight", cnd.getTextContent());
										}
									}
									if ("walk".equalsIgnoreCase(cnd.getNodeName())) {
										set.set("baseWalkSpd", cnd.getTextContent());
									} else if ("run".equalsIgnoreCase(cnd.getNodeName())) {
										set.set("baseRunSpd", cnd.getTextContent());
									} else if ("slowSwim".equals(cnd.getNodeName())) {
										set.set("baseSwimWalkSpd", cnd.getTextContent());
									} else if ("fastSwim".equals(cnd.getNodeName())) {
										set.set("baseSwimRunSpd", cnd.getTextContent());
									} else if (!cnd.getNodeName().equals("#text")) {
										set.set((nd.getNodeName() + cnd.getNodeName()), cnd.getTextContent());
									}
								}
							} else {
								set.set(nd.getNodeName(), nd.getTextContent());
							}
						}
						// calculate total pdef and mdef from parts
						set.set("basePDef", (set.getInt("basePDefchest", 0) + set.getInt("basePDeflegs", 0) + set.getInt("basePDefhead", 0) + set.getInt("basePDeffeet", 0) + set.getInt("basePDefgloves", 0) + set.getInt("basePDefunderwear", 0) + set.getInt("basePDefcloak", 0)));
						set.set("baseMDef", (set.getInt("baseMDefrear", 0) + set.getInt("baseMDeflear", 0) + set.getInt("baseMDefrfinger", 0) + set.getInt("baseMDefrfinger", 0) + set.getInt("baseMDefneck", 0)));
						
						_playerTemplates.put(ClassId.getClassId(classId), new L2PcTemplate(set));
					} else if ("lvlUpgainData".equalsIgnoreCase(d.getNodeName())) {
						for (Node lvlNode = d.getFirstChild(); lvlNode != null; lvlNode = lvlNode.getNextSibling()) {
							if ("level".equalsIgnoreCase(lvlNode.getNodeName())) {
								attrs = lvlNode.getAttributes();
								int level = parseInteger(attrs, "val");
								
								for (Node valNode = lvlNode.getFirstChild(); valNode != null; valNode = valNode.getNextSibling()) {
									String nodeName = valNode.getNodeName();
									
									if ((nodeName.startsWith("hp") || nodeName.startsWith("mp") || nodeName.startsWith("cp")) && _playerTemplates.containsKey(ClassId.getClassId(classId))) {
										_playerTemplates.get(ClassId.getClassId(classId)).setUpgainValue(nodeName, level, Double.parseDouble(valNode.getTextContent()));
										_dataCount++;
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public L2PcTemplate getTemplate(ClassId classId) {
		return _playerTemplates.get(classId);
	}
	
	public L2PcTemplate getTemplate(int classId) {
		return _playerTemplates.get(ClassId.getClassId(classId));
	}
	
	public static PlayerTemplateData getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		protected static final PlayerTemplateData INSTANCE = new PlayerTemplateData();
	}
}
