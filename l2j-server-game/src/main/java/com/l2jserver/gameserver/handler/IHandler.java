package com.l2jserver.gameserver.handler;

public interface IHandler<K, V> {
	default void registerByClass(Class<?> clazz) throws Exception {
		final Object object = clazz.getDeclaredConstructor().newInstance();
		registerHandler((K) object);
	}
	
	/**
	 * Registers the handler.
	 * @param handler the handler
	 */
	void registerHandler(K handler);
	
	/**
	 * Removes the handler.
	 * @param handler the handler to remove
	 */
	void removeHandler(K handler);
	
	/**
	 * Gets the handler for the given object.
	 * @param val the object
	 * @return the handler
	 */
	K getHandler(V val);
	
	/**
	 * Gets the amount of handlers.
	 * @return the amount of handlers
	 */
	int size();
}
