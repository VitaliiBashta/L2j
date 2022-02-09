
package com.l2jserver.datapack.custom.service.base.util.htmltmpls.funcs;

import java.util.Map;

import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplateFunc;
import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplatePlaceholder;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Include function.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public final class IncludeFunc extends HTMLTemplateFunc {
	public static final IncludeFunc INSTANCE = new IncludeFunc();
	
	private IncludeFunc() {
		super("INC", "ENDINC", true);
	}
	
	@Override
	public Map<String, HTMLTemplatePlaceholder> handle(StringBuilder content, L2PcInstance player, Map<String, HTMLTemplatePlaceholder> placeholders, HTMLTemplateFunc[] funcs) {
		String fileContent = HtmCache.getInstance().getHtm(player != null ? player.getHtmlPrefix() : null, content.toString());
		if (fileContent != null) {
			content.ensureCapacity(fileContent.length());
			content.setLength(0);
			content.append(fileContent);
		}
		return null;
	}
}
