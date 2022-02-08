package com.l2jserver.gameserver.network.loginserverpackets;

import com.l2jserver.commons.network.BaseRecievePacket;

public class InitLS extends BaseRecievePacket {

  private final byte[] key;

  public InitLS(byte[] decrypt) {
    super(decrypt);
    int size = readD();
    key = readB(size);
  }

  public byte[] getRSAKey() {
    return key;
  }
}
