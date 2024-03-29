package com.l2jserver.gameserver.model.entity;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.instancemanager.DimensionalRiftManager;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.serverpackets.Earthquake;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import static com.l2jserver.gameserver.config.Configuration.general;

public class DimensionalRift {
  private static final long seconds_5 = 5000L;
  protected byte _type;
  protected L2Party _party;
  protected List<Byte> _completedRooms = new ArrayList<>();
  // private static final int MILLISECONDS_IN_MINUTE = 60000;
  protected byte jumps_current = 0;
  protected byte _chosenRoom;
  protected List<L2PcInstance> _deadPlayers = new CopyOnWriteArrayList<>();
  protected List<L2PcInstance> _revivedInWaitingRoom = new CopyOnWriteArrayList<>();
  private Timer teleporterTimer;
  private TimerTask teleporterTimerTask;
  private Timer spawnTimer;
  private TimerTask spawnTimerTask;
  private Future<?> earthQuakeTask;
  private boolean _hasJumped = false;
  private boolean isBossRoom = false;

  public DimensionalRift(L2Party party, byte type, byte room) {
    DimensionalRiftManager.getInstance().getRoom(type, room).setPartyInside(true);
    _type = type;
    _party = party;
    _chosenRoom = room;
    Location coords = getRoomCoord(room);
    party.setDimensionalRift(this);
    for (L2PcInstance p : party.getMembers()) {
      final Quest riftQuest = QuestManager.getInstance().getQuest(635);
      if (riftQuest != null) {
        QuestState qs = p.getQuestState(riftQuest.getName());
        if (qs == null) {
          qs = riftQuest.newQuestState(p);
        }
        if (!qs.isStarted()) {
          qs.startQuest();
        }
      }
      p.teleToLocation(coords);
    }
    createSpawnTimer(_chosenRoom);
    createTeleporterTimer(true);
  }

  public byte getType() {
    return _type;
  }

  public byte getCurrentRoom() {
    return _chosenRoom;
  }

  protected void createTeleporterTimer(final boolean reasonTP) {
    if (_party == null) {
      return;
    }

    if (teleporterTimerTask != null) {
      teleporterTimerTask.cancel();
      teleporterTimerTask = null;
    }

    if (teleporterTimer != null) {
      teleporterTimer.cancel();
      teleporterTimer = null;
    }

    if (earthQuakeTask != null) {
      earthQuakeTask.cancel(false);
      earthQuakeTask = null;
    }

    teleporterTimer = new Timer();
    teleporterTimerTask =
        new TimerTask() {
          @Override
          public void run() {
            if (_chosenRoom > -1) {
              DimensionalRiftManager.getInstance()
                  .getRoom(_type, _chosenRoom)
                  .unspawn()
                  .setPartyInside(false);
            }

            if (reasonTP
                && (jumps_current < getMaxJumps())
                && (_party.getMemberCount() > _deadPlayers.size())) {
              jumps_current++;

              _completedRooms.add(_chosenRoom);
              _chosenRoom = -1;

              for (L2PcInstance p : _party.getMembers()) {
                if (!_revivedInWaitingRoom.contains(p)) {
                  teleportToNextRoom(p);
                }
              }
              createTeleporterTimer(true);
              createSpawnTimer(_chosenRoom);
            } else {
              for (L2PcInstance p : _party.getMembers()) {
                if (!_revivedInWaitingRoom.contains(p)) {
                  teleportToWaitingRoom(p);
                }
              }
              killRift();
              cancel();
            }
          }
        };

    if (reasonTP) {
      long jumpTime = calcTimeToNextJump();
      teleporterTimer.schedule(teleporterTimerTask, jumpTime); // Teleporter task, 8-10 minutes

      earthQuakeTask =
          ThreadPoolManager.getInstance()
              .scheduleGeneral(
                  () -> {
                    for (L2PcInstance p : _party.getMembers()) {
                      if (!_revivedInWaitingRoom.contains(p)) {
                        p.sendPacket(new Earthquake(p.getX(), p.getY(), p.getZ(), 65, 9));
                      }
                    }
                  },
                  jumpTime - 7000);
    } else {
      teleporterTimer.schedule(teleporterTimerTask, seconds_5); // incorrect party member invited.
    }
  }

