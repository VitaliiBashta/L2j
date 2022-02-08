package com.l2jserver.commons.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.nio.charset.StandardCharsets.UTF_16LE;


public abstract class BaseRecievePacket {
	
	private static final Logger LOG = LogManager.getLogger(BaseRecievePacket.class);
	
	private final byte[] _decrypt;
	
	private int _off;
	
	public BaseRecievePacket(byte[] decrypt) {
		_decrypt = decrypt;
		_off = 1; // skip packet type id
	}
	
	public int readD() {
		int result = _decrypt[_off++] & 0xff;
		result |= (_decrypt[_off++] << 8) & 0xff00;
		result |= (_decrypt[_off++] << 0x10) & 0xff0000;
		result |= (_decrypt[_off++] << 0x18) & 0xff000000;
		return result;
	}
	
	public int readC() {
		return _decrypt[_off++] & 0xff;
	}
	
	public int readH() {
		int result = _decrypt[_off++] & 0xff;
		result |= (_decrypt[_off++] << 8) & 0xff00;
		return result;
	}
	
	public double readF() {
		long result = _decrypt[_off++] & 0xff;
		result |= (_decrypt[_off++] & 0xffL) << 8L;
		result |= (_decrypt[_off++] & 0xffL) << 16L;
		result |= (_decrypt[_off++] & 0xffL) << 24L;
		result |= (_decrypt[_off++] & 0xffL) << 32L;
		result |= (_decrypt[_off++] & 0xffL) << 40L;
		result |= (_decrypt[_off++] & 0xffL) << 48L;
		result |= (_decrypt[_off++] & 0xffL) << 56L;
		return Double.longBitsToDouble(result);
	}
	
	public String readS() {
		String result = null;
		try {
			result = new String(_decrypt, _off, _decrypt.length - _off, UTF_16LE);
			result = result.substring(0, result.indexOf(0x00));
			_off += (result.length() * 2) + 2;
		} catch (Exception ex) {
			LOG.warn("There has been an error trying to read an String!", ex);
		}
		return result;
	}
	
	public final byte[] readB(int length) {
		byte[] result = new byte[length];
		System.arraycopy(_decrypt, _off, result, 0, length);
		_off += length;
		return result;
	}
	
	public long readQ() {
		long result = _decrypt[_off++] & 0xff;
		result |= (_decrypt[_off++] & 0xffL) << 8L;
		result |= (_decrypt[_off++] & 0xffL) << 16L;
		result |= (_decrypt[_off++] & 0xffL) << 24L;
		result |= (_decrypt[_off++] & 0xffL) << 32L;
		result |= (_decrypt[_off++] & 0xffL) << 40L;
		result |= (_decrypt[_off++] & 0xffL) << 48L;
		result |= (_decrypt[_off++] & 0xffL) << 56L;
		return result;
	}
}
