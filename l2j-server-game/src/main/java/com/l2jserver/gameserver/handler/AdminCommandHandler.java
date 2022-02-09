package com.l2jserver.gameserver.handler;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminCommandHandler implements IHandler<IAdminCommandHandler, String> {
  private final Map<String, IAdminCommandHandler> datatable = new HashMap<>();

  public AdminCommandHandler(List<IAdminCommandHandler> handlers) {
    handlers.forEach(this::registerHandler);
  }

  public static AdminCommandHandler getInstance() {
    return SingletonHolder._instance;
  }

  @Override
  public void registerHandler(IAdminCommandHandler handler) {
    String[] ids = handler.getAdminCommandList();
    for (String id : ids) {
      datatable.put(id, handler);
    }
  }

  @Override
  public synchronized void removeHandler(IAdminCommandHandler handler) {
    String[] ids = handler.getAdminCommandList();
    for (String id : ids) {
      datatable.remove(id);
    }
  }

  @Override
  public IAdminCommandHandler getHandler(String adminCommand) {
    String command = adminCommand;
    if (adminCommand.contains(" ")) {
      command = adminCommand.substring(0, adminCommand.indexOf(" "));
    }
    return datatable.get(command);
  }

  @Override
  public int size() {
    return datatable.size();
  }

  private static class SingletonHolder {
    protected static final AdminCommandHandler _instance = new AdminCommandHandler(List.of());
  }
}
