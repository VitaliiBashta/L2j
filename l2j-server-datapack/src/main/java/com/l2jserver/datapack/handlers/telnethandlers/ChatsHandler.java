
package com.l2jserver.datapack.handlers.telnethandlers;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.handler.ITelnetHandler;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.util.Broadcast;

/**
 * @author UnAfraid
 */
public class ChatsHandler implements ITelnetHandler {
	private final String[] _commands = {
		"announce",
		"msg",
		"gmchat"
	};
	
	@Override
	public boolean useCommand(String command, PrintWriter _print, Socket _cSocket, int _uptime) {
		if (command.startsWith("announce")) {
			try {
				command = command.substring(9);
				Broadcast.toAllOnlinePlayers(command);
				_print.println("Announcement Sent!");
			} catch (StringIndexOutOfBoundsException e) {
				_print.println("Please Enter Some Text To Announce!");
			}
		} else if (command.startsWith("msg")) {
			try {
				String val = command.substring(4);
				StringTokenizer st = new StringTokenizer(val);
				String name = st.nextToken();
				String message = val.substring(name.length() + 1);
				L2PcInstance reciever = L2World.getInstance().getPlayer(name);
				CreatureSay cs = new CreatureSay(0, Say2.TELL, "Telnet Priv", message);
				if (reciever != null) {
					reciever.sendPacket(cs);
					_print.println("Telnet Priv->" + name + ": " + message);
					_print.println("Message Sent!");
				} else {
					_print.println("Unable To Find Username: " + name);
				}
			} catch (StringIndexOutOfBoundsException e) {
				_print.println("Please Enter Some Text!");
			}
		} else if (command.startsWith("gmchat")) {
			try {
				command = command.substring(7);
				CreatureSay cs = new CreatureSay(0, Say2.ALLIANCE, "Telnet GM Broadcast from " + _cSocket.getInetAddress().getHostAddress(), command);
				AdminData.getInstance().broadcastToGMs(cs);
				_print.println("Your Message Has Been Sent To " + getOnlineGMS() + " GM(s).");
			} catch (StringIndexOutOfBoundsException e) {
				_print.println("Please Enter Some Text To Announce!");
			}
		}
		return false;
	}
	
	private int getOnlineGMS() {
		return AdminData.getInstance().getAllGms(true).size();
	}
	
	@Override
	public String[] getCommandList() {
		return _commands;
	}
}
