package com.l2jserver.mmocore;

import java.nio.ByteBuffer;

public abstract class AbstractPacket<T extends MMOClient<?>> {

  protected ByteBuffer _buf;

  protected T _client;

  public final T getClient() {
    return _client;
  }
}
