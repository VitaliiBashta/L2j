
package com.l2jserver.datapack.custom.service.teleporter.model.entity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplatePlaceholder;
import com.l2jserver.datapack.custom.service.teleporter.model.TeleporterConfig;

/**
 * Group teleport category.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public class GroupTeleportCategory extends AbstractTeleportCategory {
	private List<String> groupTeleports;
	
	private transient Map<String, GroupTeleport> groupTeleportsMap;
	
	public GroupTeleportCategory() {
		groupTeleportsMap = new LinkedHashMap<>();
	}
	
	@Override
	public void afterDeserialize(TeleporterConfig config) {
		super.afterDeserialize(config);
		
		for (String id : groupTeleports) {
			groupTeleportsMap.put(id, config.getGlobal().getGroupTeleports().get(id));
		}
		
		if (!groupTeleports.isEmpty()) {
			HTMLTemplatePlaceholder telePlaceholder = getPlaceholder().addChild("teleports", null).getChild("teleports");
			for (Entry<String, GroupTeleport> groupTeleport : groupTeleportsMap.entrySet()) {
				telePlaceholder.addAliasChild(String.valueOf(telePlaceholder.getChildrenSize()), groupTeleport.getValue().getPlaceholder());
			}
		}
	}
	
	public Map<String, GroupTeleport> getGroupTeleports() {
		return groupTeleportsMap;
	}
}
