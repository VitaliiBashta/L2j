package com.l2jserver.mmocore;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_CONNECT;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;

public final class SelectorThread<T extends MMOClient<?>> extends Thread {
	
	// default BYTE_ORDER
	private static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
	// default HEADER_SIZE
	private static final int HEADER_SIZE = 2;
	// Selector
	private final Selector _selector;
	// Implementations
	private final PacketHandler<T> _packetHandler;
	private final MMOExecutor<T> _executor;
	private final ClientFactory<T> _clientFactory;
	private final AcceptFilter _acceptFilter;
	// Configurations
	private final int HELPER_BUFFER_SIZE;
	private final int HELPER_BUFFER_COUNT;
	private final int MAX_SEND_PER_PASS;
	private final int MAX_READ_PER_PASS;
	private final long SLEEP_TIME;
	public boolean TCP_NODELAY;
	// Main Buffers
	private final ByteBuffer DIRECT_WRITE_BUFFER;
	private final ByteBuffer WRITE_BUFFER;
	private final ByteBuffer READ_BUFFER;
	// String Buffer
	private final NioNetStringBuffer STRING_BUFFER;
	// ByteBuffers General Purpose Pool
	private final Queue<ByteBuffer> _bufferPool;
	// Pending Close
	private final NioNetStackList<MMOConnection<T>> _pendingClose;
	
	private boolean _shutdown;
	
	public SelectorThread(SelectorConfig sc, MMOExecutor<T> executor, PacketHandler<T> packetHandler, ClientFactory<T> clientFactory, AcceptFilter acceptFilter) throws IOException {
		super.setName("SelectorThread-" + super.getId());
		
		HELPER_BUFFER_SIZE = sc.HELPER_BUFFER_SIZE;
		HELPER_BUFFER_COUNT = sc.HELPER_BUFFER_COUNT;
		MAX_SEND_PER_PASS = sc.MAX_SEND_PER_PASS;
		MAX_READ_PER_PASS = sc.MAX_READ_PER_PASS;
		SLEEP_TIME = sc.SLEEP_TIME;
		TCP_NODELAY = sc.TCP_NODELAY;
		
		DIRECT_WRITE_BUFFER = ByteBuffer.allocateDirect(sc.WRITE_BUFFER_SIZE).order(BYTE_ORDER);
		WRITE_BUFFER = ByteBuffer.wrap(new byte[sc.WRITE_BUFFER_SIZE]).order(BYTE_ORDER);
		READ_BUFFER = ByteBuffer.wrap(new byte[sc.READ_BUFFER_SIZE]).order(BYTE_ORDER);
		
		STRING_BUFFER = new NioNetStringBuffer(64 * 1024);
		
		_pendingClose = new NioNetStackList<>();
		_bufferPool = new ConcurrentLinkedQueue<>();
		
		for (int i = 0; i < HELPER_BUFFER_COUNT; i++) {
			_bufferPool.add(ByteBuffer.wrap(new byte[HELPER_BUFFER_SIZE]).order(BYTE_ORDER));
		}
		
		_acceptFilter = acceptFilter;
		_packetHandler = packetHandler;
		_clientFactory = clientFactory;
		_executor = executor;
		_selector = Selector.open();
	}
	
	public final void openServerSocket(InetAddress address, int tcpPort) throws IOException {
		ServerSocketChannel selectable = ServerSocketChannel.open();
		selectable.configureBlocking(false);
		
		ServerSocket ss = selectable.socket();
		
		if (address == null) {
			ss.bind(new InetSocketAddress(tcpPort));
		} else {
			ss.bind(new InetSocketAddress(address, tcpPort));
		}
		
		selectable.register(_selector, OP_ACCEPT);
	}
	
	final ByteBuffer getPooledBuffer() {
		if (_bufferPool.isEmpty()) {
			return ByteBuffer.wrap(new byte[HELPER_BUFFER_SIZE]).order(BYTE_ORDER);
		}
		
		return _bufferPool.remove();
	}
	
