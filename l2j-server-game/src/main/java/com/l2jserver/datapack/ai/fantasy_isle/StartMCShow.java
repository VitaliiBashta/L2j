package com.l2jserver.datapack.ai.fantasy_isle;

import com.l2jserver.gameserver.instancemanager.QuestManager;

public class StartMCShow implements Runnable {

  private final QuestManager questManager;

  public StartMCShow(QuestManager questManager) {
    this.questManager = questManager;
  }

  @Override
  public void run() {
    questManager.getQuest("MC_Show").notifyEvent("Start", null, null);
  }
}
