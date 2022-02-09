
package com.l2jserver.datapack.custom.service.buffer.model;

import java.util.Map;

import com.l2jserver.datapack.custom.service.buffer.model.entity.BuffCategory;
import com.l2jserver.datapack.custom.service.buffer.model.entity.BuffSkill;

/**
 * Global configuration.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public final class GlobalConfig {
	private Map<String, BuffSkill> buffs;
	private Map<String, BuffCategory> buffCategories;
	
	public void afterDeserialize(BufferConfig config) {
		for (var buff : buffs.values()) {
			buff.afterDeserialize(config);
		}
		
		for (var category : buffCategories.values()) {
			category.afterDeserialize(config);
		}
	}
	
	public BuffSkill getBuff(String id) {
		return buffs.get(id);
	}
	
	public final Map<String, BuffSkill> getBuffs() {
		return buffs;
	}
	
	public final Map<String, BuffCategory> getCategories() {
		return buffCategories;
	}
}