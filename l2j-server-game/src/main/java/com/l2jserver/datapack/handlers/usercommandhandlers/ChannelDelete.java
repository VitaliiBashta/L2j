package com.l2jserver.datapack.handlers.usercommandhandlers;

import com.l2jserver.gameserver.handler.IUserCommandHandler;
import com.l2jserver.gameserver.model.L2CommandChannel;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.springframework.stereotype.Service;

import static com.l2jserver.gameserver.network.SystemMessageId.COMMAND_CHANNEL_DISBANDED;

@Service
public class ChannelDelete implements IUserCommandHandler {
  private static final int[] COMMAND_IDS = {93};

  @Override
  public boolean useUserCommand(int id, L2PcInstance activeChar) {
    if (id != COMMAND_IDS[0]) {
      return false;
    }

    if (activeChar.isInParty()) {
      if (activeChar.getParty().isLeader(activeChar)
          && activeChar.getParty().isInCommandChannel()
          && activeChar.getParty().getCommandChannel().getLeader().equals(activeChar)) {
        L2CommandChannel channel = activeChar.getParty().getCommandChannel();

        channel.broadcastMessage(COMMAND_CHANNEL_DISBANDED);

        channel.disbandChannel();
        return true;
      }
    }

    return false;
  }

  @Override
  public int[] getUserCommandList() {
    return COMMAND_IDS;
  }
}
