
package com.l2jserver.datapack.custom.service.base.model.entity;

import java.util.List;

import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplatePlaceholder;

/**
 * Custom Service Product.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public abstract class CustomServiceProduct extends Refable {
	private List<ItemRequirement> items;
	
	protected CustomServiceProduct() {
	}
	
	@Override
	public void afterDeserialize() {
		super.afterDeserialize();
		
		if (!items.isEmpty()) {
			HTMLTemplatePlaceholder itemsPlaceholder = getPlaceholder().addChild("items", null).getChild("items");
			for (ItemRequirement item : items) {
				item.afterDeserialize();
				itemsPlaceholder.addAliasChild(String.valueOf(itemsPlaceholder.getChildrenSize()), item.getPlaceholder());
			}
		}
	}
	
	public final List<ItemRequirement> getItems() {
		return items;
	}
}
