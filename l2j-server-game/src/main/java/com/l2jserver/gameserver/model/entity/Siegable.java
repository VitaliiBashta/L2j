package com.l2jserver.gameserver.model.entity;

import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.L2SiegeClan;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.Calendar;
import java.util.List;

public interface Siegable {
	void startSiege();
	
	void endSiege();
	
	L2SiegeClan getAttackerClan(int clanId);
	
	L2SiegeClan getAttackerClan(L2Clan clan);
	
	List<L2SiegeClan> getAttackerClans();
	
	List<L2PcInstance> getAttackersInZone();
	
	boolean checkIsAttacker(L2Clan clan);
	
	L2SiegeClan getDefenderClan(int clanId);
	
	L2SiegeClan getDefenderClan(L2Clan clan);
	
	List<L2SiegeClan> getDefenderClans();
	
	boolean checkIsDefender(L2Clan clan);
	
	List<L2Npc> getFlag(L2Clan clan);
	
	Calendar getSiegeDate();
	
	boolean giveFame();
	
	int getFameFrequency();
	
	int getFameAmount();
	
	void updateSiege();
}
