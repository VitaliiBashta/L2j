
package com.l2jserver.datapack.custom.service.teleporter.model.entity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplatePlaceholder;
import com.l2jserver.datapack.custom.service.teleporter.model.TeleporterConfig;

/**
 * Solo teleport category.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public class SoloTeleportCategory extends AbstractTeleportCategory {
	private List<String> soloTeleports;
	
	private transient Map<String, SoloTeleport> soloTeleportsMap;
	
	public SoloTeleportCategory() {
		soloTeleportsMap = new LinkedHashMap<>();
	}
	
	@Override
	public void afterDeserialize(TeleporterConfig config) {
		super.afterDeserialize(config);
		
		for (String id : soloTeleports) {
			soloTeleportsMap.put(id, config.getGlobal().getSoloTeleports().get(id));
		}
		
		if (!soloTeleports.isEmpty()) {
			HTMLTemplatePlaceholder telePlaceholder = getPlaceholder().addChild("teleports", null).getChild("teleports");
			for (Entry<String, SoloTeleport> soloTeleport : soloTeleportsMap.entrySet()) {
				telePlaceholder.addAliasChild(String.valueOf(telePlaceholder.getChildrenSize()), soloTeleport.getValue().getPlaceholder());
			}
		}
	}
	
	public Map<String, SoloTeleport> getSoloTeleports() {
		return soloTeleportsMap;
	}
}
