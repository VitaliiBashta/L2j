package com.l2jserver.gameserver.scripting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class ScriptEngineManager {
  private static final Logger LOG = LoggerFactory.getLogger(ScriptEngineManager.class);

  private static final String MAIN = "main";

  private static final Class<?>[] ARG_MAIN = new Class[] {String[].class};

  public static ScriptEngineManager getInstance() {
    return SingletonHolder.INSTANCE;
  }

  public void executeScript(Class<?> clazz) {
    runInit(clazz);
  }

  private void runInit(Class<?> clazz) {
    final var mainMethod = findMethod(clazz, MAIN, ARG_MAIN);
    if (mainMethod == null) {
      LOG.warn("Unable to find main method in class {}!", clazz);
      return;
    }
    try {
      clazz.getConstructor().newInstance();
    } catch (InstantiationException
        | NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new IllegalStateException("Object cannot be instantiated:" + clazz, e);
    }
    //    mainMethod.invoke(null, MAIN_METHOD_ARGS);
  }

  private static Method findMethod(Class<?> clazz, String methodName, Class<?>[] args) {
    try {
      final var mainMethod = clazz.getMethod(methodName, args);
      final int modifiers = mainMethod.getModifiers();
      if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
        return mainMethod;
      }
    } catch (NoSuchMethodException ignored) {
    }
    return null;
  }

  private static class SingletonHolder {
    protected static final ScriptEngineManager INSTANCE = new ScriptEngineManager();
  }
}
