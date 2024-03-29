package com.l2jserver.datapack.handlers.chathandlers;

import com.l2jserver.gameserver.handler.IChatHandler;
import com.l2jserver.gameserver.instancemanager.PetitionManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import org.springframework.stereotype.Service;

import static com.l2jserver.gameserver.config.Configuration.general;

@Service
public class ChatPetition implements IChatHandler {
  private static final int[] COMMAND_IDS = {6, 7};
  private final PetitionManager petitionManager;

  public ChatPetition(PetitionManager petitionManager) {
    this.petitionManager = petitionManager;
  }

  @Override
  public void handleChat(int type, L2PcInstance activeChar, String target, String text) {
    if (activeChar.isChatBanned() && general().getBanChatChannels().contains(type)) {
      activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED);
      return;
    }

    if (!petitionManager.isPlayerInConsultation(activeChar)) {
      activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_IN_PETITION_CHAT);
      return;
    }

    petitionManager.sendActivePetitionMessage(activeChar, text);
  }

  @Override
  public int[] getChatTypeList() {
    return COMMAND_IDS;
  }
}
