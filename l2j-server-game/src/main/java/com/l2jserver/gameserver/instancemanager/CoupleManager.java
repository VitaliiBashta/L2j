package com.l2jserver.gameserver.instancemanager;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Couple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class CoupleManager {

  private static final Logger LOG = LogManager.getLogger(CoupleManager.class.getName());

  private final List<Couple> couples = new CopyOnWriteArrayList<>();

  private final ConnectionFactory connectionFactory;

  protected CoupleManager(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
    load();
  }

  private void load() {
    try (var con = connectionFactory.getConnection();
        var ps = con.createStatement();
        var rs = ps.executeQuery("SELECT id FROM mods_wedding ORDER BY id")) {
      while (rs.next()) {
        getCouples().add(new Couple(rs.getInt("id")));
      }
      LOG.info("Loaded: {} couples(s)", couples.size());
    } catch (Exception e) {
      throw new IllegalStateException("Exception: CoupleManager.load(): ", e);
    }
  }

  public List<Couple> getCouples() {
    return couples;
  }

  public static CoupleManager getInstance() {
    return SingletonHolder._instance;
  }

  public Couple getCouple(int coupleId) {
    int index = getCoupleIndex(coupleId);
    if (index >= 0) {
      return getCouples().get(index);
    }
    return null;
  }

  public int getCoupleIndex(int coupleId) {
    int i = 0;
    for (Couple temp : getCouples()) {
      if ((temp != null) && (temp.getId() == coupleId)) {
        return i;
      }
      i++;
    }
    return -1;
  }

  public void createCouple(L2PcInstance player1, L2PcInstance player2) {
    if ((player1 != null) && (player2 != null)) {
      if ((player1.getPartnerId() == 0) && (player2.getPartnerId() == 0)) {
        int player1id = player1.getObjectId();
        int player2id = player2.getObjectId();

        Couple couple = new Couple(player1, player2);
        getCouples().add(couple);
        player1.setPartnerId(player2id);
        player2.setPartnerId(player1id);
        player1.setCoupleId(couple.getId());
        player2.setCoupleId(couple.getId());
      }
    }
  }

  public void deleteCouple(int coupleId) {
    int index = getCoupleIndex(coupleId);
    Couple couple = getCouples().get(index);
    if (couple != null) {
      L2PcInstance player1 = L2World.getInstance().getPlayer(couple.getPlayer1Id());
      L2PcInstance player2 = L2World.getInstance().getPlayer(couple.getPlayer2Id());
      if (player1 != null) {
        player1.setPartnerId(0);
        player1.setMarried(false);
        player1.setCoupleId(0);
      }
      if (player2 != null) {
        player2.setPartnerId(0);
        player2.setMarried(false);
        player2.setCoupleId(0);
      }
      couple.divorce();
      getCouples().remove(index);
    }
  }

  private static class SingletonHolder {
    protected static final CoupleManager _instance = new CoupleManager(null);
  }
}
