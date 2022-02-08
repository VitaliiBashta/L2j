/*
 * Copyright © 2004-2021 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.commons;

import static com.l2jserver.commons.util.HexUtils.NEW_LINE_CHARS;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Iterator;
import java.util.LinkedList;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.l2jserver.commons.util.HexUtils;

/**
 * HexUtil test.
 * @author HorridoJoho
 * @version 2.6.5.0
 */
public class HexUtilsTest {
	
	@ParameterizedTest
	@MethodSource("provideArgs")
	public void testHexEdOutput(byte[] data, char[] expected) {
		assertArrayEquals(expected, HexUtils.bArr2HexEdChars(data, data.length));
	}
	
	private static Iterator<Object[]> provideArgs() {
		// @formatter:off
		final var result = new LinkedList<Object[]>();
		result.add(new Object[] { new byte[0], new char[0] });
		result.add(new Object[] { new byte[] { 0x42 }, "00000000 42                               B".toCharArray() });
		result.add(new Object[] { new byte[] { 0x42, 0x4D, 0x5E, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x36, 0x00, 0x00, 0x00, 0x28, 0x00 },
				"00000000 424D5E00000000000000360000002800 BM^.......6...(.".toCharArray() });
		result.add(new Object[] { new byte[] { 0x42, 0x4D, 0x5E, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x36, 0x00, 0x00, 0x00, 0x28, 0x00, 0x00 },
				("00000000 424D5E00000000000000360000002800 BM^.......6...(." + new String(NEW_LINE_CHARS) + "00000010 00                               .").toCharArray() });
		result.add(new Object[] { new byte[] { 0x42, 0x4D, 0x5E, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x36, 0x00, 0x00, 0x00, 0x28,
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xB7, 0x59, 0x71, (byte) 0xFF, (byte) 0xB7, 0x59, 0x71, (byte) 0xFF, (byte) 0xB7, 0x59 },
				("00000000 424D5E00000000000000360000002800 BM^.......6...(." + new String(NEW_LINE_CHARS) + "00000010 000000000000B75971FFB75971FFB759 .......Yq..Yq..Y").toCharArray() });
		// @formatter:on
		return result.iterator();
	}
}
