
package com.l2jserver.datapack.custom.service.base.util.htmltmpls.funcs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplateFunc;
import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplateParser;
import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplatePlaceholder;
import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplateUtils;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class implements the following function syntax:<br>
 * [FOREACH(alias_placeholder_name IN placeholder_name DO text per iteration)ENDEACH]<br>
 * <br>
 * This construct is able to iterate over the children of the "placeholder_name" placeholder.<br>
 * For each child in this placeholder, the text after "DO" is placed in the content.<br>
 * The current child is placed as an alias toplevel placeholder. This means, in this example<br>
 * you can use %alias_placeholder_name% inside the foreach block.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public final class ForeachFunc extends HTMLTemplateFunc {
	public static final ForeachFunc INSTANCE = new ForeachFunc();
	
	private static final Pattern FIRST_PLACEHOLDER_PATTERN = Pattern.compile("\\s*[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*");
	
	private static final Pattern IN_PATTERN = Pattern.compile("\\s*\\sIN\\s");
	
	private static final Pattern SECOND_PLACEHOLDER_PATTERN = Pattern.compile("\\s*[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*");
	
	private static final Pattern DO_PATTERN = Pattern.compile("\\s*\\sDO\\s");
	
	private ForeachFunc() {
		super("FOREACH", "ENDEACH", false);
	}
	
	@Override
	public Map<String, HTMLTemplatePlaceholder> handle(StringBuilder content, L2PcInstance player, Map<String, HTMLTemplatePlaceholder> placeholders, HTMLTemplateFunc[] funcs) {
		try {
			Matcher matcher = getMatcher(FIRST_PLACEHOLDER_PATTERN, content, 0);
			String aliasPlaceholderName = matcher.group().trim();
			int findIndex = matcher.end();
			
			matcher = getMatcher(IN_PATTERN, content, findIndex);
			findIndex = matcher.end();
			
			matcher = getMatcher(SECOND_PLACEHOLDER_PATTERN, content, findIndex);
			Map<String, HTMLTemplatePlaceholder> childPlaceholders = HTMLTemplateUtils.getPlaceholderChilds(matcher.group().trim(), placeholders);
			findIndex = matcher.end();
			
			matcher = getMatcher(DO_PATTERN, content, findIndex);
			findIndex = matcher.end();
			
			content.delete(0, findIndex);
			HashMap<String, HTMLTemplatePlaceholder> newPlaceholders = new HashMap<>(placeholders);
			StringBuilder orgContent = new StringBuilder(content);
			StringBuilder modContent = new StringBuilder(content.length());
			content.setLength(0);
			// we don't need to save an overwritten placeholder, we create our own map
			for (Entry<String, HTMLTemplatePlaceholder> childPlaceholder : childPlaceholders.entrySet()) {
				modContent.setLength(0);
				modContent.append(orgContent);
				newPlaceholders.put(aliasPlaceholderName, childPlaceholder.getValue().createAlias(aliasPlaceholderName));
				HTMLTemplateParser.fromStringBuilder(modContent, player, newPlaceholders, funcs);
				content.append(modContent);
			}
		} catch (Exception ex) {
			content.setLength(0);
		}
		return null;
	}
	
	private static Matcher getMatcher(Pattern pattern, StringBuilder content, int findIndex) throws Exception {
		Matcher m = pattern.matcher(content);
		if (!m.find(findIndex) || (m.start() > findIndex)) {
			throw new Exception();
		}
		return m;
	}
}