  public void createSpawnTimer(final byte room) {
    if (spawnTimerTask != null) {
      spawnTimerTask.cancel();
      spawnTimerTask = null;
    }

    if (spawnTimer != null) {
      spawnTimer.cancel();
      spawnTimer = null;
    }

    spawnTimer = new Timer();
    spawnTimerTask =
        new TimerTask() {
          @Override
          public void run() {
            DimensionalRiftManager.getInstance().getRoom(_type, room).spawn();
          }
        };

    spawnTimer.schedule(spawnTimerTask, general().getRiftSpawnDelay());
  }

  public void partyMemberInvited() {
    createTeleporterTimer(false);
  }

  public void partyMemberExited(L2PcInstance player) {
    _deadPlayers.remove(player);

    _revivedInWaitingRoom.remove(player);

    if ((_party.getMemberCount() < general().getRiftMinPartySize())
        || (_party.getMemberCount() == 1)) {
      for (L2PcInstance p : _party.getMembers()) {
        teleportToWaitingRoom(p);
      }
      killRift();
    }
  }

  protected void teleportToWaitingRoom(L2PcInstance player) {
    DimensionalRiftManager.getInstance().teleportToWaitingRoom(player);
    final Quest riftQuest = QuestManager.getInstance().getQuest(635);
    if (riftQuest != null) {
      final QuestState qs = player.getQuestState(riftQuest.getName());
      if ((qs != null) && qs.isCond(1)) {
        qs.exitQuest(true, true);
      }
    }
  }

  public void killRift() {
    _completedRooms.clear();

    if (_party != null) {
      _party.setDimensionalRift(null);
    }

    _party = null;
    _revivedInWaitingRoom = null;
    _deadPlayers = null;

    if (earthQuakeTask != null) {
      earthQuakeTask.cancel(false);
      earthQuakeTask = null;
    }

    DimensionalRiftManager.getInstance()
        .getRoom(_type, _chosenRoom)
        .unspawn()
        .setPartyInside(false);
    DimensionalRiftManager.getInstance().killRift(this);
  }

  public void manualTeleport(L2PcInstance player, L2Npc npc) {
    if (!player.isInParty() || !player.getParty().isInDimensionalRift()) {
      return;
    }

    if (player.getObjectId() != player.getParty().getLeaderObjectId()) {
      DimensionalRiftManager.getInstance()
          .showHtmlFile(player, "data/html/seven_signs/rift/NotPartyLeader.htm", npc);
      return;
    }

    if (_hasJumped) {
      DimensionalRiftManager.getInstance()
          .showHtmlFile(player, "data/html/seven_signs/rift/AlreadyTeleported.htm", npc);
      return;
    }

    _hasJumped = true;
    DimensionalRiftManager.getInstance()
        .getRoom(_type, _chosenRoom)
        .unspawn()
        .setPartyInside(false);
    _completedRooms.add(_chosenRoom);
    _chosenRoom = -1;

    for (L2PcInstance p : _party.getMembers()) {
      teleportToNextRoom(p);
    }

    DimensionalRiftManager.getInstance().getRoom(_type, _chosenRoom).setPartyInside(true);

    createSpawnTimer(_chosenRoom);
    createTeleporterTimer(true);
  }

  public void manualExitRift(L2PcInstance player, L2Npc npc) {
    if (!player.isInParty() || !player.getParty().isInDimensionalRift()) {
      return;
    }

    if (player.getObjectId() != player.getParty().getLeaderObjectId()) {
      DimensionalRiftManager.getInstance()
          .showHtmlFile(player, "data/html/seven_signs/rift/NotPartyLeader.htm", npc);
      return;
    }

    for (L2PcInstance p : player.getParty().getMembers()) {
      teleportToWaitingRoom(p);
    }
    killRift();
  }

