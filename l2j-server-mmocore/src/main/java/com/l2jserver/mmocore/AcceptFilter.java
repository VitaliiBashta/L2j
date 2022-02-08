package com.l2jserver.mmocore;

import java.nio.channels.SocketChannel;

public interface AcceptFilter {
	
	boolean accept(SocketChannel sc);
}
