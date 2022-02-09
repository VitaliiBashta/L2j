package com.l2jserver.gameserver.agathion.repository;

import com.l2jserver.gameserver.agathion.Agathion;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class AgathionRepository {

  private final Map<Integer, Agathion> agathions =
      new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
          put(1539, new Agathion(1539, 539, 20818, 100000, 100000));
          put(1540, new Agathion(1540, 540, 20820, 100000, 100000));
          put(1541, new Agathion(1541, 541, 20822, 100000, 100000));
          put(1542, new Agathion(1542, 542, 20824, 100000, 100000));
          put(1543, new Agathion(1543, 543, 20826, 100000, 100000));
          put(1544, new Agathion(1544, 544, 20828, 100000, 100000));
          put(1545, new Agathion(1545, 545, 20830, 100000, 100000));
          put(1546, new Agathion(1546, 546, 20832, 100000, 100000));
          put(1547, new Agathion(1547, 547, 20834, 100000, 100000));
          put(1548, new Agathion(1548, 548, 20836, 100000, 100000));
          put(1549, new Agathion(1549, 549, 20838, 100000, 100000));
          put(1550, new Agathion(1550, 550, 20840, 100000, 100000));
          put(1576, new Agathion(1576, 576, 20983, 1000, 1000));
          put(1577, new Agathion(1577, 577, 20984, 1000, 1000));
          put(1578, new Agathion(1578, 578, 20985, 1000, 1000));
          put(1579, new Agathion(1579, 579, 20986, 1000, 1000));
          put(1580, new Agathion(1580, 580, 20987, 1000, 1000));
          put(1581, new Agathion(1581, 581, 20988, 1000, 1000));
          put(1582, new Agathion(1582, 582, 20989, 1000, 1000));
          put(1583, new Agathion(1583, 583, 20990, 1000, 1000));
          put(1584, new Agathion(1584, 584, 20991, 1000, 1000));
        }
      };

  private final Map<Integer, Agathion> agathionItems =
      agathions.entrySet().stream()
          .collect(toMap(e -> e.getValue().getItemId(), Map.Entry::getValue));

  public static AgathionRepository getInstance() {
    return SingletonHolder.INSTANCE;
  }

  public Agathion getByNpcId(int npcId) {
    return agathions.get(npcId);
  }

  public Agathion getByItemId(int itemId) {
    return agathionItems.get(itemId);
  }

  private static class SingletonHolder {
    static final AgathionRepository INSTANCE = new AgathionRepository();
  }
}
