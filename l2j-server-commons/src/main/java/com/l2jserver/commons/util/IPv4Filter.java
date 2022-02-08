package com.l2jserver.commons.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.mmocore.AcceptFilter;

/**
 * IPv4 filter.
 * @author Forsaiken
 * @author Zoey76
 */
public class IPv4Filter implements AcceptFilter, Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger(IPv4Filter.class);
	
	private static final long SLEEP_TIME = 5000;
	
	private final Map<Integer, Flood> _ipFloodMap;
	
	public IPv4Filter() {
		_ipFloodMap = new HashMap<>();
		Thread t = new Thread(this, getClass().getSimpleName());
		t.setDaemon(true);
		t.start();
	}
	
	private static final int hash(byte[] ip) {
		return (ip[0] & 0xFF) | ((ip[1] << 8) & 0xFF00) | ((ip[2] << 16) & 0xFF0000) | ((ip[3] << 24) & 0xFF000000);
	}
	
	protected static final class Flood {
		long lastAccess;
		int trys;
		
		Flood() {
			lastAccess = System.currentTimeMillis();
			trys = 0;
		}
	}
	
	@Override
	public boolean accept(SocketChannel sc) {
		final InetAddress addr = sc.socket().getInetAddress();
		if (!(addr instanceof Inet4Address)) {
			LOG.warn("Someone tried to connect from something other than IPv4 {}!", addr.getHostAddress());
			return false;
		}
		
		final int h = hash(addr.getAddress());
		long current = System.currentTimeMillis();
		Flood f;
		synchronized (_ipFloodMap) {
			f = _ipFloodMap.get(h);
		}
		if (f != null) {
			if (f.trys == -1) {
				f.lastAccess = current;
				return false;
			}
			
			if ((f.lastAccess + 1000) > current) {
				f.lastAccess = current;
				
				if (f.trys >= 3) {
					f.trys = -1;
					return false;
				}
				
				f.trys++;
			} else {
				f.lastAccess = current;
			}
		} else {
			synchronized (_ipFloodMap) {
				_ipFloodMap.put(h, new Flood());
			}
		}
		
		return true;
	}
	
	@Override
	public void run() {
		while (true) {
			long reference = System.currentTimeMillis() - (1000 * 300);
			synchronized (_ipFloodMap) {
				Iterator<Entry<Integer, Flood>> it = _ipFloodMap.entrySet().iterator();
				while (it.hasNext()) {
					Flood f = it.next().getValue();
					if (f.lastAccess < reference) {
						it.remove();
					}
				}
			}
			
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}