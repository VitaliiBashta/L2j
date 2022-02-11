package com.l2jserver.gameserver.handler;

import com.l2jserver.gameserver.model.items.L2EtcItem;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemHandler implements IHandler<IItemHandler, L2EtcItem> {
	private final Map<String, IItemHandler> _datatable;
	
	protected ItemHandler() {
		_datatable = new HashMap<>();
	}
	
	@Override
	public void registerHandler(IItemHandler handler) {
		_datatable.put(handler.getClass().getSimpleName(), handler);
	}
	
	@Override
	public synchronized void removeHandler(IItemHandler handler) {
		_datatable.remove(handler.getClass().getSimpleName());
	}
	
	@Override
	public IItemHandler getHandler(L2EtcItem item) {
		if ((item == null) || (item.getHandlerName() == null)) {
			return null;
		}
		return _datatable.get(item.getHandlerName());
	}
	
	@Override
	public int size() {
		return _datatable.size();
	}
	
	public static ItemHandler getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		protected static final ItemHandler INSTANCE = new ItemHandler();
	}
}
