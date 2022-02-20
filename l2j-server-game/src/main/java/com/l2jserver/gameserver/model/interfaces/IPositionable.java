package com.l2jserver.gameserver.model.interfaces;

import com.l2jserver.gameserver.model.Location;


public interface IPositionable extends ILocational {
	/**
	 * Sets the X coordinate of this object.
	 * @param x the new X coordinate
	 */
	void setX(int x);
	
	/**
	 * Sets the Y coordinate of this object.
	 * @param y the new Y coordinate
	 */
	void setY(int y);
	
	/**
	 * Sets the Z coordinate of this object.
	 * @param z the new Z coordinate
	 */
	void setZ(int z);
	
	/**
	 * Sets all three coordinates of this object.
	 * @param x the new X coordinate
	 * @param y the new Y coordinate
	 * @param z the new Z coordinate
	 */
	void setXYZ(int x, int y, int z);
	
	/**
	 * Sets all three coordinates of this object.
	 * @param loc the object whose coordinates to use
	 */
	void setXYZ(ILocational loc);
	
	/**
	 * Sets the heading of this object.
	 * @param heading the new heading
	 */
	void setHeading(int heading);
	
	/**
	 * Changes the instance zone ID of this object.
	 * @param instanceId the ID of the instance zone to put this object in (0 - not in any instance)
	 */
	void setInstanceId(int instanceId);
	
	/**
	 * Changes the location of this object.
	 * @param loc the new location
	 */
	void setLocation(Location loc);
}