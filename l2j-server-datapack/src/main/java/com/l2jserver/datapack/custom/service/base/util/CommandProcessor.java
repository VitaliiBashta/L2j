
package com.l2jserver.datapack.custom.service.base.util;

import java.util.Objects;

/**
 * Command processor.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public final class CommandProcessor {
	private String remaining;
	
	public CommandProcessor(String command) {
		Objects.requireNonNull(command);
		remaining = command;
	}
	
	public boolean matchAndRemove(String... expectations) {
		Objects.requireNonNull(expectations);
		for (String expectation : expectations) {
			Objects.requireNonNull(expectation);
			if (!expectation.isEmpty() && remaining.startsWith(expectation)) {
				remaining = remaining.substring(expectation.length());
				return true;
			}
		}
		return false;
	}
	
	public String[] splitRemaining(String regex) {
		return remaining.split(regex);
	}
	
	public String getRemaining() {
		return remaining;
	}
}
