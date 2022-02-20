package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.L2SkillLearn;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.AcquireSkillType;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.base.SocialClass;
import com.l2jserver.gameserver.model.base.SubClass;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.holders.PlayerSkillHolder;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.interfaces.ISkillsHolder;
import com.l2jserver.gameserver.model.skills.CommonSkill;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.util.IXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.l2jserver.gameserver.config.Configuration.character;

/**
 * This class loads and manage the characters and pledges skills trees.<br>
 * Here can be found the following skill trees:<br>
 * <ul>
 * <li>Class skill trees: player skill trees for each class.</li>
 * <li>Transfer skill trees: player skill trees for each healer class.</lI>
 * <li>Collect skill tree: player skill tree for Gracia related skills.</li>
 * <li>Fishing skill tree: player skill tree for fishing related skills.</li>
 * <li>Transform skill tree: player skill tree for transformation related skills.</li>
 * <li>Sub-Class skill tree: player skill tree for sub-class related skills.</li>
 * <li>Noble skill tree: player skill tree for noblesse related skills.</li>
 * <li>Hero skill tree: player skill tree for heroes related skills.</li>
 * <li>GM skill tree: player skill tree for Game Master related skills.</li>
 * <li>Common skill tree: custom skill tree for players, skills in this skill tree will be available for all players.</li>
 * <li>Pledge skill tree: clan skill tree for main clan.</li>
 * <li>Sub-Pledge skill tree: clan skill tree for sub-clans.</li>
 * </ul>
 * For easy customization of player class skill trees, the parent Id of each class is taken from the XML data, this means you can use a different class parent Id than in the normal game play, for example all 3rd class dagger users will have Treasure Hunter skills as 1st and 2nd class skills.<br>
 * For XML schema please refer to skillTrees.xsd in datapack in xsd folder and for parameters documentation refer to documentation.txt in skillTrees folder.<br>
 */
@Service
public class SkillTreesData extends IXmlReader {
	
	private static final Logger LOG = LoggerFactory.getLogger(SkillTreesData.class);
	
	// ClassId, Map of Skill Hash Code, L2SkillLearn
	private final Map<ClassId, Map<Integer, L2SkillLearn>> classSkillTrees = new LinkedHashMap<>();
	private final Map<ClassId, Map<Integer, L2SkillLearn>> transferSkillTrees = new LinkedHashMap<>();
	// Skill Hash Code, L2SkillLearn
	private final Map<Integer, L2SkillLearn> collectSkillTree = new LinkedHashMap<>();
	private final Map<Integer, L2SkillLearn> fishingSkillTree = new LinkedHashMap<>();
	private final Map<Integer, L2SkillLearn> pledgeSkillTree = new LinkedHashMap<>();
	private final Map<Integer, L2SkillLearn> subClassSkillTree = new LinkedHashMap<>();
	private final Map<Integer, L2SkillLearn> subPledgeSkillTree = new LinkedHashMap<>();
	private final Map<Integer, L2SkillLearn> transformSkillTree = new LinkedHashMap<>();
	private final Map<Integer, L2SkillLearn> commonSkillTree = new LinkedHashMap<>();
	// Other skill trees
	private final Map<Integer, L2SkillLearn> nobleSkillTree = new LinkedHashMap<>();
	private final Map<Integer, L2SkillLearn> heroSkillTree = new LinkedHashMap<>();
	private final Map<Integer, L2SkillLearn> gameMasterSkillTree = new LinkedHashMap<>();
	private final Map<Integer, L2SkillLearn> gameMasterAuraSkillTree = new LinkedHashMap<>();
	/** Parent class IDs are read from XML and stored in this map, to allow easy customization. */
	private final Map<ClassId, ClassId> parentClassMap = new LinkedHashMap<>();
	// Checker, sorted arrays of hash codes
	private Map<Integer, int[]> skillsByClassIdHashCodes; // Occupation skills
	private Map<Integer, int[]> skillsByRaceHashCodes; // Race-specific Transformations
	private int[] allSkillsHashCodes; // Fishing, Collection, Transformations, Common Skills.

	public static SkillTreesData getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	@Override
	public void load() {
		// Load files.
		parseDatapackDirectory("data/skillTrees/");

		// Generate check arrays.
		generateCheckArrays();

		// Logs a report with skill trees info.
		report();
	}
	
