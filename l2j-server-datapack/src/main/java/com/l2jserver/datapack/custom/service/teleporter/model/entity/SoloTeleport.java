
package com.l2jserver.datapack.custom.service.teleporter.model.entity;

import com.l2jserver.datapack.custom.service.base.model.entity.CustomServiceProduct;
import com.l2jserver.datapack.custom.service.teleporter.model.TeleporterConfig;

/**
 * Solo teleport.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public class SoloTeleport extends CustomServiceProduct {
	private String name;
	private int x;
	private int y;
	private int z;
	private int heading;
	private int randomOffset;
	private String instance;
	
	public SoloTeleport() {
	}
	
	public void afterDeserialize(TeleporterConfig config) {
		super.afterDeserialize();
		
		getPlaceholder().addChild("name", name);
	}
	
	public String getName() {
		return name;
	}
	
	public int getRandomOffset() {
		return randomOffset;
	}
	
	public int getHeading() {
		return heading;
	}
	
	public String getInstance() {
		return instance;
	}
	
	public int getZ() {
		return z;
	}
	
	public int getY() {
		return y;
	}
	
	public int getX() {
		return x;
	}
}
