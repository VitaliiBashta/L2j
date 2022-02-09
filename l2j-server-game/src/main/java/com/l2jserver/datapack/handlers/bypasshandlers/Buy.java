
package com.l2jserver.datapack.handlers.bypasshandlers;

import java.util.StringTokenizer;
import java.util.logging.Level;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2MerchantInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class Buy implements IBypassHandler {
	private static final String[] COMMANDS = {
		"Buy"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		if (!(target instanceof L2MerchantInstance)) {
			return false;
		}
		
		try {
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			if (st.countTokens() < 1) {
				return false;
			}
			
			((L2MerchantInstance) target).showBuyWindow(activeChar, Integer.parseInt(st.nextToken()));
			return true;
		} catch (Exception e) {
			_log.log(Level.WARNING, "Exception in " + getClass().getSimpleName(), e);
		}
		return false;
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
