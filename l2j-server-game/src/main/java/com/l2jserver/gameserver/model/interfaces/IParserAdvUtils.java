package com.l2jserver.gameserver.model.interfaces;

/**
 * More advanced interface for parsers.<br>
 * Allows usage of get methods without fall back value.<br>
 * @author xban1x
 */
public interface IParserAdvUtils extends IParserUtils {
	
	boolean getBoolean(String key);
	
	byte getByte(String key);
	
	short getShort(String key);
	
	int getInt(String key);
	
	long getLong(String key);
	
	float getFloat(String key);
	
	double getDouble(String key);
	
	String getString(String key);
	
	<T extends Enum<T>> T getEnum(String key, Class<T> clazz);
}
