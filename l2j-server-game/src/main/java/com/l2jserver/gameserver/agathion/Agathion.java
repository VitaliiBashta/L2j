package com.l2jserver.gameserver.agathion;

public class Agathion {

	private final int npcId;

	private final int id;

	private final int itemId;

	private final int energy;

	private final int maxEnergy;

	public Agathion(int npcId, int id, int itemId, int energy, int maxEnergy) {
		this.npcId = npcId;
		this.id = id;
		this.itemId = itemId;
		this.energy = energy;
		this.maxEnergy = maxEnergy;
	}

	public int getNpcId() {
		return npcId;
	}

	public int getId() {
		return id;
	}

	public int getItemId() {
		return itemId;
	}

	public int getEnergy() {
		return energy;
	}

	public int getMaxEnergy() {
		return maxEnergy;
	}
}