	final void recycleBuffer(ByteBuffer buf) {
		if (_bufferPool.size() < HELPER_BUFFER_COUNT) {
			buf.clear();
			_bufferPool.add(buf);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final void run() {
		int selectedKeysCount = 0;
		
		while (!_shutdown) {
			try {
				selectedKeysCount = _selector.selectNow();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (selectedKeysCount > 0) {
				var selectedKeys = _selector.selectedKeys().iterator();
				
				while (selectedKeys.hasNext()) {
					SelectionKey key = selectedKeys.next();
					selectedKeys.remove();
					var con = (MMOConnection<T>) key.attachment();
					switch (key.readyOps()) {
						case OP_CONNECT: {
							finishConnection(key, con);
							break;
						}
						case OP_ACCEPT: {
							acceptConnection(key);
							break;
						}
						case OP_READ: {
							readPacket(key, con);
							break;
						}
						case OP_WRITE: {
							writePacket(key, con);
							break;
						}
						case OP_READ | OP_WRITE: {
							writePacket(key, con);
							if (key.isValid()) {
								readPacket(key, con);
							}
							break;
						}
					}
				}
			}
			
			synchronized (_pendingClose) {
				while (!_pendingClose.isEmpty()) {
					try {
						var con = _pendingClose.removeFirst();
						writeClosePacket(con);
						closeConnectionImpl(con.getSelectionKey(), con);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		closeSelectorThread();
	}
	
	private void finishConnection(SelectionKey key, MMOConnection<T> con) {
		try {
			((SocketChannel) key.channel()).finishConnect();
		} catch (IOException e) {
			con.getClient().onForcedDisconnection();
			closeConnectionImpl(key, con);
		}
		
		// key might have been invalidated on finishConnect()
		if (key.isValid()) {
			key.interestOps(key.interestOps() | OP_READ);
			key.interestOps(key.interestOps() & ~OP_CONNECT);
		}
	}
	
	private void acceptConnection(SelectionKey key) {
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		SocketChannel sc;
		
		try {
			while ((sc = ssc.accept()) != null) {
				if ((_acceptFilter == null) || _acceptFilter.accept(sc)) {
					sc.configureBlocking(false);
					SelectionKey clientKey = sc.register(_selector, OP_READ);
					var con = new MMOConnection<>(this, sc.socket(), clientKey, TCP_NODELAY);
					con.setClient(_clientFactory.create(con));
					clientKey.attach(con);
				} else {
					sc.socket().close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readPacket(SelectionKey key, MMOConnection<T> con) {
		if (!con.isClosed()) {
			ByteBuffer buf;
			if ((buf = con.getReadBuffer()) == null) {
				buf = READ_BUFFER;
			}
			
			// if we try to to do a read with no space in the buffer it will
			// read 0 bytes
			// going into infinite loop
			if (buf.position() == buf.limit()) {
				System.exit(0);
			}
			
			int result = -2;
			
			try {
				result = con.read(buf);
			} catch (IOException e) {
				// error handling goes bellow
			}
			
			if (result > 0) {
				buf.flip();
				
				final T client = con.getClient();
				
				for (int i = 0; i < MAX_READ_PER_PASS; i++) {
					if (!tryReadPacket(key, client, buf, con)) {
						return;
					}
				}
				
				// only reachable if MAX_READ_PER_PASS has been reached
				// check if there are some more bytes in buffer
				// and allocate/compact to prevent content lose.
				if (buf.remaining() > 0) {
					// did we use the READ_BUFFER ?
					if (buf == READ_BUFFER) {
						// move the pending byte to the connections READ_BUFFER
						allocateReadBuffer(con);
					} else {
						// move the first byte to the beginning :)
						buf.compact();
					}
				}
			} else {
				switch (result) {
					case 0:
					case -1: {
						closeConnectionImpl(key, con);
						break;
					}
					case -2: {
						con.getClient().onForcedDisconnection();
						closeConnectionImpl(key, con);
						break;
					}
				}
			}
		}
	}
	
	private boolean tryReadPacket(SelectionKey key, T client, ByteBuffer buf, MMOConnection<T> con) {
		switch (buf.remaining()) {
			case 0:
				// buffer is full
				// nothing to read
				return false;
			case 1:
				// we don`t have enough data for header so we need to read
				key.interestOps(key.interestOps() | OP_READ);
				
				// did we use the READ_BUFFER ?
				if (buf == READ_BUFFER) {
					// move the pending byte to the connections READ_BUFFER
					allocateReadBuffer(con);
				} else {
					// move the first byte to the beginning :)
					buf.compact();
				}
				return false;
			default:
				// data size excluding header size :>
				final int dataPending = (buf.getShort() & 0xFFFF) - HEADER_SIZE;
				
				// do we got enough bytes for the packet?
				if (dataPending <= buf.remaining()) {
					// avoid parsing dummy packets (packets without body)
					if (dataPending > 0) {
						final int pos = buf.position();
						parseClientPacket(pos, buf, dataPending, client);
						buf.position(pos + dataPending);
					}
					
					// if we are done with this buffer
					if (!buf.hasRemaining()) {
						if (buf != READ_BUFFER) {
							con.setReadBuffer(null);
							recycleBuffer(buf);
						} else {
							READ_BUFFER.clear();
						}
						return false;
					}
					return true;
				}
				
				// we don`t have enough bytes for the dataPacket so we need to read
				key.interestOps(key.interestOps() | OP_READ);
				
				// did we use the READ_BUFFER ?
				if (buf == READ_BUFFER) {
					// move it`s position
					buf.position(buf.position() - HEADER_SIZE);
					// move the pending byte to the connections READ_BUFFER
					allocateReadBuffer(con);
				} else {
					buf.position(buf.position() - HEADER_SIZE);
					buf.compact();
				}
				return false;
		}
	}
	
	private void allocateReadBuffer(MMOConnection<T> con) {
		con.setReadBuffer(getPooledBuffer().put(READ_BUFFER));
		READ_BUFFER.clear();
	}
	
	private void parseClientPacket(int pos, ByteBuffer buf, int dataSize, T client) {
		final boolean ret = client.decrypt(buf, dataSize);
		
		if (ret && buf.hasRemaining()) {
			// apply limit
			final int limit = buf.limit();
			buf.limit(pos + dataSize);
			final var cp = _packetHandler.handlePacket(buf, client);
			if (cp != null) {
				cp._buf = buf;
				cp._sbuf = STRING_BUFFER;
				cp._client = client;
				
				if (cp.read()) {
					_executor.execute(cp);
				}
				
				cp._buf = null;
				cp._sbuf = null;
			}
			buf.limit(limit);
		}
	}
	
	private void writeClosePacket(MMOConnection<T> con) {
		SendablePacket<T> sp;
		synchronized (con.getSendQueue()) {
			if (con.getSendQueue().isEmpty()) {
				return;
			}
			
			while ((sp = con.getSendQueue().removeFirst()) != null) {
				WRITE_BUFFER.clear();
				
				putPacketIntoWriteBuffer(con.getClient(), sp);
				
				WRITE_BUFFER.flip();
				
				try {
					con.write(WRITE_BUFFER);
				} catch (IOException e) {
					// error handling goes on the if bellow
				}
			}
		}
	}
	
	protected final void writePacket(SelectionKey key, MMOConnection<T> con) {
		if (!prepareWriteBuffer(con)) {
			key.interestOps(key.interestOps() & ~OP_WRITE);
			return;
		}
		
		DIRECT_WRITE_BUFFER.flip();
		
		final int size = DIRECT_WRITE_BUFFER.remaining();
		
		int result = -1;
		
		try {
			result = con.write(DIRECT_WRITE_BUFFER);
		} catch (IOException e) {
			// error handling goes on the if bellow
		}
		
		// check if no error happened
		if (result >= 0) {
			// check if we written everything
			if (result == size) {
				// complete write
				synchronized (con.getSendQueue()) {
					if (con.getSendQueue().isEmpty() && !con.hasPendingWriteBuffer()) {
						key.interestOps(key.interestOps() & ~OP_WRITE);
					}
				}
			} else {
				// incomplete write
				con.createWriteBuffer(DIRECT_WRITE_BUFFER);
			}
		} else {
			con.getClient().onForcedDisconnection();
			closeConnectionImpl(key, con);
		}
	}
	
	private boolean prepareWriteBuffer(MMOConnection<T> con) {
		boolean hasPending = false;
		DIRECT_WRITE_BUFFER.clear();
		
		// if there is pending content add it
		if (con.hasPendingWriteBuffer()) {
			con.movePendingWriteBufferTo(DIRECT_WRITE_BUFFER);
			hasPending = true;
		}
		
		if ((DIRECT_WRITE_BUFFER.remaining() > 1) && !con.hasPendingWriteBuffer()) {
			final NioNetStackList<SendablePacket<T>> sendQueue = con.getSendQueue();
			final T client = con.getClient();
			SendablePacket<T> sp;
			
			for (int i = 0; i < MAX_SEND_PER_PASS; i++) {
				synchronized (con.getSendQueue()) {
					if (sendQueue.isEmpty()) {
						sp = null;
					} else {
						sp = sendQueue.removeFirst();
					}
				}
				
				if (sp == null) {
					break;
				}
				
				hasPending = true;
				
				// put into WriteBuffer
				putPacketIntoWriteBuffer(client, sp);
				
				WRITE_BUFFER.flip();
				
				if (DIRECT_WRITE_BUFFER.remaining() >= WRITE_BUFFER.limit()) {
					DIRECT_WRITE_BUFFER.put(WRITE_BUFFER);
				} else {
					con.createWriteBuffer(WRITE_BUFFER);
					break;
				}
			}
		}
		return hasPending;
	}
	
	private void putPacketIntoWriteBuffer(T client, SendablePacket<T> sp) {
		WRITE_BUFFER.clear();
		
		// reserve space for the size
		final int headerPos = WRITE_BUFFER.position();
		final int dataPos = headerPos + HEADER_SIZE;
		WRITE_BUFFER.position(dataPos);
		
		// set the write buffer
		sp._buf = WRITE_BUFFER;
		// set the client.
		sp._client = client;
		// write content to buffer
		sp.write();
		// delete the write buffer
		sp._buf = null;
		
		// size (inclusive header)
		int dataSize = WRITE_BUFFER.position() - dataPos;
		WRITE_BUFFER.position(dataPos);
		client.encrypt(WRITE_BUFFER, dataSize);
		
		// recalculate size after encryption
		dataSize = WRITE_BUFFER.position() - dataPos;
		
		WRITE_BUFFER.position(headerPos);
		// write header
		WRITE_BUFFER.putShort((short) (dataSize + HEADER_SIZE));
		WRITE_BUFFER.position(dataPos + dataSize);
	}
	
	final void closeConnection(MMOConnection<T> con) {
		synchronized (_pendingClose) {
			_pendingClose.addLast(con);
		}
	}
	
	private void closeConnectionImpl(SelectionKey key, MMOConnection<T> con) {
		try {
			// notify connection
			con.getClient().onDisconnection();
		} finally {
			try {
				// close socket and the SocketChannel
				con.close();
			} catch (IOException e) {
				// ignore, we are closing anyway
			} finally {
				con.releaseBuffers();
				// clear attachment
				key.attach(null);
				// cancel key
				key.cancel();
			}
		}
	}
	
	public final void shutdown() {
		_shutdown = true;
	}
	
	protected void closeSelectorThread() {
		for (final SelectionKey key : _selector.keys()) {
			try {
				key.channel().close();
			} catch (IOException e) {
				// ignore
			}
		}
		
		try {
			_selector.close();
		} catch (IOException e) {
			// Ignore
		}
	}
}
