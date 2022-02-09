
package com.l2jserver.datapack.custom.service.base.model.entity;

import com.l2jserver.datapack.custom.service.base.DialogType;
import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplatePlaceholder;

/**
 * Custom Service Server.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public abstract class CustomServiceServer {
	private DialogType dialogType;
	private String htmlFolder;
	
	private final transient HTMLTemplatePlaceholder placeholder;
	private final transient String bypassPrefix;
	private final transient String htmlAccessorName;
	
	public CustomServiceServer(String bypassPrefix, String htmlAccessorName) {
		dialogType = DialogType.NPC;
		htmlFolder = null;
		
		placeholder = new HTMLTemplatePlaceholder("service", null);
		this.bypassPrefix = "bypass -h " + bypassPrefix;
		this.htmlAccessorName = htmlAccessorName;
	}
	
	public void afterDeserialize() {
		placeholder.addChild("bypass_prefix", bypassPrefix).addChild("name", getName());
	}
	
	public final DialogType getDialogType() {
		return dialogType;
	}
	
	public final String getHtmlFolder() {
		return htmlFolder;
	}
	
	public final HTMLTemplatePlaceholder getPlaceholder() {
		return placeholder;
	}
	
	public final String getBypassPrefix() {
		return bypassPrefix;
	}
	
	public final String getHtmlAccessorName() {
		return htmlAccessorName;
	}
	
	public abstract String getName();
}
