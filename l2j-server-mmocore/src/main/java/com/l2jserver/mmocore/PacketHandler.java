package com.l2jserver.mmocore;

import java.nio.ByteBuffer;

public interface PacketHandler<T extends MMOClient<?>> {

  ReceivablePacket<T> handlePacket(ByteBuffer buf, T client);
}
