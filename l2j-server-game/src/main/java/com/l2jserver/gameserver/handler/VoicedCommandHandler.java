package com.l2jserver.gameserver.handler;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VoicedCommandHandler {
  private final Map<String, IVoicedCommandHandler> datatable = new HashMap<>();

  protected VoicedCommandHandler(List<IVoicedCommandHandler> handlers) {
    handlers.forEach(this::registerHandler);
  }

  public void registerHandler(IVoicedCommandHandler handler) {
    for (String id : handler.getVoicedCommandList()) {
      datatable.put(id, handler);
    }
  }

  public IVoicedCommandHandler getHandler(String voicedCommand) {
    String command = voicedCommand;
    if (voicedCommand.contains(" ")) {
      command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
    }
    return datatable.get(command);
  }

  public int size() {
    return datatable.size();
  }

  public static VoicedCommandHandler getInstance() {
    return SingletonHolder.INSTANCE;
  }

  private static class SingletonHolder {
    protected static final VoicedCommandHandler INSTANCE = new VoicedCommandHandler(null);
  }
}
