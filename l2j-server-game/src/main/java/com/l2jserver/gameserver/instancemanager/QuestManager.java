package com.l2jserver.gameserver.instancemanager;

import com.l2jserver.commons.util.Util;
import com.l2jserver.gameserver.model.quest.Quest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.l2jserver.gameserver.config.Configuration.general;

@Service
public class QuestManager {

  private static final Logger LOG = LoggerFactory.getLogger(QuestManager.class);

  /** Map containing all the quests. */
  private final Map<String, Quest> quests = new ConcurrentHashMap<>();
  /** Map containing all the scripts. */
  private final Map<String, Quest> scripts = new ConcurrentHashMap<>();

  protected QuestManager() {
    // Prevent initialization.
  }

  public static QuestManager getInstance() {
    return SingletonHolder.INSTANCE;
  }

  public boolean reload(String questFolder) {
    final Quest q = getQuest(questFolder);
    if (q == null) {
      return false;
    }
    return q.reload();
  }

  /**
   * Gets a quest by name.<br>
   * <i>For backwards compatibility, verifies scripts with the given name if the quest is not
   * found.</i>
   *
   * @param name the quest name
   * @return the quest
   */
  public Quest getQuest(String name) {
    if (quests.containsKey(name)) {
      return quests.get(name);
    }
    return scripts.get(name);
  }

  /**
   * Reloads a the quest by ID.
   *
   * @param questId the ID of the quest to be reloaded
   * @return {@code true} if reload was successful, {@code false} otherwise
   */
  public boolean reload(int questId) {
    final Quest q = getQuest(questId);
    if (q == null) {
      return false;
    }
    return q.reload();
  }

  /** Gets a quest by ID. */
  public Quest getQuest(int questId) {
    for (Quest q : quests.values()) {
      if (q.getId() == questId) {
        return q;
      }
    }
    return null;
  }

  /** Calls {@link Quest#saveGlobalData()} in all quests and scripts. */
  public void save() {
    // Save quests.
    for (Quest quest : quests.values()) {
      quest.saveGlobalData();
    }

    // Save scripts.
    for (Quest script : scripts.values()) {
      script.saveGlobalData();
    }
  }

  /** Adds a new quest. */
  public void addQuest(Quest quest) {
    if (quest == null) {
      throw new IllegalArgumentException("Quest argument cannot be null");
    }

    quests.put(quest.getName(), quest);

    if (general().showQuestsLoadInLogs()) {
      final String questName =
          quest.getName().contains("_")
              ? quest.getName().substring(quest.getName().indexOf('_') + 1)
              : quest.getName();
      LOG.info("Loaded quest {}.", Util.splitWords(questName));
    }
  }

  /** Removes a script. */
  public boolean removeScript(Quest script) {
    if (quests.containsKey(script.getName())) {
      quests.remove(script.getName());
      return true;
    } else if (scripts.containsKey(script.getName())) {
      scripts.remove(script.getName());
      return true;
    }
    return false;
  }

  public Map<String, Quest> getQuests() {
    return quests;
  }

  /**
   * Gets all the registered scripts.
   *
   * @return all the scripts
   */
  public Map<String, Quest> getScripts() {
    return scripts;
  }

  /**
   * Adds a script.
   *
   * @param script the script to be added
   */
  public void addScript(Quest script) {
    final Quest old = scripts.put(script.getClass().getSimpleName(), script);
    if (old != null) {
      old.unload();
      LOG.info("Replaced script {} with a new version.", old);
    }

    if (general().showQuestsLoadInLogs()) {
      LOG.info("Loaded script {}.", Util.splitWords(script.getClass().getSimpleName()));
    }
  }

  private static class SingletonHolder {
    protected static final QuestManager INSTANCE = new QuestManager();
  }
}
