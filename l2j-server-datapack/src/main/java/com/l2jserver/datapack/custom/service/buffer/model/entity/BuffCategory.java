
package com.l2jserver.datapack.custom.service.buffer.model.entity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.l2jserver.datapack.custom.service.base.model.entity.Refable;
import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplatePlaceholder;
import com.l2jserver.datapack.custom.service.buffer.model.BufferConfig;

/**
 * Buff category.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public class BuffCategory extends Refable {
	private String name;
	private List<String> buffs;
	
	private final transient Map<String, BuffSkill> buffSkillsMap = new LinkedHashMap<>();
	
	public void afterDeserialize(BufferConfig config) {
		super.afterDeserialize();
		
		for (String id : buffs) {
			buffSkillsMap.put(id, config.getGlobal().getBuff(id));
		}
		
		getPlaceholder().addChild("name", name);
		if (!buffs.isEmpty()) {
			HTMLTemplatePlaceholder buffsPlaceholder = getPlaceholder().addChild("buffs", null).getChild("buffs");
			for (Entry<String, BuffSkill> buff : buffSkillsMap.entrySet()) {
				buffsPlaceholder.addAliasChild(String.valueOf(buffsPlaceholder.getChildrenSize()), buff.getValue().getPlaceholder());
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Map<String, BuffSkill> getBuffs() {
		return buffSkillsMap;
	}
	
	public BuffSkill getBuff(String id) {
		return buffSkillsMap.get(id);
	}
}
