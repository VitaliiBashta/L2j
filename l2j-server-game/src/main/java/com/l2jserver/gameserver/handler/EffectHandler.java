package com.l2jserver.gameserver.handler;

import com.l2jserver.datapack.handlers.EffectMasterHandler;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.scripting.ScriptEngineManager;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Service
public class EffectHandler implements IHandler<Class<? extends AbstractEffect>, String> {
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
	
	public void executeScript()  {
		ScriptEngineManager.getInstance().executeScript(EffectMasterHandler.class);
	}
	
	public static EffectHandler getInstance() {
		return SingletonHolder.INSTANCE;
	}

  /**
   * Creates an effect given the parameters.
   *
   * @param attachCond the attach condition
   * @param applyCond the apply condition
   * @param set the attributes
   * @param params the parameters
   * @return the new effect
   */
  public AbstractEffect createEffect(
      Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
    final String name = set.getString("name");
    final Class<? extends AbstractEffect> handler = getHandler(name);
    if (handler == null) {
      throw new IllegalArgumentException(
          " Requested unexistent effect handler: " + name + " in skill[" + set.getInt("id") + "]");
    }

    final Constructor<?> constructor;
    try {
      constructor =
          handler.getConstructor(Condition.class, Condition.class, StatsSet.class, StatsSet.class);
    } catch (NoSuchMethodException | SecurityException e) {
      throw new IllegalArgumentException(
          " Requested unexistent constructor for effect handler: "
              + name
              + " in skill["
              + set.getInt("id")
              + "] : "
              + e.getMessage());
    }

    try {
      return (AbstractEffect) constructor.newInstance(attachCond, applyCond, set, params);
    } catch (InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException e) {
      throw new IllegalArgumentException(
          "Unable to initialize effect handler: "
              + name
              + " in skill["
              + set.getInt("id")
              + "] : "
              + e.getMessage());
    }
  }

	private static final class SingletonHolder {
		protected static final EffectHandler INSTANCE = new EffectHandler();
	}
}
