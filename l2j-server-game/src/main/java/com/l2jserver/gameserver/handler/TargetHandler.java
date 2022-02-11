package com.l2jserver.gameserver.handler;

import com.l2jserver.gameserver.model.skills.targets.TargetType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TargetHandler {
  private final Map<Enum<TargetType>, TargetTypeHandler> datatable = new HashMap<>();

  protected TargetHandler(List<TargetTypeHandler> handlers) {
    handlers.forEach(this::registerHandler);
  }

  public void registerHandler(TargetTypeHandler handler) {
    datatable.put(handler.getTargetType(), handler);
  }

  public static TargetHandler getInstance() {
    return SingletonHolder._instance;
  }

  public TargetTypeHandler getHandler(Enum<TargetType> targetType) {
    return datatable.get(targetType);
  }

  private static class SingletonHolder {
    protected static final TargetHandler _instance = new TargetHandler(null);
  }
}
