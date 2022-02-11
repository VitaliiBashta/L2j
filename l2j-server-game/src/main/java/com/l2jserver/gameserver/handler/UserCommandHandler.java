package com.l2jserver.gameserver.handler;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserCommandHandler {
  private final Map<Integer, IUserCommandHandler> datatable = new HashMap<>();

  public UserCommandHandler(List<IUserCommandHandler> handlers) {
    handlers.forEach(this::registerHandler);
	}

  private void registerHandler(IUserCommandHandler handler) {
		int[] ids = handler.getUserCommandList();
		for (int id : ids) {
      datatable.put(id, handler);
		}
	}
	

	public IUserCommandHandler getHandler(Integer userCommand) {
    return datatable.get(userCommand);
	}
	

	public static UserCommandHandler getInstance() {
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder {
    protected static final UserCommandHandler _instance = new UserCommandHandler(null);
	}
}
