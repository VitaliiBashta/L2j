package com.l2jserver.gameserver.instancemanager.tasks;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jserver.gameserver.Context;
import com.l2jserver.gameserver.instancemanager.MailManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Message;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;


public class MessageDeletionTask implements Runnable {
	private static final Logger _log = Logger.getLogger(MessageDeletionTask.class.getName());
	
	final int _msgId;
	public MessageDeletionTask(int msgId) {
		_msgId = msgId;
	}
	
	@Override
	public void run() {
		final Message msg = MailManager.getInstance().getMessage(_msgId);
		if (msg == null) {
			return;
		}
		
		if (msg.hasAttachments()) {
			try {
				final L2PcInstance sender = L2World.getInstance().getPlayer(msg.getSenderId());
				if (sender != null) {
					msg.getAttachments().returnToWh(sender.getWarehouse());
					sender.sendPacket(SystemMessageId.MAIL_RETURNED);
				} else {
					msg.getAttachments().returnToWh(null);
				}
				
				msg.getAttachments().deleteMe();
				msg.removeAttachments();
				
				final L2PcInstance receiver = L2World.getInstance().getPlayer(msg.getReceiverId());
				if (receiver != null) {
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.MAIL_RETURNED);
					// sm.addString(msg.getReceiverName());
					receiver.sendPacket(sm);
				}
			} catch (Exception e) {
				_log.log(Level.WARNING, getClass().getSimpleName() + ": Error returning items:" + e.getMessage(), e);
			}
		}
		MailManager.getInstance().deleteMessageInDb(msg.getId());
	}
}
