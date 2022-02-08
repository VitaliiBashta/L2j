package com.l2jserver.gameserver.model.clientstrings;

final class BuilderText extends Builder {
  private final String text;

	BuilderText(final String text) {
    this.text = text;
	}
	
	@Override
	public String toString(final Object param) {
		return toString();
	}
	
	@Override
	public final String toString(final Object... params) {
		return toString();
	}
	
	@Override
	public int getIndex() {
		return -1;
	}
	
	@Override
	public String toString() {
    return text;
	}
}