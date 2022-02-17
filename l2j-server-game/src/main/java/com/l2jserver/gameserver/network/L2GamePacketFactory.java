package com.l2jserver.gameserver.network;

import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.instancemanager.PetitionManager;
import com.l2jserver.gameserver.network.clientpackets.EnterWorld;
import com.l2jserver.gameserver.network.clientpackets.Logout;
import com.l2jserver.gameserver.network.clientpackets.RequestPetition;
import com.l2jserver.gameserver.network.clientpackets.RequestPetitionCancel;
import com.l2jserver.gameserver.taskmanager.AttackStanceTaskManager;
import org.springframework.stereotype.Service;

@Service
public class L2GamePacketFactory {

  private final AttackStanceTaskManager attackStanceTaskManager;
  private final AdminData adminData;
  private final PetitionManager petitionManager;

  public L2GamePacketFactory(
      AttackStanceTaskManager attackStanceTaskManager,
      AdminData adminData,
      PetitionManager petitionManager) {
    this.attackStanceTaskManager = attackStanceTaskManager;
    this.adminData = adminData;
    this.petitionManager = petitionManager;
  }

  public Logout logout() {
    return new Logout(attackStanceTaskManager);
  }

  public RequestPetition requestPetition() {
    return new RequestPetition(adminData, petitionManager);
  }

  public RequestPetitionCancel requestPetitionCancel() {
    return new RequestPetitionCancel(petitionManager);
  }

  public EnterWorld enterWorld() {
    return new EnterWorld(petitionManager);
  }
}