  protected void teleportToNextRoom(L2PcInstance player) {
    if (_chosenRoom == -1) {
      List<Byte> emptyRooms;
      do {
        emptyRooms = DimensionalRiftManager.getInstance().getFreeRooms(_type);
        // Do not tp in the same room a second time
        emptyRooms.removeAll(_completedRooms);
        // If no room left, find any empty
        if (emptyRooms.isEmpty()) {
          emptyRooms = DimensionalRiftManager.getInstance().getFreeRooms(_type);
        }
        _chosenRoom = emptyRooms.get(Rnd.get(1, emptyRooms.size()) - 1);
      } while (DimensionalRiftManager.getInstance().getRoom(_type, _chosenRoom).isPartyInside());
    }

    DimensionalRiftManager.getInstance().getRoom(_type, _chosenRoom).setPartyInside(true);
    checkBossRoom(_chosenRoom);
    player.teleToLocation(getRoomCoord(_chosenRoom));
  }

  public Timer getTeleportTimer() {
    return teleporterTimer;
  }

  public void setTeleportTimer(Timer t) {
    teleporterTimer = t;
  }

  public TimerTask getTeleportTimerTask() {
    return teleporterTimerTask;
  }

  public void setTeleportTimerTask(TimerTask tt) {
    teleporterTimerTask = tt;
  }

  public Timer getSpawnTimer() {
    return spawnTimer;
  }

  public void setSpawnTimer(Timer t) {
    spawnTimer = t;
  }

  public TimerTask getSpawnTimerTask() {
    return spawnTimerTask;
  }

  public void setSpawnTimerTask(TimerTask st) {
    spawnTimerTask = st;
  }

  private long calcTimeToNextJump() {
    int time = Rnd.get(general().getAutoJumpsDelayMin(), general().getAutoJumpsDelayMax());
    if (isBossRoom) {
      return (long) (time * general().getBossRoomTimeMultiply());
    }
    return time;
  }

  public void memberDead(L2PcInstance player) {
    if (!_deadPlayers.contains(player)) {
      _deadPlayers.add(player);
    }
  }

  public void memberResurrected(L2PcInstance player) {
    _deadPlayers.remove(player);
  }

  public void usedTeleport(L2PcInstance player) {
    if (!_revivedInWaitingRoom.contains(player)) {
      _revivedInWaitingRoom.add(player);
    }

    if (!_deadPlayers.contains(player)) {
      _deadPlayers.add(player);
    }

    if ((_party.getMemberCount() - _revivedInWaitingRoom.size())
        < general().getRiftMinPartySize()) {
      // int pcm = _party.getMemberCount();
      // int rev = revivedInWaitingRoom.size();
      // int min = Config.RIFT_MIN_PARTY_SIZE;

      for (L2PcInstance p : _party.getMembers()) {
        if ((p != null) && !_revivedInWaitingRoom.contains(p)) {
          teleportToWaitingRoom(p);
        }
      }
      killRift();
    }
  }

  public List<L2PcInstance> getDeadMemberList() {
    return _deadPlayers;
  }

  public List<L2PcInstance> getRevivedAtWaitingRoom() {
    return _revivedInWaitingRoom;
  }

  public void checkBossRoom(byte room) {
    isBossRoom = DimensionalRiftManager.getInstance().getRoom(_type, room).isBossRoom();
  }

  public Location getRoomCoord(byte room) {
    return DimensionalRiftManager.getInstance().getRoom(_type, room).getTeleportCoordinates();
  }

  public int getMaxJumps() {
    if ((general().getMaxRiftJumps() <= 8) && (general().getMaxRiftJumps() >= 1)) {
      return general().getMaxRiftJumps();
    }
    return 4;
  }
}
