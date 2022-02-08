
package com.l2jserver.datapack.custom.service.buffer.model.entity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.l2jserver.datapack.custom.service.base.model.entity.CustomServiceServer;
import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplatePlaceholder;
import com.l2jserver.datapack.custom.service.buffer.model.BufferConfig;
import com.l2jserver.gameserver.config.Configuration;

/**
 * Abstract buffer.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public abstract class AbstractBuffer extends CustomServiceServer {
	private boolean canHeal;
	private boolean canCancel;
	private List<String> presetBuffCategories;
	private List<String> buffCategories;
	
	private final transient Map<String, BuffCategory> presetBuffCatsMap = new LinkedHashMap<>();
	private final transient Map<String, BuffCategory> buffCatsMap = new LinkedHashMap<>();
	
	public AbstractBuffer(String bypassPrefix) {
		super(bypassPrefix, "buffer");
	}
	
	public void afterDeserialize(BufferConfig config) {
		super.afterDeserialize();
		
		for (String id : presetBuffCategories) {
			presetBuffCatsMap.put(id, config.getGlobal().getCategories().get(id));
		}
		
		for (String id : buffCategories) {
			buffCatsMap.put(id, config.getGlobal().getCategories().get(id));
		}
		
		if (canHeal) {
			getPlaceholder().addChild("can_heal", null);
		}
		if (canCancel) {
			getPlaceholder().addChild("can_cancel", null);
		}
		if (!presetBuffCategories.isEmpty()) {
			HTMLTemplatePlaceholder presetBufflistsPlaceholder = getPlaceholder().addChild("presets", null).getChild("presets");
			for (Entry<String, BuffCategory> presetBufflist : presetBuffCatsMap.entrySet()) {
				presetBufflistsPlaceholder.addAliasChild(String.valueOf(presetBufflistsPlaceholder.getChildrenSize()), presetBufflist.getValue().getPlaceholder());
			}
		}
		if (!buffCategories.isEmpty()) {
			HTMLTemplatePlaceholder buffCatsPlaceholder = getPlaceholder().addChild("categories", null).getChild("categories");
			for (Entry<String, BuffCategory> buffCat : buffCatsMap.entrySet()) {
				buffCatsPlaceholder.addAliasChild(String.valueOf(buffCatsPlaceholder.getChildrenSize()), buffCat.getValue().getPlaceholder());
			}
		}
		
		getPlaceholder().addChild("max_unique_lists", String.valueOf(Configuration.bufferService().getMaxUniqueLists()));
	}
	
	public final boolean getCanHeal() {
		return canHeal;
	}
	
	public final boolean getCanCancel() {
		return canCancel;
	}
	
	public Map<String, BuffCategory> getPresetBuffCats() {
		return presetBuffCatsMap;
	}
	
	public final Map<String, BuffCategory> getBuffCats() {
		return buffCatsMap;
	}
}
