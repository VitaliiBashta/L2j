package com.l2jserver.gameserver.network.serverpackets;

import com.l2jserver.gameserver.model.interfaces.IPositionable;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.network.L2GameClient;
import com.l2jserver.mmocore.SendablePacket;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class L2GameServerPacket extends SendablePacket<L2GameClient> {
  protected static final Logger _log = Logger.getLogger(L2GameServerPacket.class.getName());
  private static final int[] PAPERDOLL_ORDER =
          new int[]{
                  Inventory.PAPERDOLL_UNDER,
                  Inventory.PAPERDOLL_REAR,
                  Inventory.PAPERDOLL_LEAR,
                  Inventory.PAPERDOLL_NECK,
                  Inventory.PAPERDOLL_RFINGER,
                  Inventory.PAPERDOLL_LFINGER,
                  Inventory.PAPERDOLL_HEAD,
                  Inventory.PAPERDOLL_RHAND,
                  Inventory.PAPERDOLL_LHAND,
                  Inventory.PAPERDOLL_GLOVES,
                  Inventory.PAPERDOLL_CHEST,
                  Inventory.PAPERDOLL_LEGS,
                  Inventory.PAPERDOLL_FEET,
                  Inventory.PAPERDOLL_CLOAK,
                  Inventory.PAPERDOLL_RHAND,
                  Inventory.PAPERDOLL_HAIR,
                  Inventory.PAPERDOLL_HAIR2,
                  Inventory.PAPERDOLL_RBRACELET,
                  Inventory.PAPERDOLL_LBRACELET,
                  Inventory.PAPERDOLL_DECO1,
                  Inventory.PAPERDOLL_DECO2,
                  Inventory.PAPERDOLL_DECO3,
                  Inventory.PAPERDOLL_DECO4,
                  Inventory.PAPERDOLL_DECO5,
                  Inventory.PAPERDOLL_DECO6,
                  Inventory.PAPERDOLL_BELT
          };
  private boolean invisible = false;

  /**
   * @return True if packet originated from invisible character.
   */
  public boolean isInvisible() {
    return invisible;
  }

  /**
   * Set "invisible" boolean flag in the packet.<br>
   * Packets from invisible characters will not be broadcasted to players.
   */
  public void setInvisible(boolean b) {
    invisible = b;
  }

  /**
   * Writes 3 D (int32) with current location x, y, z
   */
  protected void writeLoc(IPositionable loc) {
    writeD(loc.getX());
    writeD(loc.getY());
    writeD(loc.getZ());
  }

  protected int[] getPaperdollOrder() {
    return PAPERDOLL_ORDER;
  }

  @Override
  protected void write() {
    try {
      writeImpl();
    } catch (Exception e) {
      _log.log(
              Level.SEVERE,
              "Client: "
                      + getClient().toString()
                      + " - Failed writing: "
                      + getClass().getSimpleName()
                      + " ; "
                      + e.getMessage(),
              e);
    }
  }

  protected abstract void writeImpl();

  public void runImpl() {
  }
}
