
package com.l2jserver.datapack.ai.fantasy_isle;

import com.l2jserver.gameserver.instancemanager.QuestManager;

/**
 * Start MC Show.
 */
public class StartMCShow implements Runnable {
	@Override
	public void run() {
		QuestManager.getInstance().getQuest("MC_Show").notifyEvent("Start", null, null);
	}
}
