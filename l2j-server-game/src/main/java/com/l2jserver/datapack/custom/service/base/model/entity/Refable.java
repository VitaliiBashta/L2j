
package com.l2jserver.datapack.custom.service.base.model.entity;

import java.util.Objects;

import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplatePlaceholder;

/**
 * Refable.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public abstract class Refable implements IRefable<String> {
	private String id;
	
	private final transient HTMLTemplatePlaceholder placeholder;
	
	protected Refable() {
		id = null;
		
		placeholder = new HTMLTemplatePlaceholder("placeholder", null);
	}
	
	protected Refable(String id) {
		Objects.requireNonNull(id);
		this.id = id;
		
		placeholder = new HTMLTemplatePlaceholder("placeholder", null);
	}
	
	public void afterDeserialize() {
		placeholder.addChild("ident", id);
	}
	
	@Override
	public final String getId() {
		return id;
	}
	
	public final HTMLTemplatePlaceholder getPlaceholder() {
		return placeholder;
	}
}
