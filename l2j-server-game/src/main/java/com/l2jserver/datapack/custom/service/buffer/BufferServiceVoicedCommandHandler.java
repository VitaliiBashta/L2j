package com.l2jserver.datapack.custom.service.buffer;

import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

@Service
public class BufferServiceVoicedCommandHandler implements IVoicedCommandHandler {

  private static final String[] COMMANDS =
      new String[] {Configuration.bufferService().getVoicedCommand()};
  private final BufferService bufferService;

  public BufferServiceVoicedCommandHandler(BufferService bufferService) {
    this.bufferService = bufferService;
  }

  @Override
  public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params) {
    bufferService.executeCommand(activeChar, null, params);
    return true;
  }

  @Override
  public String[] getVoicedCommandList() {
    return COMMANDS;
  }
}
