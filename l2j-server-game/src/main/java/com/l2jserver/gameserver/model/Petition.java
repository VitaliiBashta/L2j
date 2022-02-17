package com.l2jserver.gameserver.model;

import com.l2jserver.gameserver.enums.PetitionState;
import com.l2jserver.gameserver.enums.PetitionType;
import com.l2jserver.gameserver.idfactory.IdFactory;
import com.l2jserver.gameserver.instancemanager.PetitionManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jserver.gameserver.network.serverpackets.PetitionVotePacket;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Petition {
  private final long submitTime = System.currentTimeMillis();
  private final int id;
  private final PetitionType _type;
  private final String content;
  private final List<CreatureSay> messageLog = new CopyOnWriteArrayList<>();
  private final L2PcInstance petitioner;
  private final PetitionManager petitionManager;
  private PetitionState state = PetitionState.PENDING;
  private L2PcInstance responder;

  public Petition(
      PetitionManager petitionManager,
      L2PcInstance petitioner,
      String petitionText,
      int petitionType) {
    this.petitionManager = petitionManager;
    id = IdFactory.getInstance().getNextId();
    _type = PetitionType.values()[--petitionType];
    content = petitionText;
    this.petitioner = petitioner;
  }

  public boolean addLogMessage(CreatureSay cs) {
    return messageLog.add(cs);
  }

  public List<CreatureSay> getLogMessages() {
    return messageLog;
  }

  public String getContent() {
    return content;
  }

  public long getSubmitTime() {
    return submitTime;
  }

  public PetitionState getState() {
    return state;
  }

  public void setState(PetitionState state) {
    this.state = state;
  }

  public String getTypeAsString() {
    return _type.toString().replace("_", " ");
  }

  public void sendPetitionerPacket(L2GameServerPacket responsePacket) {
    if ((getPetitioner() == null) || !getPetitioner().isOnline()) {
      // Allows petitioners to see the results of their petition when
      // they log back into the game.

      // endPetitionConsultation(PetitionState.Petitioner_Missing);
      return;
    }

    getPetitioner().sendPacket(responsePacket);
  }

  public L2PcInstance getPetitioner() {
    return petitioner;
  }

  public void sendResponderPacket(L2GameServerPacket responsePacket) {
    if ((getResponder() == null) || !getResponder().isOnline()) {
      endPetitionConsultation(PetitionState.RESPONDER_MISSING);
      return;
    }

    responder.sendPacket(responsePacket);
  }

  public boolean endPetitionConsultation(PetitionState endState) {
    state = endState;

    if ((responder != null) && responder.isOnline()) {
      if (endState == PetitionState.RESPONDER_REJECT) {
        petitioner.sendMessage("Your petition was rejected. Please try again later.");
      } else {
        // Ending petition consultation with <Player>.
        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PETITION_ENDED_WITH_C1);
        sm.addString(petitioner.getName());
        responder.sendPacket(sm);

        if (endState == PetitionState.PETITIONER_CANCEL) {
          // Receipt No. <ID> petition cancelled.
          sm = SystemMessage.getSystemMessage(SystemMessageId.RECENT_NO_S1_CANCELED);
          sm.addInt(id);
          responder.sendPacket(sm);
        }
      }
    }

    // End petition consultation and inform them, if they are still online. And if petitioner is
    // online, enable Evaluation button
    if ((petitioner != null) && getPetitioner().isOnline()) {
      petitioner.sendPacket(SystemMessageId.THIS_END_THE_PETITION_PLEASE_PROVIDE_FEEDBACK);
      petitioner.sendPacket(PetitionVotePacket.STATIC_PACKET);
    }

    petitionManager.getCompletedPetitions().put(id, this);
    return petitionManager.removePetition(id);
  }

  public L2PcInstance getResponder() {
    return responder;
  }

  public void setResponder(L2PcInstance respondingAdmin) {
    if (getResponder() != null) {
      return;
    }

    responder = respondingAdmin;
  }

  public int getId() {
    return id;
  }
}
