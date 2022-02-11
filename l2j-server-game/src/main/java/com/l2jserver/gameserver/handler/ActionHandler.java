package com.l2jserver.gameserver.handler;

import com.l2jserver.gameserver.enums.InstanceType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActionHandler {
  private final Map<InstanceType, IActionHandler> _actions = new HashMap<>();

  protected ActionHandler(List<IActionHandler> handlers) {
    handlers.forEach(this::registerHandler);
  }

  public void registerHandler(IActionHandler handler) {
    _actions.put(handler.getInstanceType(), handler);
  }

  public static ActionHandler getInstance() {
    return SingletonHolder._instance;
  }

  public synchronized void removeHandler(IActionHandler handler) {
    _actions.remove(handler.getInstanceType());
  }

  public IActionHandler getHandler(InstanceType iType) {
    IActionHandler result = null;
    for (InstanceType t = iType; t != null; t = t.getParent()) {
      result = _actions.get(t);
      if (result != null) {
        break;
      }
    }
    return result;
  }

  public int size() {
    return _actions.size();
  }

  private static class SingletonHolder {
    protected static final ActionHandler _instance = new ActionHandler(List.of());
  }
}
