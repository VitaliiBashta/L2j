package com.l2jserver.gameserver.model;

import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

/**
 * Class explanation:<br>
 * For item counting or checking purposes. When you don't want to modify inventory<br>
 * class contains itemId, quantity, ownerId, referencePrice, but not objectId<br>
 * is stored, this will be only "list" of items with it's owner
 */
public final class TempItem {
  private final int itemId;
  private final int referencePrice;
  private final String itemName;
  private int quantity;

	public TempItem(L2ItemInstance item, int quantity) {
    itemId = item.getId();
    this.quantity = quantity;
    itemName = item.getItem().getName();
    referencePrice = item.getReferencePrice();
	}
	
	public int getQuantity() {
    return quantity;
	}
	
	public void setQuantity(int quantity) {
    this.quantity = quantity;
	}
	
	public int getReferencePrice() {
    return referencePrice;
	}
	
	public int getItemId() {
    return itemId;
	}
	
	public String getItemName() {
    return itemName;
	}
}
