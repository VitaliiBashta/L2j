package com.l2jserver.gameserver.config.converter;

import org.aeonbits.owner.Converter;
import org.apache.logging.log4j.util.Strings;

import java.lang.reflect.Method;
import java.math.BigInteger;

public class HexIdConverter implements Converter<BigInteger> {
	
	@Override
	public BigInteger convert(Method method, String input) {
		if (Strings.isBlank(input)) {
			return null;
		}
		return new BigInteger(input, 16);
	}
}
