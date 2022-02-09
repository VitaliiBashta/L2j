
package com.l2jserver.datapack.custom.service.teleporter.model.entity;

import com.l2jserver.datapack.custom.service.base.model.entity.Refable;
import com.l2jserver.datapack.custom.service.teleporter.model.TeleporterConfig;

/**
 * Base class for teleporter categories.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public abstract class AbstractTeleportCategory extends Refable {
	private String name;
	
	public void afterDeserialize(TeleporterConfig config) {
		super.afterDeserialize();
		
		getPlaceholder().addChild("name", name);
	}
	
	public final String getName() {
		return name;
	}
}
