package com.l2jserver.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.datapack.handlers.EffectMasterHandler;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.scripting.ScriptEngineManager;

public final class EffectHandler implements IHandler<Class<? extends AbstractEffect>, String> {
	private final Map<String, Class<? extends AbstractEffect>> _handlers;
	
	protected EffectHandler() {
		_handlers = new HashMap<>();
	}
	
	@Override
	public void registerHandler(Class<? extends AbstractEffect> handler) {
		_handlers.put(handler.getSimpleName(), handler);
	}
	
	@Override
	public synchronized void removeHandler(Class<? extends AbstractEffect> handler) {
		_handlers.remove(handler.getSimpleName());
	}
	
	@Override
	public Class<? extends AbstractEffect> getHandler(String name) {
		return _handlers.get(name);
	}
	
	@Override
	public int size() {
		return _handlers.size();
	}
	
	public void executeScript() throws Exception {
		ScriptEngineManager.getInstance().executeScript(EffectMasterHandler.class);
	}
	
	public static EffectHandler getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder {
		protected static final EffectHandler INSTANCE = new EffectHandler();
	}
}
