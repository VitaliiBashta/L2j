
package com.l2jserver.datapack.custom.service.base.util.htmltmpls;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * HTML Template Utils.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public final class HTMLTemplateUtils {
	public static int findSequenceEnd(StringBuilder string, int startOffset, HTMLTemplateFunc func) {
		int dept = 0;
		String seqStart = func.getSequenceStart();
		String seqEnd = func.getSequenceEnd();
		String escapedSeqStart = "\\" + seqStart;
		String escapedSeqEnd = "\\" + seqEnd;
		
		while (true) {
			int endSeqOffset = string.indexOf(seqEnd, startOffset);
			if (endSeqOffset == -1) {
				return -1; // there is no sequence end to find
			}
			
			int escapedSeqStartOffset = string.indexOf(escapedSeqStart, startOffset);
			int escapedSeqEndOffset = string.indexOf(escapedSeqEnd, startOffset);
			int startSeqOffset = string.indexOf(seqStart, startOffset);
			
			if (((endSeqOffset < startSeqOffset) || (startSeqOffset == -1)) && ((endSeqOffset < escapedSeqStartOffset) || (escapedSeqStartOffset == -1)) && ((endSeqOffset < escapedSeqEndOffset) || (escapedSeqEndOffset == -1))) {
				if (dept == 0) {
					return endSeqOffset + seqEnd.length();
				}
				
				--dept;
				startOffset = endSeqOffset + seqEnd.length();
			} else if ((startSeqOffset != -1) && (startSeqOffset < endSeqOffset) && ((startSeqOffset < escapedSeqStartOffset) || (escapedSeqStartOffset == -1)) && ((startSeqOffset < escapedSeqEndOffset) || (escapedSeqEndOffset == -1))) {
				startOffset = startSeqOffset + seqStart.length();
				++dept;
			} else if ((escapedSeqStartOffset != -1) && ((escapedSeqStartOffset < escapedSeqEndOffset) || (escapedSeqEndOffset == -1))) {
				startOffset += escapedSeqStartOffset + escapedSeqStart.length();
			} else if ((escapedSeqEndOffset != -1) && ((escapedSeqEndOffset < escapedSeqStartOffset) || (escapedSeqStartOffset != -1))) {
				startOffset += escapedSeqEndOffset + escapedSeqEnd.length();
			}
		}
	}
	
	/**
	 * Searches the placeholder specified by placeholderString inside the placeholders map
	 * @param placeholderString placeholder to search for
	 * @param placeholders map with the placeholders available
	 * @return the placeholder if found, null otherwise
	 */
	public static HTMLTemplatePlaceholder getPlaceholder(String placeholderString, Map<String, HTMLTemplatePlaceholder> placeholders) {
		if (placeholders == null) {
			return null;
		}
		
		String[] placeholderParts = placeholderString.split(Pattern.quote("."));
		HTMLTemplatePlaceholder placeholder = null;
		for (String placeholderPart : placeholderParts) {
			if (placeholder == null) {
				placeholder = placeholders.get(placeholderPart);
				if (placeholder == null) {
					break;
				}
			} else {
				placeholder = placeholder.getChild(placeholderPart);
				if (placeholder == null) {
					break;
				}
			}
		}
		return placeholder;
	}
	
	/**
	 * Get the value of the placeholder specified by placeholderString
	 * @param placeholderString the placeholder to get the value from
	 * @param placeholders placeholder map to search in
	 * @return the value of the found placeholder
	 * @throws Exception the placeholder was not found or the value is null
	 */
	public static String getPlaceholderValue(String placeholderString, Map<String, HTMLTemplatePlaceholder> placeholders) throws Exception {
		HTMLTemplatePlaceholder placeholder = getPlaceholder(placeholderString, placeholders);
		if (placeholder == null) {
			throw new Exception();
		}
		
		String value = placeholder.getValue();
		if (value == null) {
			throw new Exception();
		}
		
		return value;
	}
	
	/**
	 * Get the childs of the placeholder specified by placeholderString in an unmodifyable map
	 * @param placeholderString the placeholder to get the childs from
	 * @param placeholders placeholder map to search in
	 * @return the childs in an unmodifyable map of the found placeholder
	 * @throws Exception the placeholder was not found
	 */
	public static Map<String, HTMLTemplatePlaceholder> getPlaceholderChilds(String placeholderString, Map<String, HTMLTemplatePlaceholder> placeholders) throws Exception {
		HTMLTemplatePlaceholder placeholder = getPlaceholder(placeholderString, placeholders);
		if (placeholder == null) {
			throw new Exception();
		}
		return placeholder.getChildren();
	}
}
