package com.l2jserver.gameserver.handler;

import com.l2jserver.gameserver.enums.InstanceType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActionShiftHandler {
  private final Map<InstanceType, IActionShiftHandler> _actionsShift = new HashMap<>();

  public ActionShiftHandler(List<IActionShiftHandler> handlers) {
    handlers.forEach(this::registerHandler);
	}

  private void registerHandler(IActionShiftHandler handler) {
		_actionsShift.put(handler.getInstanceType(), handler);
	}

	public IActionShiftHandler getHandler(InstanceType iType) {
		IActionShiftHandler result = null;
		for (InstanceType t = iType; t != null; t = t.getParent()) {
			result = _actionsShift.get(t);
			if (result != null) {
				break;
			}
		}
		return result;
	}
	

	public static ActionShiftHandler getInstance() {
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder {
    protected static final ActionShiftHandler _instance = new ActionShiftHandler(null);
	}
}