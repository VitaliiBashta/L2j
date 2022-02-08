
package com.l2jserver.datapack.handlers.telnethandlers;

import java.io.PrintWriter;
import java.net.Socket;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.handler.ITelnetHandler;

/**
 * @author UnAfraid
 */
public class ThreadHandler implements ITelnetHandler {
	private final String[] _commands = {
		"purge",
		"performance"
	};
	
	@Override
	public boolean useCommand(String command, PrintWriter _print, Socket _cSocket, int _uptime) {
		if (command.equals("performance")) {
			for (String line : ThreadPoolManager.getInstance().getStats()) {
				_print.println(line);
			}
			_print.flush();
		} else if (command.equals("purge")) {
			ThreadPoolManager.getInstance().purge();
			_print.println("STATUS OF THREAD POOLS AFTER PURGE COMMAND:");
			_print.println("");
			for (String line : ThreadPoolManager.getInstance().getStats()) {
				_print.println(line);
			}
			_print.flush();
		}
		return false;
	}
	
	@Override
	public String[] getCommandList() {
		return _commands;
	}
}
