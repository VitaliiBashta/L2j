/*
 * Copyright © 2004-2021 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.handler;

import com.l2jserver.gameserver.model.skills.targets.TargetType;

import java.util.HashMap;
import java.util.Map;

/** @author UnAfraid */
public class TargetHandler implements IHandler<TargetTypeHandler, Enum<TargetType>> {
  private final Map<Enum<TargetType>, TargetTypeHandler> _datatable;

	protected TargetHandler() {
		_datatable = new HashMap<>();
	}

  @Override
  public void registerHandler(TargetTypeHandler handler) {
		_datatable.put(handler.getTargetType(), handler);
	}

  @Override
  public synchronized void removeHandler(TargetTypeHandler handler) {
		_datatable.remove(handler.getTargetType());
	}

  @Override
  public TargetTypeHandler getHandler(Enum<TargetType> targetType) {
		return _datatable.get(targetType);
	}
	
	@Override
	public int size() {
		return _datatable.size();
	}
	
	public static TargetHandler getInstance() {
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder {
		protected static final TargetHandler _instance = new TargetHandler();
	}
}
