package com.l2jserver.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.gameserver.enums.InstanceType;
import org.springframework.stereotype.Service;

public class ActionHandler implements IHandler<IActionHandler, InstanceType> {
	private final Map<InstanceType, IActionHandler> _actions;
	
	public static ActionHandler getInstance() {
		return SingletonHolder._instance;
	}
	
	protected ActionHandler() {
		_actions = new HashMap<>();
	}
	
	@Override
	public void registerHandler(IActionHandler handler) {
		_actions.put(handler.getInstanceType(), handler);
	}
	
	@Override
	public synchronized void removeHandler(IActionHandler handler) {
		_actions.remove(handler.getInstanceType());
	}
	
	@Override
	public IActionHandler getHandler(InstanceType iType) {
		IActionHandler result = null;
		for (InstanceType t = iType; t != null; t = t.getParent()) {
			result = _actions.get(t);
			if (result != null) {
				break;
			}
		}
		return result;
	}
	
	@Override
	public int size() {
		return _actions.size();
	}
	
	private static class SingletonHolder {
		protected static final ActionHandler _instance = new ActionHandler();
	}
}