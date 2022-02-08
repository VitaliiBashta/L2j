package com.l2jserver.gameserver.cache;

import com.l2jserver.gameserver.util.file.filter.HTMLFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.l2jserver.gameserver.config.Configuration.general;
import static com.l2jserver.gameserver.config.Configuration.server;
import static java.nio.charset.StandardCharsets.UTF_8;

public class HtmCache {

  private static final Logger LOG = LoggerFactory.getLogger(HtmCache.class);

  private static final HTMLFilter HTML_FILTER = new HTMLFilter();

  private static final Map<String, String> HTML_CACHE =
      Boolean.TRUE.equals(general().lazyCache()) ? new ConcurrentHashMap<>() : new HashMap<>();

  private int loadedFiles;

  private long bytesBuffLen;

  protected HtmCache() {
    reload();
  }

  public static HtmCache getInstance() {
    return SingletonHolder.INSTANCE;
  }

  public void reload() {
    reload(server().getDatapackRoot());
  }

  public void reload(File f) {
    if (Boolean.FALSE.equals(general().lazyCache())) {
      LOG.info("Html cache start...");
      parseDir(f);
      LOG.info("{} megabytes on {} files loaded", getMemoryUsage(), getLoadedFiles());
    } else {
      HTML_CACHE.clear();
      loadedFiles = 0;
      bytesBuffLen = 0;
      LOG.info("Running lazy cache.");
    }
  }

  public void reloadPath(File f) {
    parseDir(f);
    LOG.info("Reloaded specified path.");
  }

  public double getMemoryUsage() {
    return ((float) bytesBuffLen / 1048576);
  }

  public int getLoadedFiles() {
    return loadedFiles;
  }

  private void parseDir(File dir) {
    final File[] files = dir.listFiles();
    if (files != null) {
      for (File file : files) {
        if (!file.isDirectory()) {
          loadFile(file);
        } else {
          parseDir(file);
        }
      }
    }
  }

  public String loadFile(File file) {
    if (!HTML_FILTER.accept(file)) {
      return null;
    }

    String content = null;
    try (var fis = new FileInputStream(file);
        var bis = new BufferedInputStream(fis)) {
      final int bytes = bis.available();
      byte[] raw = new byte[bytes];

      bis.read(raw);
      content = new String(raw, UTF_8);
      content = content.replaceAll("(?s)<!--.*?-->", ""); // Remove html comments

      String oldContent = HTML_CACHE.put(file.getCanonicalPath(), content);
      if (oldContent == null) {
        bytesBuffLen += bytes;
        loadedFiles++;
      } else {
        bytesBuffLen = (bytesBuffLen - oldContent.length()) + bytes;
      }
    } catch (Exception e) {
      LOG.warn("Problem with htm file {}!", file, e);
    }
    return content;
  }

  public String getHtm(String prefix, String path) {
    final var newPath = Optional.ofNullable(prefix).orElse("") + path;
    var content = HTML_CACHE.get(newPath);
    if (Boolean.TRUE.equals(general().lazyCache()) && (content == null)) {
      content = loadFile(new File(server().getDatapackRoot(), newPath));
      if (content == null) {
        content = loadFile(new File(server().getScriptRoot(), newPath));
      }

      // If multilanguage content is not present, try default location.
      if (prefix != null && content == null) {
        content = loadFile(new File(server().getDatapackRoot(), path));
        if (content == null) {
          content = loadFile(new File(server().getScriptRoot(), path));
        }
      }
    }
    return content;
  }

  public boolean contains(String path) {
    return HTML_CACHE.containsKey(path);
  }

  /**
   * @param path The path to the HTM
   * @return {@code true} if the path targets a HTM or HTML file, {@code false} otherwise.
   */
  public boolean isLoadable(String path) {
    return HTML_FILTER.accept(new File(server().getDatapackRoot(), path));
  }

  private static class SingletonHolder {
    protected static final HtmCache INSTANCE = new HtmCache();
  }
}
