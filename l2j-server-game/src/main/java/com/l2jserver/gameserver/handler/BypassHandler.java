package com.l2jserver.gameserver.handler;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BypassHandler {
  private final Map<String, IBypassHandler> datatable = new HashMap<>();

  protected BypassHandler(List<IBypassHandler> handlers) {
    handlers.forEach(this::registerHandler);
  }

  private void registerHandler(IBypassHandler handler) {
    for (String element : handler.getBypassList()) {
      datatable.put(element.toLowerCase(), handler);
    }
  }

  public static BypassHandler getInstance() {
    return SingletonHolder._instance;
  }

  public IBypassHandler getHandler(String command) {
    if (command.contains(" ")) {
      command = command.substring(0, command.indexOf(" "));
    }
    return datatable.get(command.toLowerCase());
  }

  private static class SingletonHolder {
    protected static final BypassHandler _instance = new BypassHandler(null);
  }
}
