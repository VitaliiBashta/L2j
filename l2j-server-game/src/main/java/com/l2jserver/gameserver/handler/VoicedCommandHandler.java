package com.l2jserver.gameserver.handler;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VoicedCommandHandler implements IHandler<IVoicedCommandHandler, String> {
  private final Map<String, IVoicedCommandHandler> _datatable = new HashMap<>();

  protected VoicedCommandHandler(List<IVoicedCommandHandler> handlers) {
    handlers.forEach(this::registerHandler);
  }

  @Override
  public void registerHandler(IVoicedCommandHandler handler) {
    for (String id : handler.getVoicedCommandList()) {
      _datatable.put(id, handler);
    }
  }

  @Override
  public synchronized void removeHandler(IVoicedCommandHandler handler) {
    for (String id : handler.getVoicedCommandList()) {
      _datatable.remove(id);
    }
  }

  @Override
  public IVoicedCommandHandler getHandler(String voicedCommand) {
    String command = voicedCommand;
    if (voicedCommand.contains(" ")) {
      command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
    }
    return _datatable.get(command);
  }

  @Override
  public int size() {
    return _datatable.size();
  }

  public static VoicedCommandHandler getInstance() {
    return SingletonHolder.INSTANCE;
  }

  private static class SingletonHolder {
    protected static final VoicedCommandHandler INSTANCE = new VoicedCommandHandler();
  }
}
