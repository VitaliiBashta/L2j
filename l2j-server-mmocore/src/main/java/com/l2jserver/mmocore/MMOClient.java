package com.l2jserver.mmocore;

import java.nio.ByteBuffer;

public abstract class MMOClient<T extends MMOConnection<?>> {
	
	private final T _con;
	
	public MMOClient(T con) {
		_con = con;
	}
	
	public T getConnection() {
		return _con;
	}
	
	public abstract boolean decrypt(ByteBuffer buf, int size);
	
	public abstract boolean encrypt(ByteBuffer buf, int size);
	
	protected abstract void onDisconnection();
	
	protected abstract void onForcedDisconnection();
}
