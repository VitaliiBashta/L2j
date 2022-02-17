package com.l2jserver.gameserver.model.olympiad;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.model.holders.ItemHolder;

import java.util.List;

import static com.l2jserver.gameserver.config.Configuration.olympiad;

public class OlympiadGameClassed extends OlympiadGameNormal {
	private OlympiadGameClassed(int id, Participant[] opponents) {
		super(id, opponents);
	}
	
	@Override
	public final CompetitionType getType() {
		return CompetitionType.CLASSED;
	}
	
	@Override
	protected final int getDivider() {
		return olympiad().getDividerClassed();
	}
	
	@Override
	protected final List<ItemHolder> getReward() {
		return olympiad().getClassedReward();
	}
	
	@Override
	protected final String getWeeklyMatchType() {
		return COMP_DONE_WEEK_CLASSED;
	}
	
	protected static OlympiadGameClassed createGame(int id, List<List<Integer>> classList) {
		if ((classList == null) || classList.isEmpty()) {
			return null;
		}
		
		List<Integer> list;
		Participant[] opponents;
		while (!classList.isEmpty()) {
			list = classList.get(Rnd.nextInt(classList.size()));
			if ((list == null) || (list.size() < 2)) {
				classList.remove(list);
				continue;
			}
			
			opponents = OlympiadGameNormal.createListOfParticipants(list);
			if (opponents == null) {
				classList.remove(list);
				continue;
			}
			
			return new OlympiadGameClassed(id, opponents);
		}
		return null;
	}
}