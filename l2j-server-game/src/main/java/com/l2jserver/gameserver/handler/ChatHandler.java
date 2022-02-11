package com.l2jserver.gameserver.handler;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatHandler {
  private final Map<Integer, IChatHandler> datatable = new HashMap<>();

  public ChatHandler(List<IChatHandler> handlers) {
    handlers.forEach(this::registerHandler);
  }

  private void registerHandler(IChatHandler handler) {
    int[] ids = handler.getChatTypeList();
    for (int id : ids) {
      datatable.put(id, handler);
    }
  }

  public static ChatHandler getInstance() {
    return SingletonHolder._instance;
  }

  /** Get the chat handler for the given chat type */
  public IChatHandler getHandler(Integer chatType) {
    return datatable.get(chatType);
  }

  private static class SingletonHolder {
    protected static final ChatHandler _instance = new ChatHandler(null);
  }
}
