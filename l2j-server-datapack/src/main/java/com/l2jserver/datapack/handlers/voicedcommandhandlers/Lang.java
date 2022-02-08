
package com.l2jserver.datapack.handlers.voicedcommandhandlers;

import static com.l2jserver.gameserver.config.Configuration.customs;

import java.util.StringTokenizer;

import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.StringUtil;

public class Lang implements IVoicedCommandHandler {
	private static final String[] VOICED_COMMANDS = {
		"lang"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params) {
		if (!customs().multiLangEnable() || !customs().multiLangVoiceCommand()) {
			return false;
		}
		
		final NpcHtmlMessage msg = new NpcHtmlMessage();
		if (params == null) {
			final StringBuilder html = StringUtil.startAppend(100);
			for (String lang : customs().getMultiLangAllowed()) {
				StringUtil.append(html, "<button value=\"", lang.toUpperCase(), "\" action=\"bypass -h voice .lang ", lang, "\" width=60 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>");
			}
			
			msg.setFile(activeChar.getHtmlPrefix(), "data/html/mods/Lang/LanguageSelect.htm");
			msg.replace("%list%", html.toString());
			activeChar.sendPacket(msg);
			return true;
		}
		
		final StringTokenizer st = new StringTokenizer(params);
		if (st.hasMoreTokens()) {
			final String lang = st.nextToken().trim();
			if (activeChar.setLang(lang)) {
				msg.setFile(activeChar.getHtmlPrefix(), "data/html/mods/Lang/Ok.htm");
				activeChar.sendPacket(msg);
				return true;
			}
			msg.setFile(activeChar.getHtmlPrefix(), "data/html/mods/Lang/Error.htm");
			activeChar.sendPacket(msg);
			return true;
		}
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList() {
		return VOICED_COMMANDS;
	}
}