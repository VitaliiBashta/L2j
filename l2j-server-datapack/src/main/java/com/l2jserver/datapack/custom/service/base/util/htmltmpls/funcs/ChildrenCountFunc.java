
package com.l2jserver.datapack.custom.service.base.util.htmltmpls.funcs;

import java.util.Map;

import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplateFunc;
import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplatePlaceholder;
import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplateUtils;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Children Count function.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public final class ChildrenCountFunc extends HTMLTemplateFunc {
	public static final ChildrenCountFunc INSTANCE = new ChildrenCountFunc();
	
	private ChildrenCountFunc() {
		super("CHILDSCOUNT", "ENDCHILDSCOUNT", false);
	}
	
	@Override
	public Map<String, HTMLTemplatePlaceholder> handle(StringBuilder content, L2PcInstance player, Map<String, HTMLTemplatePlaceholder> placeholders, HTMLTemplateFunc[] funcs) {
		HTMLTemplatePlaceholder placeholder = HTMLTemplateUtils.getPlaceholder(content.toString(), placeholders);
		content.setLength(0);
		if (placeholder != null) {
			content.append(placeholder.getChildrenSize());
		}
		return null;
	}
}
