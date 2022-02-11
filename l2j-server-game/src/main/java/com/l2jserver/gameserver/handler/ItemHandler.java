package com.l2jserver.gameserver.handler;

import com.l2jserver.gameserver.model.items.L2EtcItem;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemHandler {
  private final Map<String, IItemHandler> datatable = new HashMap<>();

  protected ItemHandler(List<IItemHandler> handlers) {
    handlers.forEach(this::registerHandler);
  }

  public void registerHandler(IItemHandler handler) {
    datatable.put(handler.getClass().getSimpleName(), handler);
  }

  public static ItemHandler getInstance() {
    return SingletonHolder.INSTANCE;
  }

  public IItemHandler getHandler(L2EtcItem item) {
    if ((item == null) || (item.getHandlerName() == null)) {
      return null;
    }
    return datatable.get(item.getHandlerName());
  }

  private static class SingletonHolder {
    protected static final ItemHandler INSTANCE = new ItemHandler(null);
  }
}