	/**
	 * Parse a skill tree file and store it into the correct skill tree.
	 */
	@Override
	public void parseDocument(Document doc) {
		int cId;
		ClassId classId = null;
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
			if ("list".equalsIgnoreCase(n.getNodeName())) {
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
					if ("skillTree".equalsIgnoreCase(d.getNodeName())) {
						final Map<Integer, L2SkillLearn> classSkillTree = new HashMap<>();
						final Map<Integer, L2SkillLearn> trasferSkillTree = new HashMap<>();
						final String type = d.getAttributes().getNamedItem("type").getNodeValue();
						Node attr = d.getAttributes().getNamedItem("classId");
						if (attr != null) {
							cId = Integer.parseInt(attr.getNodeValue());
							classId = ClassId.values()[cId];
						} else {
							cId = -1;
						}

						attr = d.getAttributes().getNamedItem("parentClassId");
						if (attr != null) {
							final int parentClassId = Integer.parseInt(attr.getNodeValue());
							if ((cId > -1) && (cId != parentClassId) && (parentClassId > -1) && !parentClassMap.containsKey(classId)) {
								parentClassMap.put(classId, ClassId.values()[parentClassId]);
							}
						}

						for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
							if ("skill".equalsIgnoreCase(c.getNodeName())) {
								final StatsSet learnSkillSet = new StatsSet();
								NamedNodeMap attrs = c.getAttributes();
								for (int i = 0; i < attrs.getLength(); i++) {
									attr = attrs.item(i);
									learnSkillSet.set(attr.getNodeName(), attr.getNodeValue());
								}

								final L2SkillLearn skillLearn = new L2SkillLearn(learnSkillSet);
								for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling()) {
									attrs = b.getAttributes();
									switch (b.getNodeName()) {
										case "item" -> skillLearn.addRequiredItem(new ItemHolder(parseInteger(attrs, "id"), parseInteger(attrs, "count")));
										case "preRequisiteSkill" -> skillLearn.addPreReqSkill(new SkillHolder(parseInteger(attrs, "id"), parseInteger(attrs, "lvl")));
										case "race" -> skillLearn.addRace(Race.valueOf(b.getTextContent()));
										case "residenceId" -> skillLearn.addResidenceId(Integer.valueOf(b.getTextContent()));
										case "socialClass" -> skillLearn.setSocialClass(Enum.valueOf(SocialClass.class, b.getTextContent()));
										case "subClassConditions" -> skillLearn.addSubclassConditions(parseInteger(attrs, "slot"), parseInteger(attrs, "lvl"));
									}
								}

								final int skillHashCode = SkillData.getSkillHashCode(skillLearn.getSkillId(), skillLearn.getSkillLevel());
								switch (type) {
									case "classSkillTree" -> {
										if (cId != -1) {
											classSkillTree.put(skillHashCode, skillLearn);
										} else {
											commonSkillTree.put(skillHashCode, skillLearn);
										}
									}
									case "transferSkillTree" -> trasferSkillTree.put(skillHashCode, skillLearn);
									case "collectSkillTree" -> collectSkillTree.put(skillHashCode, skillLearn);
									case "fishingSkillTree" -> fishingSkillTree.put(skillHashCode, skillLearn);
									case "pledgeSkillTree" -> pledgeSkillTree.put(skillHashCode, skillLearn);
									case "subClassSkillTree" -> subClassSkillTree.put(skillHashCode, skillLearn);
									case "subPledgeSkillTree" -> subPledgeSkillTree.put(skillHashCode, skillLearn);
									case "transformSkillTree" -> transformSkillTree.put(skillHashCode, skillLearn);
									case "nobleSkillTree" -> nobleSkillTree.put(skillHashCode, skillLearn);
									case "heroSkillTree" -> heroSkillTree.put(skillHashCode, skillLearn);
									case "gameMasterSkillTree" -> gameMasterSkillTree.put(skillHashCode, skillLearn);
									case "gameMasterAuraSkillTree" -> gameMasterAuraSkillTree.put(skillHashCode, skillLearn);
									default -> LOG.warn("Unknown Skill Tree type: {}!", type);
								}
							}
						}

						if (type.equals("transferSkillTree")) {
							transferSkillTrees.put(classId, trasferSkillTree);
						} else if (type.equals("classSkillTree") && (cId > -1)) {
							if (!classSkillTrees.containsKey(classId)) {
								classSkillTrees.put(classId, classSkillTree);
							} else {
								classSkillTrees.get(classId).putAll(classSkillTree);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Create and store hash values for skills for easy and fast checks.
	 */
	private void generateCheckArrays() {
		int i;
		int[] array;

		// Class specific skills:
		Map<Integer, L2SkillLearn> tempMap;
		final Set<ClassId> keySet = classSkillTrees.keySet();
		skillsByClassIdHashCodes = new HashMap<>(keySet.size());
		for (ClassId cls : keySet) {
			i = 0;
			tempMap = getCompleteClassSkillTree(cls);
			array = new int[tempMap.size()];
			for (int h : tempMap.keySet()) {
				array[i++] = h;
			}
			tempMap.clear();
			Arrays.sort(array);
			skillsByClassIdHashCodes.put(cls.ordinal(), array);
		}

		// Race specific skills from Fishing and Transformation skill trees.
		final List<Integer> list = new ArrayList<>();
		skillsByRaceHashCodes = new HashMap<>(Race.values().length);
		for (Race r : Race.values()) {
			for (L2SkillLearn s : fishingSkillTree.values()) {
				if (s.getRaces().contains(r)) {
					list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
				}
			}

			for (L2SkillLearn s : transformSkillTree.values()) {
				if (s.getRaces().contains(r)) {
					list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
				}
			}

			i = 0;
			array = new int[list.size()];
			for (int s : list) {
				array[i++] = s;
			}
			Arrays.sort(array);
			skillsByRaceHashCodes.put(r.ordinal(), array);
			list.clear();
		}

		// Skills available for all classes and races
		for (L2SkillLearn s : commonSkillTree.values()) {
			if (s.getRaces().isEmpty()) {
				list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
			}
		}

		for (L2SkillLearn s : fishingSkillTree.values()) {
			if (s.getRaces().isEmpty()) {
				list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
			}
		}

		for (L2SkillLearn s : transformSkillTree.values()) {
			if (s.getRaces().isEmpty()) {
				list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
			}
		}

		for (L2SkillLearn s : collectSkillTree.values()) {
			list.add(SkillData.getSkillHashCode(s.getSkillId(), s.getSkillLevel()));
		}

		allSkillsHashCodes = new int[list.size()];
		int j = 0;
		for (int hashcode : list) {
			allSkillsHashCodes[j++] = hashcode;
		}
		Arrays.sort(allSkillsHashCodes);
	}
	
	/**
	 * Method to get the complete skill tree for a given class id.<br>
	 * Include all skills common to all classes.<br>
	 * Includes all parent skill trees.
	 * @param classId the class skill tree Id
	 * @return the complete Class Skill Tree including skill trees from parent class for a given {@code classId}
	 */
	public Map<Integer, L2SkillLearn> getCompleteClassSkillTree(ClassId classId) {
		// Add all skills that belong to all classes.
		final Map<Integer, L2SkillLearn> skillTree = new LinkedHashMap<>(commonSkillTree);
		final LinkedList<ClassId> classSequence = new LinkedList<>();
		while (classId != null) {
			classSequence.addFirst(classId);
			classId = parentClassMap.get(classId);
		}

		for (ClassId cid : classSequence) {
			final Map<Integer, L2SkillLearn> classSkillTree = classSkillTrees.get(cid);
			if (classSkillTree != null) {
				skillTree.putAll(classSkillTree);
			}
		}
		return skillTree;
	}
	
	/**
	 * Logs current Skill Trees skills count.
	 */
	private void report() {
		int classSkillTreeCount = 0;
		for (Map<Integer, L2SkillLearn> classSkillTree : classSkillTrees.values()) {
			classSkillTreeCount += classSkillTree.size();
		}

		int trasferSkillTreeCount = 0;
		for (Map<Integer, L2SkillLearn> trasferSkillTree : transferSkillTrees.values()) {
			trasferSkillTreeCount += trasferSkillTree.size();
		}

		int dwarvenOnlyFishingSkillCount = 0;
		for (L2SkillLearn fishSkill : fishingSkillTree.values()) {
			if (fishSkill.getRaces().contains(Race.DWARF)) {
				dwarvenOnlyFishingSkillCount++;
			}
		}

		int resSkillCount = 0;
		for (L2SkillLearn pledgeSkill : pledgeSkillTree.values()) {
			if (pledgeSkill.isResidentialSkill()) {
				resSkillCount++;
			}
		}

		LOG.info("Loaded {} Class Skills for {} Class Skill Trees.", classSkillTreeCount, classSkillTrees.size());
		LOG.info("Loaded {} Sub-Class Skills.", subClassSkillTree.size());
		LOG.info("Loaded {} Transfer Skills for {} Transfer Skill Trees.", trasferSkillTreeCount, transferSkillTrees.size());
		LOG.info("Loaded {} Fishing Skills, {} Dwarven only Fishing Skills.", fishingSkillTree.size(), dwarvenOnlyFishingSkillCount);
		LOG.info("Loaded {} Collect Skills.", collectSkillTree.size());
		LOG.info("Loaded {} Pledge Skills, {} for Pledge and {} Residential.", pledgeSkillTree.size(), (pledgeSkillTree.size() - resSkillCount), resSkillCount);
		LOG.info("Loaded {} Sub-Pledge Skills.", subPledgeSkillTree.size());
		LOG.info("Loaded {} Transform Skills.", transformSkillTree.size());
		LOG.info("Loaded {} Noble Skills.", nobleSkillTree.size());
		LOG.info("Loaded {} Hero Skills.", heroSkillTree.size());
		LOG.info("Loaded {} Game Master Skills.", gameMasterSkillTree.size());
		LOG.info("Loaded {} Game Master Aura Skills.", gameMasterAuraSkillTree.size());
		final int commonSkills = commonSkillTree.size();
		if (commonSkills > 0) {
			LOG.info("Loaded {} Common Skills to all classes.", commonSkills);
		}
	}
	
	/**
	 * Gets the transfer skill tree.<br>
	 * If new classes are implemented over 3rd class, we use a recursive call.
	 * @param classId the transfer skill tree Id
	 * @return the complete Transfer Skill Tree for a given {@code classId}
	 */
	public Map<Integer, L2SkillLearn> getTransferSkillTree(ClassId classId) {
		if (classId.level() >= 3) {
			return getTransferSkillTree(classId.getParent());
		}
		return transferSkillTrees.get(classId);
	}

	/**
	 * Gets the collect skill tree.
	 * @return the complete Collect Skill Tree
	 */
	public Map<Integer, L2SkillLearn> getCollectSkillTree() {
		return collectSkillTree;
	}
	
	/**
	 * Gets the fishing skill tree.
	 * @return the complete Fishing Skill Tree
	 */
	public Map<Integer, L2SkillLearn> getFishingSkillTree() {
		return fishingSkillTree;
	}

	/**
	 * Gets the transform skill tree.
	 * @return the complete Transform Skill Tree
	 */
	public Map<Integer, L2SkillLearn> getTransformSkillTree() {
		return transformSkillTree;
	}
	
	/**
	 * Gets the noble skill tree.
	 * @return the complete Noble Skill Tree
	 */
	public Map<Integer, Skill> getNobleSkillTree() {
		final Map<Integer, Skill> tree = new HashMap<>();
		final SkillData st = SkillData.getInstance();
		for (Entry<Integer, L2SkillLearn> e : nobleSkillTree.entrySet()) {
			tree.put(e.getKey(), st.getSkill(e.getValue().getSkillId(), e.getValue().getSkillLevel()));
		}
		return tree;
	}
	
	/**
	 * Gets the hero skill tree.
	 * @return the complete Hero Skill Tree
	 */
	public Map<Integer, Skill> getHeroSkillTree() {
		final Map<Integer, Skill> tree = new HashMap<>();
		final SkillData st = SkillData.getInstance();
		for (Entry<Integer, L2SkillLearn> e : heroSkillTree.entrySet()) {
			tree.put(e.getKey(), st.getSkill(e.getValue().getSkillId(), e.getValue().getSkillLevel()));
		}
		return tree;
	}
	
	/**
	 * Gets the Game Master skill tree.
	 * @return the complete Game Master Skill Tree
	 */
	public Map<Integer, Skill> getGMSkillTree() {
		final Map<Integer, Skill> tree = new HashMap<>();
		final SkillData st = SkillData.getInstance();
		for (Entry<Integer, L2SkillLearn> e : gameMasterSkillTree.entrySet()) {
			tree.put(e.getKey(), st.getSkill(e.getValue().getSkillId(), e.getValue().getSkillLevel()));
		}
		return tree;
	}
	
	/**
	 * Gets the Game Master Aura skill tree.
	 * @return the complete Game Master Aura Skill Tree
	 */
	public Map<Integer, Skill> getGMAuraSkillTree() {
		final Map<Integer, Skill> tree = new HashMap<>();
		final SkillData st = SkillData.getInstance();
		for (Entry<Integer, L2SkillLearn> e : gameMasterAuraSkillTree.entrySet()) {
			tree.put(e.getKey(), st.getSkill(e.getValue().getSkillId(), e.getValue().getSkillLevel()));
		}
		return tree;
	}
	
	/**
	 * Gets the available skills.
	 * @param player the learning skill player
	 * @param classId the learning skill class Id
	 * @param includeByFs if {@code true} skills from Forgotten Scroll will be included
	 * @param includeAutoGet if {@code true} Auto-Get skills will be included
	 * @return all available skills for a given {@code player}, {@code classId}, {@code includeByFs} and {@code includeAutoGet}
	 */
	public List<L2SkillLearn> getAvailableSkills(L2PcInstance player, ClassId classId, boolean includeByFs, boolean includeAutoGet) {
		return getAvailableSkills(player, classId, includeByFs, includeAutoGet, player);
	}
	
	public Collection<Skill> getAllAvailableSkills(L2PcInstance player, ClassId classId, boolean includeByFs, boolean includeAutoGet) {
		// Get available skills

		Map<Integer, Skill> skills = player.getSkills().values()
				.stream()
				.filter(skill -> isSkillAllowed(player, skill))
				.collect(Collectors.toMap(Skill::getId, v -> v));

		PlayerSkillHolder holder = new PlayerSkillHolder(skills);
		List<L2SkillLearn> learnable = getAvailableSkills(player, classId, includeByFs, includeAutoGet, holder);
		while (!learnable.isEmpty()) {
			for (L2SkillLearn s : learnable) {
				Skill sk = SkillData.getInstance().getSkill(s.getSkillId(), s.getSkillLevel());
				holder.addSkill(sk);
			}

			// Get new available skills, some skills depend of previous skills to be available.
			learnable = getAvailableSkills(player, classId, includeByFs, includeAutoGet, holder);
		}
		return holder.getSkills().values();
	}
	
	/**
	 * Gets the available skills.
	 * @param player the learning skill player
	 * @param classId the learning skill class Id
	 * @param includeByFs if {@code true} skills from Forgotten Scroll will be included
	 * @param includeAutoGet if {@code true} Auto-Get skills will be included
	 * @return all available skills for a given {@code player}, {@code classId}, {@code includeByFs} and {@code includeAutoGet}
	 */
	private List<L2SkillLearn> getAvailableSkills(L2PcInstance player, ClassId classId, boolean includeByFs, boolean includeAutoGet, ISkillsHolder holder) {
		final List<L2SkillLearn> result = new ArrayList<>();
		final Map<Integer, L2SkillLearn> skills = getCompleteClassSkillTree(classId);

		if (skills.isEmpty()) {
			// The Skill Tree for this class is undefined.
			LOG.warn("{}: Skilltree for class {} is not defined!", getClass().getSimpleName(), classId);
			return result;
		}

		for (L2SkillLearn skill : skills.values()) {
			if (((skill.getSkillId() == CommonSkill.DIVINE_INSPIRATION.getId()) && (!character().autoLearnDivineInspiration() && includeAutoGet) && !player.isGM())) {
				continue;
			}

			if (((includeAutoGet && skill.isAutoGet()) || skill.isLearnedByNpc() || (includeByFs && skill.isLearnedByFS())) && (player.getLevel() >= skill.getGetLevel())) {
				final Skill oldSkill = holder.getKnownSkill(skill.getSkillId());
				if (oldSkill != null) {
					if (oldSkill.getLevel() == (skill.getSkillLevel() - 1)) {
						result.add(skill);
					}
				} else if (skill.getSkillLevel() == 1) {
					result.add(skill);
				}
			}
		}
		return result;
	}
	
	/**
	 * Verify if the give skill is valid for the given player.<br>
	 * GM's skills are excluded for GM players
	 * @param player the player to verify the skill
	 * @param skill the skill to be verified
	 * @return {@code true} if the skill is allowed to the given player
	 */
	public boolean isSkillAllowed(L2PcInstance player, Skill skill) {
		if (skill.isExcludedFromCheck()) {
			return true;
		}

		if (player.isGM() && skill.isGMSkill()) {
			return true;
		}


		final int maxLvl = SkillData.getInstance().getMaxLevel(skill.getId());
		final int hashCode = SkillData.getSkillHashCode(skill.getId(), Math.min(skill.getLevel(), maxLvl));

		if (Arrays.binarySearch(skillsByClassIdHashCodes.get(player.getClassId().ordinal()), hashCode) >= 0) {
			return true;
		}

		if (Arrays.binarySearch(skillsByRaceHashCodes.get(player.getRace().ordinal()), hashCode) >= 0) {
			return true;
		}

		if (Arrays.binarySearch(allSkillsHashCodes, hashCode) >= 0) {
			return true;
		}

		// Exclude Transfer Skills from this check.
		return getTransferSkill(skill.getId(), Math.min(skill.getLevel(), maxLvl), player.getClassId()) != null;
	}
	
	/**
	 * Gets the transfer skill.
	 * @param id the transfer skill Id
	 * @param lvl the transfer skill level.
	 * @param classId the transfer skill tree Id
	 * @return the transfer skill from the Transfer Skill Trees for a given {@code classId}, {@code id} and {@code lvl}
	 */
	public L2SkillLearn getTransferSkill(int id, int lvl, ClassId classId) {
		if (classId.getParent() != null) {
			final ClassId parentId = classId.getParent();
			if (transferSkillTrees.get(parentId) != null) {
				return transferSkillTrees.get(parentId).get(SkillData.getSkillHashCode(id, lvl));
			}
		}
		return null;
	}
	
	/**
	 * Gets the available auto get skills.
	 * @param player the player requesting the Auto-Get skills
	 * @return all the available Auto-Get skills for a given {@code player}
	 */
	public List<L2SkillLearn> getAvailableAutoGetSkills(L2PcInstance player) {
		final List<L2SkillLearn> result = new ArrayList<>();
		final Map<Integer, L2SkillLearn> skills = getCompleteClassSkillTree(player.getClassId());
		if (skills.isEmpty()) {
			// The Skill Tree for this class is undefined, so we return an empty list.
			LOG.warn("{}: Skill Tree for class ID {} is not defined!", getClass().getSimpleName(), player.getClassId());
			return result;
		}

		final Race race = player.getRace();
		for (L2SkillLearn skill : skills.values()) {
			if (!skill.getRaces().isEmpty() && !skill.getRaces().contains(race)) {
				continue;
			}

			if (skill.isAutoGet() && (player.getLevel() >= skill.getGetLevel())) {
				final Skill oldSkill = player.getSkills().get(skill.getSkillId());
				if (oldSkill != null) {
					if (oldSkill.getLevel() < skill.getSkillLevel()) {
						result.add(skill);
					}
				} else {
					result.add(skill);
				}
			}
		}
		return result;
	}
	
	/**
	 * Dwarvens will get additional dwarven only fishing skills.
	 * @param player the player
	 * @return all the available Fishing skills for a given {@code player}
	 */
	public List<L2SkillLearn> getAvailableFishingSkills(L2PcInstance player) {
		final List<L2SkillLearn> result = new ArrayList<>();
		final Race playerRace = player.getRace();
		for (L2SkillLearn skill : fishingSkillTree.values()) {
			// If skill is Race specific and the player's race isn't allowed, skip it.
			if (!skill.getRaces().isEmpty() && !skill.getRaces().contains(playerRace)) {
				continue;
			}

			if (skill.isLearnedByNpc() && (player.getLevel() >= skill.getGetLevel())) {
				final Skill oldSkill = player.getSkills().get(skill.getSkillId());
				if (oldSkill != null) {
					if (oldSkill.getLevel() == (skill.getSkillLevel() - 1)) {
						result.add(skill);
					}
				} else if (skill.getSkillLevel() == 1) {
					result.add(skill);
				}
			}
		}
		return result;
	}
	
	/**
	 * Used in Gracia continent.
	 * @param player the collecting skill learning player
	 * @return all the available Collecting skills for a given {@code player}
	 */
	public List<L2SkillLearn> getAvailableCollectSkills(L2PcInstance player) {
		final List<L2SkillLearn> result = new ArrayList<>();
		for (L2SkillLearn skill : collectSkillTree.values()) {
			final Skill oldSkill = player.getSkills().get(skill.getSkillId());
			if (oldSkill != null) {
				if (oldSkill.getLevel() == (skill.getSkillLevel() - 1)) {
					result.add(skill);
				}
			} else if (skill.getSkillLevel() == 1) {
				result.add(skill);
			}
		}
		return result;
	}
	
	/**
	 * Gets the available transfer skills.
	 * @param player the transfer skill learning player
	 * @return all the available Transfer skills for a given {@code player}
	 */
	public List<L2SkillLearn> getAvailableTransferSkills(L2PcInstance player) {
		final List<L2SkillLearn> result = new ArrayList<>();
		ClassId classId = player.getClassId();
		// If new classes are implemented over 3rd class, a different way should be implemented.
		if (classId.level() == 3) {
			classId = classId.getParent();
		}

		if (!transferSkillTrees.containsKey(classId)) {
			return result;
		}

		for (L2SkillLearn skill : transferSkillTrees.get(classId).values()) {
			// If player doesn't know this transfer skill:
			if (player.getKnownSkill(skill.getSkillId()) == null) {
				result.add(skill);
			}
		}
		return result;
	}
	
	/**
	 * Some transformations are not available for some races.
	 * @param player the transformation skill learning player
	 * @return all the available Transformation skills for a given {@code player}
	 */
	public List<L2SkillLearn> getAvailableTransformSkills(L2PcInstance player) {
		final List<L2SkillLearn> result = new ArrayList<>();
		final Race race = player.getRace();
		for (L2SkillLearn skill : transformSkillTree.values()) {
			if ((player.getLevel() >= skill.getGetLevel()) && (skill.getRaces().isEmpty() || skill.getRaces().contains(race))) {
				final Skill oldSkill = player.getSkills().get(skill.getSkillId());
				if (oldSkill != null) {
					if (oldSkill.getLevel() == (skill.getSkillLevel() - 1)) {
						result.add(skill);
					}
				} else if (skill.getSkillLevel() == 1) {
					result.add(skill);
				}
			}
		}
		return result;
	}
	
	/**
	 * Gets the available pledge skills.
	 * @param clan the pledge skill learning clan
	 * @return all the available Pledge skills for a given {@code clan}
	 */
	public List<L2SkillLearn> getAvailablePledgeSkills(L2Clan clan) {
		final List<L2SkillLearn> result = new ArrayList<>();

		for (L2SkillLearn skill : pledgeSkillTree.values()) {
			if (!skill.isResidentialSkill() && (clan.getLevel() >= skill.getGetLevel())) {
				final Skill oldSkill = clan.getSkills().get(skill.getSkillId());
				if (oldSkill != null) {
					if ((oldSkill.getLevel() + 1) == skill.getSkillLevel()) {
						result.add(skill);
					}
				} else if (skill.getSkillLevel() == 1) {
					result.add(skill);
				}
			}
		}
		return result;
	}
	
	/**
	 * Gets the available pledge skills.
	 * @param clan the pledge skill learning clan
	 * @param includeSquad if squad skill will be added too
	 * @return all the available pledge skills for a given {@code clan}
	 */
	public Map<Integer, L2SkillLearn> getMaxPledgeSkills(L2Clan clan, boolean includeSquad) {
		final Map<Integer, L2SkillLearn> result = new HashMap<>();
		for (L2SkillLearn skill : pledgeSkillTree.values()) {
			if (!skill.isResidentialSkill() && (clan.getLevel() >= skill.getGetLevel())) {
				final Skill oldSkill = clan.getSkills().get(skill.getSkillId());
				if ((oldSkill == null) || (oldSkill.getLevel() < skill.getSkillLevel())) {
					result.put(skill.getSkillId(), skill);
				}
			}
		}

		if (includeSquad) {
			for (L2SkillLearn skill : subPledgeSkillTree.values()) {
				if ((clan.getLevel() >= skill.getGetLevel())) {
					final Skill oldSkill = clan.getSkills().get(skill.getSkillId());
					if ((oldSkill == null) || (oldSkill.getLevel() < skill.getSkillLevel())) {
						result.put(skill.getSkillId(), skill);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Gets the available sub pledge skills.
	 * @param clan the sub-pledge skill learning clan
	 * @return all the available Sub-Pledge skills for a given {@code clan}
	 */
	public List<L2SkillLearn> getAvailableSubPledgeSkills(L2Clan clan) {
		final List<L2SkillLearn> result = new ArrayList<>();
		for (L2SkillLearn skill : subPledgeSkillTree.values()) {
			if ((clan.getLevel() >= skill.getGetLevel()) && clan.isLearnableSubSkill(skill.getSkillId(), skill.getSkillLevel())) {
				result.add(skill);
			}
		}
		return result;
	}
	
	/**
	 * Gets the available sub class skills.
	 * @param player the sub-class skill learning player
	 * @return all the available Sub-Class skills for a given {@code player}
	 */
	public List<L2SkillLearn> getAvailableSubClassSkills(L2PcInstance player) {
		final List<L2SkillLearn> result = new ArrayList<>();
		for (L2SkillLearn skill : subClassSkillTree.values()) {
			if (player.getLevel() >= skill.getGetLevel()) {
				for (SubClass subClass : player.getSubClasses().values()) {
					final var subClassConds = skill.getSubClassConditions();
					if (!subClassConds.isEmpty() && (subClass.getClassIndex() <= subClassConds.size()) && (subClass.getClassIndex() == subClassConds.get(subClass.getClassIndex() - 1).getSlot()) && (subClassConds.get(subClass.getClassIndex() - 1).getLvl() <= subClass.getLevel())) {
						final Skill oldSkill = player.getSkills().get(skill.getSkillId());
						if (oldSkill != null) {
							if (oldSkill.getLevel() == (skill.getSkillLevel() - 1)) {
								result.add(skill);
							}
						} else if (skill.getSkillLevel() == 1) {
							result.add(skill);
						}
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Gets the available residential skills.
	 * @param residenceId the id of the Castle, Fort, Territory
	 * @return all the available Residential skills for a given {@code residenceId}
	 */
	public List<L2SkillLearn> getAvailableResidentialSkills(int residenceId) {
		final List<L2SkillLearn> result = new ArrayList<>();
		for (L2SkillLearn skill : pledgeSkillTree.values()) {
			if (skill.isResidentialSkill() && skill.getResidenceIds().contains(residenceId)) {
				result.add(skill);
			}
		}
		return result;
	}
	
	/**
	 * Just a wrapper for all skill trees.
	 * @param skillType the skill type
	 * @param id the skill Id
	 * @param lvl the skill level
	 * @param player the player learning the skill
	 * @return the skill learn for the specified parameters
	 */
	public L2SkillLearn getSkillLearn(AcquireSkillType skillType, int id, int lvl, L2PcInstance player) {
		return switch (skillType) {
			case CLASS -> getClassSkill(id, lvl, player.getLearningClass());
			case TRANSFORM -> getTransformSkill(id, lvl);
			case FISHING -> getFishingSkill(id, lvl);
			case PLEDGE -> getPledgeSkill(id, lvl);
			case SUBPLEDGE -> getSubPledgeSkill(id, lvl);
			case TRANSFER -> getTransferSkill(id, lvl, player.getClassId());
			case SUBCLASS -> getSubClassSkill(id, lvl);
			case COLLECT -> getCollectSkill(id, lvl);
			default -> null;
		};
	}
	
	/**
	 * Gets the transform skill.
	 * @param id the transformation skill Id
	 * @param lvl the transformation skill level
	 * @return the transform skill from the Transform Skill Tree for a given {@code id} and {@code lvl}
	 */
	public L2SkillLearn getTransformSkill(int id, int lvl) {
		return transformSkillTree.get(SkillData.getSkillHashCode(id, lvl));
	}
	
	/**
	 * Gets the class skill.
	 * @param id the class skill Id
	 * @param lvl the class skill level.
	 * @param classId the class skill tree Id
	 * @return the class skill from the Class Skill Trees for a given {@code classId}, {@code id} and {@code lvl}
	 */
	public L2SkillLearn getClassSkill(int id, int lvl, ClassId classId) {
		return getCompleteClassSkillTree(classId).get(SkillData.getSkillHashCode(id, lvl));
	}
	
	/**
	 * Gets the fishing skill.
	 * @param id the fishing skill Id
	 * @param lvl the fishing skill level
	 * @return Fishing skill from the Fishing Skill Tree for a given {@code id} and {@code lvl}
	 */
	public L2SkillLearn getFishingSkill(int id, int lvl) {
		return fishingSkillTree.get(SkillData.getSkillHashCode(id, lvl));
	}
	
	/**
	 * Gets the pledge skill.
	 * @param id the pledge skill Id
	 * @param lvl the pledge skill level
	 * @return the pledge skill from the Pledge Skill Tree for a given {@code id} and {@code lvl}
	 */
	public L2SkillLearn getPledgeSkill(int id, int lvl) {
		return pledgeSkillTree.get(SkillData.getSkillHashCode(id, lvl));
	}
	
	/**
	 * Gets the sub pledge skill.
	 * @param id the sub-pledge skill Id
	 * @param lvl the sub-pledge skill level
	 * @return the sub-pledge skill from the Sub-Pledge Skill Tree for a given {@code id} and {@code lvl}
	 */
	public L2SkillLearn getSubPledgeSkill(int id, int lvl) {
		return subPledgeSkillTree.get(SkillData.getSkillHashCode(id, lvl));
	}
	
	/**
	 * Gets the sub class skill.
	 * @param id the sub-class skill Id
	 * @param lvl the sub-class skill level
	 * @return the sub-class skill from the Sub-Class Skill Tree for a given {@code id} and {@code lvl}
	 */
	public L2SkillLearn getSubClassSkill(int id, int lvl) {
		return subClassSkillTree.get(SkillData.getSkillHashCode(id, lvl));
	}
	
	/**
	 * Gets the collect skill.
	 * @param id the collect skill Id
	 * @param lvl the collect skill level
	 * @return the collect skill from the Collect Skill Tree for a given {@code id} and {@code lvl}
	 */
	public L2SkillLearn getCollectSkill(int id, int lvl) {
		return collectSkillTree.get(SkillData.getSkillHashCode(id, lvl));
	}
	
	/**
	 * Gets the common skill.
	 * @param id the common skill Id.
	 * @param lvl the common skill level
	 * @return the common skill from the Common Skill Tree for a given {@code id} and {@code lvl}
	 */
	public L2SkillLearn getCommonSkill(int id, int lvl) {
		return commonSkillTree.get(SkillData.getSkillHashCode(id, lvl));
	}
	
	/**
	 * Gets the minimum level for new skill.
	 * @param player the player that requires the minimum level
	 * @param skillTree the skill tree to search the minimum get level
	 * @return the minimum level for a new skill for a given {@code player} and {@code skillTree}
	 */
	public int getMinLevelForNewSkill(L2PcInstance player, Map<Integer, L2SkillLearn> skillTree) {
		int minLevel = 0;
		if (skillTree.isEmpty()) {
			LOG.warn("{}: SkillTree is not defined for getMinLevelForNewSkill!", getClass().getSimpleName());
		} else {
			for (L2SkillLearn s : skillTree.values()) {
				if (s.isLearnedByNpc() && (player.getLevel() < s.getGetLevel())) {
					if ((minLevel == 0) || (minLevel > s.getGetLevel())) {
						minLevel = s.getGetLevel();
					}
				}
			}
		}
		return minLevel;
	}
	
	/**
	 * Checks if is hero skill.
	 * @param skillId the Id of the skill to check
	 * @param skillLevel the level of the skill to check, if it's -1 only Id will be checked
	 * @return {@code true} if the skill is present in the Hero Skill Tree, {@code false} otherwise
	 */
	public boolean isHeroSkill(int skillId, int skillLevel) {
		if (heroSkillTree.containsKey(SkillData.getSkillHashCode(skillId, skillLevel))) {
			return true;
		}

		for (L2SkillLearn skill : heroSkillTree.values()) {
			if ((skill.getSkillId() == skillId) && (skillLevel == -1)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if is GM skill.
	 * @param skillId the Id of the skill to check
	 * @param skillLevel the level of the skill to check, if it's -1 only Id will be checked
	 * @return {@code true} if the skill is present in the Game Master Skill Trees, {@code false} otherwise
	 */
	public boolean isGMSkill(int skillId, int skillLevel) {
		if (skillLevel <= 0) {
			return gameMasterSkillTree.values().stream().anyMatch(s -> s.getSkillId() == skillId) //
				|| gameMasterAuraSkillTree.values().stream().anyMatch(s -> s.getSkillId() == skillId);
		}
		final int hashCode = SkillData.getSkillHashCode(skillId, skillLevel);
		return gameMasterSkillTree.containsKey(hashCode) || gameMasterAuraSkillTree.containsKey(hashCode);
	}
	
	/**
	 * Checks if a skill is a Clan skill.
	 * @param skillId the Id of the skill to check
	 * @param skillLevel the level of the skill to check
	 * @return {@code true} if the skill is present in the Pledge or Subpledge Skill Trees, {@code false} otherwise
	 */
	public boolean isClanSkill(int skillId, int skillLevel) {
		final int hashCode = SkillData.getSkillHashCode(skillId, skillLevel);
		return pledgeSkillTree.containsKey(hashCode) || subPledgeSkillTree.containsKey(hashCode);
	}
	
	/**
	 * Adds the skills.
	 * @param gmchar the player to add the Game Master skills
	 * @param auraSkills if {@code true} it will add "GM Aura" skills, else will add the "GM regular" skills
	 */
	public void addSkills(L2PcInstance gmchar, boolean auraSkills) {
		final Collection<L2SkillLearn> skills = auraSkills ? gameMasterAuraSkillTree.values() : gameMasterSkillTree.values();
		final SkillData st = SkillData.getInstance();
		for (L2SkillLearn sl : skills) {
			gmchar.addSkill(st.getSkill(sl.getSkillId(), sl.getSkillLevel()), false); // Don't Save GM skills to database
		}
	}
	
	private static class SingletonHolder {
		protected static final SkillTreesData INSTANCE = new SkillTreesData();
	}
}
