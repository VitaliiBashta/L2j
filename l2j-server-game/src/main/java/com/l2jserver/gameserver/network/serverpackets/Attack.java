package com.l2jserver.gameserver.network.serverpackets;

import com.l2jserver.gameserver.model.Hit;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Attack extends L2GameServerPacket {
  private final int _attackerObjId;
  private final boolean _soulshot;
  private final int _ssGrade;
  private final Location _attackerLoc;
  private final Location _targetLoc;
  private final List<Hit> _hits = new ArrayList<>();

  public Attack(L2Character attacker, L2Character target, boolean useShots, int ssGrade) {
    _attackerObjId = attacker.getObjectId();
    _soulshot = useShots;
    _ssGrade = ssGrade;
    _attackerLoc = new Location(attacker);
    _targetLoc = new Location(target);
  }

  /**
   * Adds hit to the attack (Attacks such as dual dagger/sword/fist has two hits)
   */
  public void addHit(L2Character target, int damage, boolean miss, boolean crit, byte shld) {
    _hits.add(new Hit(target, damage, miss, crit, shld, _soulshot, _ssGrade));
  }

  /**
   * @return {@code true} if current attack contains at least 1 hit.
   */
  public boolean hasHits() {
    return !_hits.isEmpty();
  }

  /**
   * @return {@code true} if attack has soul shot charged.
   */
  public boolean hasSoulshot() {
    return _soulshot;
  }

  @Override
  protected final void writeImpl() {
    final Iterator<Hit> it = _hits.iterator();
    writeC(0x33);

    writeD(_attackerObjId);
    writeHit(it.next());
    writeLoc(_attackerLoc);

    writeH(_hits.size() - 1);
    while (it.hasNext()) {
      writeHit(it.next());
    }

    writeLoc(_targetLoc);
  }

  /**
   * Writes current hit
   */
  private void writeHit(Hit hit) {
    writeD(hit.getTargetId());
    writeD(hit.getDamage());
    writeC(hit.getFlags());
  }
}
