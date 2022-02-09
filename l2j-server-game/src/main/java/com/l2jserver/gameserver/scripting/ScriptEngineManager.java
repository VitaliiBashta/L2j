package com.l2jserver.gameserver.scripting;

import org.mdkt.compiler.InMemoryJavaCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;

import static com.l2jserver.gameserver.config.Configuration.general;
import static com.l2jserver.gameserver.config.Configuration.server;

/**
 * Script engine manager.
 *
 * @author KenM
 * @author Zoey76
 */
public final class ScriptEngineManager {
  private static final Logger LOG = LoggerFactory.getLogger(ScriptEngineManager.class);

  private static final String CLASS_PATH =
      server().getScriptRoot().getAbsolutePath()
          + System.getProperty("path.separator")
          + System.getProperty("java.class.path");

  private static final String MAIN = "main";

  private static final Object[] MAIN_METHOD_ARGS = new Object[] {new String[0]};

  private static final Class<?>[] ARG_MAIN = new Class[] {String[].class};

  private static String getClassForFile(Path script) {
    final String path = script.toAbsolutePath().toString();
    final String scpPath = server().getScriptRoot().getAbsolutePath();
    if (path.startsWith(scpPath)) {
      final int idx = path.lastIndexOf('.');
      return path.substring(scpPath.length() + 1, idx).replace('/', '.').replace('\\', '.');
    }
    return null;
  }

  private static void runMain(Class<?> clazz) {
    final var mainMethod = findMethod(clazz, MAIN, ARG_MAIN);
    if (mainMethod == null) {
      LOG.warn("Unable to find main method in class {}!", clazz);
      return;
    }

    try {
      mainMethod.invoke(null, MAIN_METHOD_ARGS);
    } catch (Exception ex) {
      LOG.error("Error loading script {}!", clazz);
    }
  }

  private static String readerToString(Reader reader) throws ScriptException {
    try (var in = new BufferedReader(reader)) {
      final var result = new StringBuilder();
      String line;
      while ((line = in.readLine()) != null) {
        result.append(line).append(System.lineSeparator());
      }
      return result.toString();
    } catch (IOException ex) {
      throw new ScriptException(ex);
    }
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

  public static ScriptEngineManager getInstance() {
    return SingletonHolder.INSTANCE;
  }

  private InMemoryJavaCompiler compiler() {
    return InMemoryJavaCompiler.newInstance() //
        .useOptions("-classpath", CLASS_PATH, "-g") //
        .ignoreWarnings();
  }

  public void executeScriptList(File list) throws Exception {
    if (general().noQuests()) {
      return;
    }

    if (!list.isFile()) {
      throw new IllegalArgumentException(
          "Argument must be an file containing a list of scripts to be loaded");
    }

    final var compiler = compiler();
    try (var fis = new FileInputStream(list);
        var isr = new InputStreamReader(fis);
        var lnr = new LineNumberReader(isr)) {
      String line;
      while ((line = lnr.readLine()) != null) {
        final var parts = line.trim().split("#");
        if ((parts.length <= 0) || parts[0].trim().isEmpty() || (parts[0].charAt(0) == '#')) {
          continue;
        }

        line = parts[0].trim();
        if (line.endsWith("/**")) {
          line = line.substring(0, line.length() - 3);
        } else if (line.endsWith("/*")) {
          line = line.substring(0, line.length() - 2);
        }

        final var file = new File(server().getScriptRoot(), line);
        if (file.isDirectory() && parts[0].endsWith("/**")) {
          executeAllScriptsInDirectory(compiler, file, true);
        } else if (file.isDirectory() && parts[0].endsWith("/*")) {
          executeAllScriptsInDirectory(compiler, file, false);
        } else if (file.isFile()) {
          addSource(compiler, file.toPath());
        } else {
          LOG.warn(
              "Failed loading: ({}) @ {}:{} - Reason: doesnt exists or is not a file.",
              file.getCanonicalPath(),
              list.getName(),
              lnr.getLineNumber());
        }
      }
    }

    compiler.compileAll().forEach((k, v) -> runMain(v));
  }

  private void executeAllScriptsInDirectory(
      InMemoryJavaCompiler compiler, File dir, boolean recurseDown) {
    if (!dir.isDirectory()) {
      throw new IllegalArgumentException(
          "The argument directory either doesnt exists or is not an directory.");
    }

    final var files = dir.listFiles();
    if (files == null) {
      return;
    }

    for (var file : files) {
      if (file.isDirectory() && recurseDown) {
        if (general().debug()) {
          LOG.info("Entering folder: {}", file.getName());
        }
        executeAllScriptsInDirectory(compiler, file, recurseDown);
      } else if (file.isFile()) {
        addSource(compiler, file.toPath());
      }
    }
  }

  public void executeScript(Class<?> clazz) {
    runMain(clazz);
  }

  public void addSource(InMemoryJavaCompiler compiler, Path file) {
    if (general().debug()) {
      LOG.info("Loading Script: {}", file.toAbsolutePath());
    }

    try (var fis = new FileInputStream(file.toFile());
        var isr = new InputStreamReader(fis);
        var reader = new BufferedReader(isr)) {
      compiler.addSource(getClassForFile(file), readerToString(reader));
    } catch (Exception ex) {
      LOG.warn("Error executing script!", ex);
    }
  }

  private static class SingletonHolder {
    protected static final ScriptEngineManager INSTANCE = new ScriptEngineManager();
  }
}
