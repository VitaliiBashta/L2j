package com.l2jserver.gameserver.taskmanager;

import com.l2jserver.gameserver.taskmanager.TaskManager.ExecutedTask;

import java.util.concurrent.ScheduledFuture;

public abstract class Task {
	
	public void initializate() {
	}
	
	public ScheduledFuture<?> launchSpecial(ExecutedTask instance) {
		return null;
	}
	
	public abstract String getName();
	
	public abstract void onTimeElapsed(ExecutedTask task);
	
	public void onDestroy() {
		
	}
}
