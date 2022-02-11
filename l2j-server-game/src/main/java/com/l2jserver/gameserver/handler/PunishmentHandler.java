package com.l2jserver.gameserver.handler;

import com.l2jserver.gameserver.model.punishment.PunishmentType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PunishmentHandler {
  private final Map<PunishmentType, IPunishmentHandler> handlers = new HashMap<>();

  protected PunishmentHandler(List<IPunishmentHandler> handlers) {
    handlers.forEach(this::registerHandler);
	}

  private void registerHandler(IPunishmentHandler handler) {
    handlers.put(handler.getType(), handler);
	}

	public IPunishmentHandler getHandler(PunishmentType val) {
    return handlers.get(val);
	}

	public static PunishmentHandler getInstance() {
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder {
    protected static final PunishmentHandler _instance = new PunishmentHandler(null);
	}
}
