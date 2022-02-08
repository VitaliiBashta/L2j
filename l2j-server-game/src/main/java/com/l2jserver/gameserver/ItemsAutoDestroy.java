package com.l2jserver.gameserver;

import com.l2jserver.gameserver.enums.ItemLocation;
import com.l2jserver.gameserver.instancemanager.ItemsOnGroundManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.l2jserver.gameserver.config.Configuration.general;

public final class ItemsAutoDestroy {
	private final Map<Integer, L2ItemInstance> _items = new ConcurrentHashMap<>();
	
	protected ItemsAutoDestroy() {
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this::removeItems, 5000, 5000);
	}
	
	public static ItemsAutoDestroy getInstance() {
		return SingletonHolder._instance;
	}
	
	public synchronized void addItem(L2ItemInstance item) {
		item.setDropTime(System.currentTimeMillis());
		_items.put(item.getObjectId(), item);
	}
	
	public synchronized void removeItems() {
		final long curtime = System.currentTimeMillis();
		for (L2ItemInstance item : _items.values()) {
			if (item == null) {
				continue;
			}
			
			if ((item.getDropTime() == 0) || (item.getItemLocation() != ItemLocation.VOID)) {
				_items.remove(item.getObjectId());
			} else {
				if (item.getItem().getAutoDestroyTime() > 0) {
					if ((curtime - item.getDropTime()) > item.getItem().getAutoDestroyTime()) {
						L2World.getInstance().removeVisibleObject(item, item.getWorldRegion());
						L2World.getInstance().removeObject(item);
						_items.remove(item.getObjectId());
						if (general().saveDroppedItem()) {
							ItemsOnGroundManager.getInstance().removeObject(item);
						}
					}
				} else if (item.getItem().hasExImmediateEffect()) {
					if ((curtime - item.getDropTime()) > general().getAutoDestroyHerbTime()) {
						L2World.getInstance().removeVisibleObject(item, item.getWorldRegion());
						L2World.getInstance().removeObject(item);
						_items.remove(item.getObjectId());
						if (general().saveDroppedItem()) {
							ItemsOnGroundManager.getInstance().removeObject(item);
						}
					}
				} else {
					final long sleep = ((general().getAutoDestroyDroppedItemAfter() == 0) ? 3600000 : general().getAutoDestroyDroppedItemAfter() * 1000);
					
					if ((curtime - item.getDropTime()) > sleep) {
						L2World.getInstance().removeVisibleObject(item, item.getWorldRegion());
						L2World.getInstance().removeObject(item);
						_items.remove(item.getObjectId());
						if (general().saveDroppedItem()) {
							ItemsOnGroundManager.getInstance().removeObject(item);
						}
					}
				}
			}
		}
	}
	
	private static class SingletonHolder {
		protected static final ItemsAutoDestroy _instance = new ItemsAutoDestroy();
	}
}