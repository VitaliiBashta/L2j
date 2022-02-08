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
package com.l2jserver.commons.util;

import java.util.Arrays;

/**
 * Hex utils.
 * 
 * @author HorridoJoho
 * @version 2.6.1.0
 */
public class HexUtils {
	// lookup table for hex characters
	private static final char[] NIBBLE_CHAR_LOOKUP = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
			'D', 'E', 'F' };
	public static final char[] NEW_LINE_CHARS = System.getProperty("line.separator").toCharArray();
	private static final char[] EMPTY_CHAR_ARRAY = new char[0];

	/**
	 * Method to generate the hexadecimal character presentation of a byte<br>
	 * This call is equivalent to {@link HexUtils#b2HexChars(byte, char[], int)}
	 * with parameters (data, null, 0)
	 * 
	 * @param data byte to generate the hexadecimal character presentation from
	 * @return a new char array with exactly 2 elements
	 */
	public static char[] b2HexChars(final byte data) {
		return b2HexChars(data, null, 0);
	}

	/**
	 * Method to generate the hexadecimal character presentation of a byte
	 * 
	 * @param data        byte to generate the hexadecimal character presentation
	 *                    from
	 * @param dstHexChars the char array the hexadecimal character presentation
	 *                    should be copied to, if this is null, dstOffset is ignored
	 *                    and a new char array with 2 elements is created
	 * @param dstOffset   offset at which the hexadecimal character presentation is
	 *                    copied to dstHexChars
	 * @return the char array the hexadecimal character presentation was copied to
	 */
	public static char[] b2HexChars(final byte data, char[] dstHexChars, int dstOffset) {
		if (dstHexChars == null) {
			dstHexChars = new char[2];
			dstOffset = 0;
		}

		// /////////////////////////////
		// NIBBLE LOOKUP
		dstHexChars[dstOffset] = NIBBLE_CHAR_LOOKUP[(data & 0xF0) >> 4];
		dstHexChars[dstOffset + 1] = NIBBLE_CHAR_LOOKUP[data & 0x0F];

		return dstHexChars;
	}

	/**
	 * Method to generate the hexadecimal character presentation of an integer This
	 * call is equivalent to {@link HexUtils#int2HexChars(int, char[], int)} with
	 * parameters (data, null, 0)
	 * 
	 * @param data integer to generate the hexadecimal character presentation from
	 * @return new char array with 8 elements
	 */
	public static char[] int2HexChars(final int data) {
		return int2HexChars(data, new char[8], 0);
	}

	/**
	 * Method to generate the hexadecimal character presentation of an integer
	 * 
	 * @param data        integer to generate the hexadecimal character presentation
	 *                    from
	 * @param dstHexChars the char array the hexadecimal character presentation
	 *                    should be copied to, if this is null, dstOffset is ignored
	 *                    and a new char array with 8 elements is created
	 * @param dstOffset   offset at which the hexadecimal character presentation is
	 *                    copied to dstHexChars
	 * @return the char array the hexadecimal character presentation was copied to
	 */
	public static char[] int2HexChars(final int data, char[] dstHexChars, int dstOffset) {
		if (dstHexChars == null) {
			dstHexChars = new char[8];
			dstOffset = 0;
		}

		b2HexChars((byte) ((data & 0xFF000000) >> 24), dstHexChars, dstOffset);
		b2HexChars((byte) ((data & 0x00FF0000) >> 16), dstHexChars, dstOffset + 2);
		b2HexChars((byte) ((data & 0x0000FF00) >> 8), dstHexChars, dstOffset + 4);
		b2HexChars((byte) (data & 0x000000FF), dstHexChars, dstOffset + 6);
		return dstHexChars;
	}

	/**
	 * Method to generate the hexadecimal character presentation of a byte array<br>
	 * This call is equivalent to
	 * {@link HexUtils#bArr2HexChars(byte[], int, int, char[], int)} with parameters
	 * (data, offset, len, null, 0)
	 * 
	 * @param data   byte array to generate the hexadecimal character presentation
	 *               from
	 * @param offset offset where to start in data array
	 * @param len    number of bytes to generate the hexadecimal character
	 *               presentation from
	 * @return a new char array with len*2 elements
	 */
	public static char[] bArr2HexChars(final byte[] data, final int offset, final int len) {
		return bArr2HexChars(data, offset, len, null, 0);
	}

	/**
	 * Method to generate the hexadecimal character presentation of a byte array
	 * 
	 * @param data        byte array to generate the hexadecimal character
	 *                    presentation from
	 * @param offset      offset where to start in data array
	 * @param len         number of bytes to generate the hexadecimal character
	 *                    presentation from
	 * @param dstHexChars the char array the hexadecimal character presentation
	 *                    should be copied to, if this is null, dstOffset is ignored
	 *                    and a new char array with len*2 elements is created
	 * @param dstOffset   offset at which the hexadecimal character presentation is
	 *                    copied to dstHexChars
	 * @return the char array the hexadecimal character presentation was copied to
	 */
	public static char[] bArr2HexChars(final byte[] data, final int offset, final int len, char[] dstHexChars,
			int dstOffset) {
		if (dstHexChars == null) {
			dstHexChars = new char[len * 2];
			dstOffset = 0;
		}

		for (int dataIdx = offset, charsIdx = dstOffset; dataIdx < (len + offset); ++dataIdx, ++charsIdx) {
			// /////////////////////////////
			// NIBBLE LOOKUP, we duplicate the code from b2HexChars here, we want to save a
			// few cycles(for charsIdx increment)
			dstHexChars[charsIdx] = NIBBLE_CHAR_LOOKUP[(data[dataIdx] & 0xF0) >> 4];
			dstHexChars[++charsIdx] = NIBBLE_CHAR_LOOKUP[data[dataIdx] & 0x0F];
		}

		return dstHexChars;
	}

	public static char[] bArr2AsciiChars(byte[] data, final int offset, final int len) {
		return bArr2AsciiChars(data, offset, len, new char[len], 0);
	}

	public static char[] bArr2AsciiChars(byte[] data, final int offset, final int len, char[] dstAsciiChars,
			int dstOffset) {
		if (dstAsciiChars == null) {
			dstAsciiChars = new char[len];
			dstOffset = 0;
		}

		for (int dataIdx = offset, charsIdx = dstOffset; dataIdx < (len + offset); ++dataIdx, ++charsIdx) {
			if ((data[dataIdx] > 0x1f) && (data[dataIdx] < 0x80)) {
				dstAsciiChars[charsIdx] = (char) data[dataIdx];
			} else {
				dstAsciiChars[charsIdx] = '.';
			}
		}

		return dstAsciiChars;
	}

	private static final int HEX_ED_BPL = 16;
	private static final int HEX_ED_CPB = 2;
	// {OFFSET} {HEXADECIMAL} {ASCII}{NEWLINE}
	private static final int HEX_ED_CPL = 9 + (HEX_ED_BPL * HEX_ED_CPB) + 1 + HEX_ED_BPL + NEW_LINE_CHARS.length;

	/**
	 * Method to generate the hexadecimal character representation of a byte array
	 * like in a hex editor<br>
	 * Line Format: {OFFSET} {HEXADECIMAL} {ASCII}({NEWLINE})<br>
	 * {OFFSET} = offset of the first byte in line(8 chars)<br>
	 * {HEXADECIMAL} = hexadecimal character representation({@link #HEX_ED_BPL}*2
	 * chars)<br>
	 * {ASCII} = ascii character presentation({@link #HEX_ED_BPL} chars)
	 * 
	 * @param data byte array to generate the hexadecimal character representation
	 * @param len  the number of bytes to generate the hexadecimal character
	 *             representation from
	 * @return byte array which contains the hexadecimal character representation of
	 *         the given byte array
	 */
	public static char[] bArr2HexEdChars(byte[] data, int len) {
		if (len == 0) {
			return EMPTY_CHAR_ARRAY;
		}

		final int lenBplMod = len % HEX_ED_BPL;

		int numLines;
		char[] textData;
		if (lenBplMod == 0) {
			numLines = len / HEX_ED_BPL;
			textData = new char[(HEX_ED_CPL * numLines) - NEW_LINE_CHARS.length];
		} else {
			numLines = (len / HEX_ED_BPL) + 1;
			textData = new char[(HEX_ED_CPL * numLines) - (HEX_ED_BPL - (lenBplMod)) - NEW_LINE_CHARS.length];
		}

		int dataOffset;
		int dataLen;
		int lineStart;
		int lineHexDataStart;
		int lineAsciiDataStart;
		for (int i = 0; i < numLines; ++i) {
			dataOffset = i * HEX_ED_BPL;
			dataLen = Math.min(len - dataOffset, HEX_ED_BPL);
			lineStart = i * HEX_ED_CPL;
			lineHexDataStart = lineStart + 9;
			lineAsciiDataStart = lineHexDataStart + (HEX_ED_BPL * HEX_ED_CPB) + 1;

			// the offset of this line
			int2HexChars(dataOffset, textData, lineStart);
			// separation spacing
			textData[lineHexDataStart - 1] = ' ';
			// the data in hex
			bArr2HexChars(data, dataOffset, dataLen, textData, lineHexDataStart);
			// the data in ascii
			bArr2AsciiChars(data, dataOffset, dataLen, textData, lineAsciiDataStart);

			if (i < (numLines - 1)) {
				// separation spacing
				textData[lineAsciiDataStart - 1] = ' ';
				// the new line
				System.arraycopy(NEW_LINE_CHARS, 0, textData, lineAsciiDataStart + HEX_ED_BPL, NEW_LINE_CHARS.length); 
			} else if (dataLen < HEX_ED_BPL) {
				// last line which shows less than _HEX_ED_BPL bytes
				int lineHexDataEnd = lineHexDataStart + (dataLen * HEX_ED_CPB);
				// spaces, for the last line if there are not _HEX_ED_BPL bytes
				Arrays.fill(textData, lineHexDataEnd, lineHexDataEnd + ((HEX_ED_BPL - dataLen) * HEX_ED_CPB) + 1, ' ');
			} else {
				// last line which shows _HEX_ED_BPL bytes
				textData[lineAsciiDataStart - 1] = ' '; // separate
			}
		}
		return textData;
	}
}
