/*
 * Copyright © 2019 L2J Server
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
package com.l2jserver.geodriver.regions;

import static com.l2jserver.geodriver.Block.TYPE_COMPLEX;
import static com.l2jserver.geodriver.Block.TYPE_FLAT;
import static com.l2jserver.geodriver.Block.TYPE_MULTILAYER;

import java.nio.ByteBuffer;

import com.l2jserver.geodriver.Block;
import com.l2jserver.geodriver.Region;
import com.l2jserver.geodriver.blocks.ComplexBlock;
import com.l2jserver.geodriver.blocks.FlatBlock;
import com.l2jserver.geodriver.blocks.MultilayerBlock;

/**
 * Normal region implementation.
 * @author HorridoJoho
 */
public final class NormalRegion implements Region {
	
	private final Block[] _blocks = new Block[Region.REGION_BLOCKS];
	
	public NormalRegion(ByteBuffer bb) {
		for (int blockOffset = 0; blockOffset < Region.REGION_BLOCKS; blockOffset++) {
			int blockType = bb.get();
			switch (blockType) {
				case TYPE_FLAT: {
					_blocks[blockOffset] = new FlatBlock(bb);
					break;
				}
				case TYPE_COMPLEX: {
					_blocks[blockOffset] = new ComplexBlock(bb);
					break;
				}
				case TYPE_MULTILAYER: {
					_blocks[blockOffset] = new MultilayerBlock(bb);
					break;
				}
				default: {
					throw new RuntimeException("Invalid block type " + blockType + "!");
				}
			}
		}
	}
	
	private Block getBlock(int geoX, int geoY) {
		return _blocks[(((geoX / Block.BLOCK_CELLS_X) % Region.REGION_BLOCKS_X) * Region.REGION_BLOCKS_Y) + ((geoY / Block.BLOCK_CELLS_Y) % Region.REGION_BLOCKS_Y)];
	}
	
	@Override
	public boolean checkNearestNswe(int geoX, int geoY, int worldZ, int nswe) {
		return getBlock(geoX, geoY).checkNearestNswe(geoX, geoY, worldZ, nswe);
	}
	
	@Override
	public int getNearestZ(int geoX, int geoY, int worldZ) {
		return getBlock(geoX, geoY).getNearestZ(geoX, geoY, worldZ);
	}
	
	@Override
	public int getNextLowerZ(int geoX, int geoY, int worldZ) {
		return getBlock(geoX, geoY).getNextLowerZ(geoX, geoY, worldZ);
	}
	
	@Override
	public int getNextHigherZ(int geoX, int geoY, int worldZ) {
		return getBlock(geoX, geoY).getNextHigherZ(geoX, geoY, worldZ);
	}
	
	@Override
	public boolean hasGeo() {
		return true;
	}
}
