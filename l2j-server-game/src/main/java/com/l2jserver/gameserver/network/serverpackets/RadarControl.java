package com.l2jserver.gameserver.network.serverpackets;

public class RadarControl extends L2GameServerPacket {
	private final int _showRadar;
	private final int _type;
	private final int _x;
	private final int _y;
	private final int _z;
	
	public RadarControl(int showRadar, int type, int x, int y, int z) {
		_showRadar = showRadar; // show radar?? 0 = show radar; 1 = delete radar;
		_type = type; // radar type??
		_x = x;
		_y = y;
		_z = z;
	}
	
	@Override
	protected final void writeImpl() {
		writeC(0xf1);
		writeD(_showRadar);
		writeD(_type); // maybe type
		writeD(_x);
		writeD(_y);
		writeD(_z);
	}
}
