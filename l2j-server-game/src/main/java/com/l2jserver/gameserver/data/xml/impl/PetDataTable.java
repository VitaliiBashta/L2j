package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.enums.MountType;
import com.l2jserver.gameserver.model.L2PetData;
import com.l2jserver.gameserver.model.L2PetLevelData;
import com.l2jserver.gameserver.model.StatsSet;
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
public class PetDataTable extends IXmlReader {

	private static final Logger LOG = LoggerFactory.getLogger(PetDataTable.class);

  private final Map<Integer, L2PetData> pets = new HashMap<>();

	@Override
	public void load() {
    pets.clear();
		parseDatapackDirectory("data/stats/pets");
    LOG.info("Loaded {} Pets.", pets.size());
	}
	
	@Override
	public void parseDocument(Document doc) {
		NamedNodeMap attrs;
		Node n = doc.getFirstChild();
		for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
			if (d.getNodeName().equals("pet")) {
				int npcId = parseInteger(d.getAttributes(), "id");
				int itemId = parseInteger(d.getAttributes(), "itemId");
				// index ignored for now
				L2PetData data = new L2PetData(npcId, itemId);
				for (Node p = d.getFirstChild(); p != null; p = p.getNextSibling()) {
					if (p.getNodeName().equals("set")) {
						attrs = p.getAttributes();
						String type = attrs.getNamedItem("name").getNodeValue();
						if ("food".equals(type)) {
							for (String foodId : attrs.getNamedItem("val").getNodeValue().split(";")) {
								data.addFood(Integer.valueOf(foodId));
							}
						} else if ("hungry_limit".equals(type)) {
							data.setHungryLimit(parseInteger(attrs, "val"));
						} else if ("sync_level".equals(type)) {
							data.setSyncLevel(parseInteger(attrs, "val") == 1);
						}
						// evolve ignored
					} else if (p.getNodeName().equals("skills")) {
						for (Node s = p.getFirstChild(); s != null; s = s.getNextSibling()) {
							if (s.getNodeName().equals("skill")) {
								attrs = s.getAttributes();
								data.addNewSkill(parseInteger(attrs, "skillId"), parseInteger(attrs, "skillLvl"), parseInteger(attrs, "minLvl"));
							}
						}
					} else if (p.getNodeName().equals("stats")) {
						for (Node s = p.getFirstChild(); s != null; s = s.getNextSibling()) {
							if (s.getNodeName().equals("stat")) {
								final int level = Integer.parseInt(s.getAttributes().getNamedItem("level").getNodeValue());
								final StatsSet set = new StatsSet();
								for (Node bean = s.getFirstChild(); bean != null; bean = bean.getNextSibling()) {
									if (bean.getNodeName().equals("set")) {
										attrs = bean.getAttributes();
										if (attrs.getNamedItem("name").getNodeValue().equals("speed_on_ride")) {
											set.set("walkSpeedOnRide", attrs.getNamedItem("walk").getNodeValue());
											set.set("runSpeedOnRide", attrs.getNamedItem("run").getNodeValue());
											set.set("slowSwimSpeedOnRide", attrs.getNamedItem("slowSwim").getNodeValue());
											set.set("fastSwimSpeedOnRide", attrs.getNamedItem("fastSwim").getNodeValue());
											if (attrs.getNamedItem("slowFly") != null) {
												set.set("slowFlySpeedOnRide", attrs.getNamedItem("slowFly").getNodeValue());
											}
											if (attrs.getNamedItem("fastFly") != null) {
												set.set("fastFlySpeedOnRide", attrs.getNamedItem("fastFly").getNodeValue());
											}
										} else {
											set.set(attrs.getNamedItem("name").getNodeValue(), attrs.getNamedItem("val").getNodeValue());
										}
									}
								}
								data.addNewStat(level, new L2PetLevelData(set));
							}
						}
					}
				}
        pets.put(npcId, data);
			}
		}
	}
	
	public L2PetData getPetDataByItemId(int itemId) {
    for (L2PetData data : pets.values()) {
			if (data.getItemId() == itemId) {
				return data;
			}
		}
		return null;
	}
	
	/**
	 * Gets the pet level data.
	 * @param petId the pet Id.
	 * @param petLevel the pet level.
	 * @return the pet's parameters for the given Id and level.
	 */
	public L2PetLevelData getPetLevelData(int petId, int petLevel) {
		final L2PetData pd = getPetData(petId);
		if (pd != null) {
			return pd.getPetLevelData(petLevel);
		}
		return null;
	}
	
	/**
	 * Gets the pet data.
	 * @param petId the pet Id.
	 * @return the pet data
	 */
	public L2PetData getPetData(int petId) {
    if (!pets.containsKey(petId)) {
			LOG.warn("Missing pet data for NPC Id {}!", petId);
		}
    return pets.get(petId);
	}
	
	/**
	 * Gets the pet min level.
	 * @param petId the pet Id.
	 * @return the pet min level
	 */
	public int getPetMinLevel(int petId) {
    return pets.get(petId).getMinLevel();
	}
	
	/**
	 * Gets the pet items by npc.
	 * @param npcId the NPC ID to get its summoning item
	 * @return summoning item for the given NPC ID
	 */
	public int getPetItemsByNpc(int npcId) {
    return pets.get(npcId).getItemId();
	}
	
	/**
	 * Checks if is mountable.
	 * @param npcId the NPC Id to verify.
	 * @return {@code true} if the given Id is from a mountable pet, {@code false} otherwise.
	 */
	public static boolean isMountable(int npcId) {
		return MountType.findByNpcId(npcId) != MountType.NONE;
	}
	
	public static PetDataTable getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		protected static final PetDataTable INSTANCE = new PetDataTable();
	}
}