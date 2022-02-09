package com.l2jserver.gameserver.engines;

import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.engines.items.DocumentItem;
import com.l2jserver.gameserver.engines.skills.DocumentSkill;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.util.file.filter.XMLFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.l2jserver.gameserver.config.Configuration.general;
import static com.l2jserver.gameserver.config.Configuration.server;

@Service
public class DocumentEngine {

	private static final Logger LOG = LoggerFactory.getLogger(DocumentEngine.class);
	
	private final List<File> _itemFiles = new ArrayList<>();
	
	private final List<File> _skillFiles = new ArrayList<>();
	
	public static DocumentEngine getInstance() {
		return SingletonHolder._instance;
	}
	
	protected DocumentEngine() {
		hashFiles("data/stats/items", _itemFiles);
		if (general().customItemsLoad()) {
			hashFiles("data/stats/items/custom", _itemFiles);
		}
		hashFiles("data/stats/skills", _skillFiles);
		if (general().customSkillsLoad()) {
			hashFiles("data/stats/skills/custom", _skillFiles);
		}
	}
	
	private void hashFiles(String dirname, List<File> hash) {
		final var dir = new File(server().getDatapackRoot(), dirname);
		if (!dir.exists()) {
			LOG.warn("Directory {} does not exists!", dir.getAbsolutePath());
			return;
		}
		
		final var files = dir.listFiles(new XMLFilter());
		if (files != null) {
			Collections.addAll(hash, files);
		}
	}
	
	public List<Skill> loadSkills(File file) {
		if (file == null) {
			LOG.warn("Skill file not found!");
			return null;
		}
		DocumentSkill doc = new DocumentSkill(file);
		doc.parse();
		return doc.getSkills();
	}
	
	public void loadAllSkills(final Map<Integer, Skill> allSkills) {
		int count = 0;
		for (File file : _skillFiles) {
			List<Skill> s = loadSkills(file);
			if (s == null) {
				continue;
			}
			for (Skill skill : s) {
				allSkills.put(SkillData.getSkillHashCode(skill), skill);
				count++;
			}
		}
		LOG.info("Loaded {} skill templates from XML files.", count);
	}
	
	/**
	 * Return created items
	 * @return List of {@link L2Item}
	 */
	public List<L2Item> loadItems() {
		List<L2Item> list = new ArrayList<>();
		for (File f : _itemFiles) {
			DocumentItem document = new DocumentItem(f);
			document.parse();
			list.addAll(document.getItemList());
		}
		return list;
	}
	
	private static class SingletonHolder {
		protected static final DocumentEngine _instance = new DocumentEngine();
	}
}
